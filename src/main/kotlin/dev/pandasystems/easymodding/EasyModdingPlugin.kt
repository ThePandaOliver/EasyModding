package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.loader.fabric.GenerateFabricMetadataTask
import dev.pandasystems.easymodding.loader.neoforge.GenerateNeoForgeMetadataTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.internal.extensions.core.extra
import kotlin.jvm.java

class EasyModdingPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		val easyModdingExtension = target.extensions.create("easyModding", EasyModdingExtension::class.java)

		when (target.findProperty("easy_modding.platform")) {
			"loom" -> target.pluginManager.apply("dev.pandasystems.easymodding.fabric")
			"moddevgradle" -> target.pluginManager.apply("dev.pandasystems.easymodding.neoforge")
			null -> {} // Ignore

			else -> throw IllegalArgumentException("Invalid platform: ${target.findProperty("easy_modding.platform")} (Available platforms are: loom, moddevgradle)")
		}

		val generateFabricMetadataTask = target.tasks.register("generateFabricMetadata", GenerateFabricMetadataTask::class.java) {
			extension.convention(easyModdingExtension)
			outputDirectory.convention(target.layout.buildDirectory.dir("generated/easy-modding/fabric"))
			enabled = easyModdingExtension.fabric.enabledMetadataGeneration.get()
		}
		val generateNeoForgeMetadataTask = target.tasks.register("generateNeoForgeMetadata", GenerateNeoForgeMetadataTask::class.java) {
			extension.convention(easyModdingExtension)
			outputDirectory.convention(target.layout.buildDirectory.dir("generated/easy-modding/neoforge"))
			enabled = easyModdingExtension.neoForge.enabledMetadataGeneration.get()
		}

		target.plugins.withId("java") {
			val javaExtension = target.extensions.getByType(JavaPluginExtension::class.java)
			val mainSourceSet = javaExtension.sourceSets.getByName("main")

			mainSourceSet.resources.srcDir(generateFabricMetadataTask.flatMap { it.outputDirectory })
			mainSourceSet.resources.srcDir(generateNeoForgeMetadataTask.flatMap { it.outputDirectory })
		}
	}
}