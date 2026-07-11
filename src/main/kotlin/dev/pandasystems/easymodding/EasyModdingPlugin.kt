package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.loader.fabric.GenerateFabricMetadataTask
import dev.pandasystems.easymodding.loader.neoforge.GenerateNeoForgeMetadataTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import kotlin.jvm.java

open class EasyModdingPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		val easyModdingExtension = target.extensions.create("easyModding", EasyModdingExtension::class.java)

		val generateFabricMetadataTask =
			target.tasks.register("generateFabricMetadata", GenerateFabricMetadataTask::class.java) {
				config.convention(easyModdingExtension.config)
				outputDirectory.convention(target.layout.buildDirectory.dir("generated/easy-modding/resources"))
				enabled = easyModdingExtension.fabric.enabledMetadataGeneration.get()
			}
		val generateNeoForgeMetadataTask =
			target.tasks.register("generateNeoForgeMetadata", GenerateNeoForgeMetadataTask::class.java) {
				config.convention(easyModdingExtension.config)
				outputDirectory.convention(target.layout.buildDirectory.dir("generated/easy-modding"))
				enabled = easyModdingExtension.neoForge.enabledMetadataGeneration.get()
			}
	}
}