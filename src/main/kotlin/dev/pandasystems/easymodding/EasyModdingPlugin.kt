package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.tasks.GenerateFabricMetadataTask
import dev.pandasystems.easymodding.tasks.GenerateNeoForgeMetadataTask
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
			"fabric" -> target.pluginManager.apply("dev.pandasystems.easymodding.fabric")
			"neoforge" -> target.pluginManager.apply("dev.pandasystems.easymodding.neoforge")
			null -> {} // Ignore
			else -> throw IllegalArgumentException("Unknown platform: $platform (Available: fabric, neoforge)")
		}

		val generateFabricMetadata =
			target.tasks.register("generateFabricMetadata", GenerateFabricMetadataTask::class.java) {
				configFile.convention(easyModdingExtension.configPath)
				outputFile.convention(target.layout.buildDirectory.file("generated/easy-modding/metadata/fabric.mod.json"))
				onlyIf { easyModdingExtension.fabric.enabled.get() }
			}

		val generateNeoForgeMetadata =
			target.tasks.register("generateNeoForgeMetadata", GenerateNeoForgeMetadataTask::class.java) {
				configFile.convention(easyModdingExtension.configPath)
				outputFile.convention(target.layout.buildDirectory.file("generated/easy-modding/metadata/neoforge.mod.toml"))
				onlyIf { easyModdingExtension.neoForge.enabled.get() }
			}

		target.tasks.named("processResources", ProcessResources::class.java) {
			from(generateFabricMetadata)
			from(generateNeoForgeMetadata)
		}
	}
}