package dev.pandasystems.easymodding.loader.fabric

import dev.pandasystems.easymodding.EasyModdingExtension
import dev.pandasystems.easymodding.loader.BaseEasyModdingPlugin
import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.TaskProvider

class EasyModdingFabricPlugin : BaseEasyModdingPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.fabricmc.fabric-loom")

		super.apply(target)

		val extension = target.extensions.getByType(EasyModdingExtension::class.java)

		target.beforeEvaluate {
			dependencies.add("minecraft", "com.mojang:minecraft:${extension.minecraftVersion.get()}")
		}
	}

	override fun registerMetadataTask(project: Project): TaskProvider<out GenerateMetadataTask> {
		return project.tasks.register("generateMetadata", GenerateFabricMetadataTask::class.java) {
			group = "easy-modding"
			description = "Generates the fabric.mod.json metadata file"

			extension.convention(project.extensions.getByType(EasyModdingExtension::class.java))
			outputDirectory.convention(project.layout.buildDirectory.dir("generated/easy-modding"))
		}
	}
}