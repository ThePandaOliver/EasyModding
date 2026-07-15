package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.extensions.EasyModdingExtension
import dev.pandasystems.easymodding.tasks.GenerateFabricResourcesTask
import dev.pandasystems.easymodding.tasks.GenerateNeoForgeResourcesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources

/**
 * The primary EasyModding Gradle plugin (`dev.pandasystems.easymodding`).
 *
 * This plugin is the single entry point for a multiloader Minecraft mod project. It acts as a thin
 * orchestrator that:
 *
 *  1. Registers the [EasyModdingExtension] (`easyModding { }`) DSL used to configure the mod.
 *  2. Reads the `easy_modding.platform` Gradle property to decide which loader-specific sub-plugin
 *     to apply (Fabric Loom or NeoForge ModDev). This lets a single set of build scripts target
 *     different platforms simply by switching the property.
 *  3. Registers the resource-generation tasks that translate the unified `easymodding.mod.json`
 *     config into the loader-native metadata files, and wires them into `processResources` so they
 *     run automatically as part of a normal build.
 *
 * The actual loader integration (applying Loom/ModDev, resolving Minecraft/NeoForge versions) is
 * delegated to the platform sub-plugins in the `platform` package.
 */
class EasyModdingPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		// Register the `easyModding { }` configuration DSL on the project.
		val easyModdingExtension = target.extensions.create("easyModding", EasyModdingExtension::class.java)

		// The selected build platform, provided by the consumer (e.g. in gradle.properties as
		// `easy_modding.platform=fabric`). May be null when the project is not building for a
		// specific loader (for example a shared "common" subproject).
		val platform = target.findProperty("easy_modding.platform") as? String

		// Every EasyModding project is a Java project and benefits from IDEA integration.
		target.pluginManager.apply("java")
		target.pluginManager.apply("idea")

		// Apply the loader-specific sub-plugin based on the selected platform.
		when (platform) {
			"fabric" -> target.pluginManager.apply("dev.pandasystems.easymodding.loom")
			"neoforge" -> target.pluginManager.apply("dev.pandasystems.easymodding.moddev")
			null -> {} // No platform selected: skip loader wiring (e.g. a common/shared module).
			else -> throw IllegalArgumentException("Unknown platform: $platform (Available: loom, moddev)")
		}

		// Task that generates the Fabric `fabric.mod.json`. Only runs when the Fabric loader is
		// enabled via the `easyModding { fabric() }` DSL.
		val generateFabricMetadata =
			target.tasks.register("generateFabricResources", GenerateFabricResourcesTask::class.java) {
				configFile.convention(easyModdingExtension.configPath)
				outputDir.convention(target.layout.buildDirectory.dir("generated/easy-modding/fabric/resources"))
				onlyIf { easyModdingExtension.fabric.enabled.get() }
			}

		// Task that generates the NeoForge `neoforge.mods.toml` and `pack.mcmeta`. Only runs when
		// the NeoForge loader is enabled via the `easyModding { neoForge() }` DSL.
		val generateNeoForgeMetadata =
			target.tasks.register("generateNeoForgeResources", GenerateNeoForgeResourcesTask::class.java) {
				configFile.convention(easyModdingExtension.configPath)
				outputDir.convention(target.layout.buildDirectory.dir("generated/easy-modding/neoforge/resources"))
				onlyIf { easyModdingExtension.neoForge.enabled.get() }
			}

		// Feed the generated metadata into the standard resource-processing pipeline so the files
		// end up on the mod's classpath / inside the built jar automatically.
		target.tasks.named("processResources", ProcessResources::class.java) {
			from(generateFabricMetadata)
			from(generateNeoForgeMetadata)
		}
	}
}