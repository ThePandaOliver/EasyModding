package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.extensions.EasyModdingExtension
import dev.pandasystems.easymodding.tasks.GenerateFabricResourcesTask
import dev.pandasystems.easymodding.tasks.GenerateNeoForgeResourcesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources

class EasyModdingPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		val easyModdingExtension = target.extensions.create("easyModding", EasyModdingExtension::class.java)
		val platform = target.findProperty("easy_modding.platform") as? String

		target.pluginManager.apply("java")
		target.pluginManager.apply("idea")

		when (platform) {
			"fabric" -> target.pluginManager.apply("dev.pandasystems.easymodding.loom")
			"neoforge" -> target.pluginManager.apply("dev.pandasystems.easymodding.moddev")
			null -> {} // Ignore
			else -> throw IllegalArgumentException("Unknown platform: $platform (Available: loom, moddev)")
		}

		val generateFabricMetadata =
			target.tasks.register("GenerateFabricResources", GenerateFabricResourcesTask::class.java) {
				configFile.convention(easyModdingExtension.configPath)
				outputDir.convention(target.layout.buildDirectory.dir("generated/easy-modding/fabric/resources"))
				onlyIf { easyModdingExtension.fabric.enabled.get() }
			}

		val generateNeoForgeMetadata =
			target.tasks.register("GenerateNeoForgeResources", GenerateNeoForgeResourcesTask::class.java) {
				configFile.convention(easyModdingExtension.configPath)
				outputDir.convention(target.layout.buildDirectory.dir("generated/easy-modding/neoforge/resources"))
				onlyIf { easyModdingExtension.neoForge.enabled.get() }
			}

		target.tasks.named("processResources", ProcessResources::class.java) {
			from(generateFabricMetadata)
			from(generateNeoForgeMetadata) { into("META-INF") }
		}
	}
}