package dev.pandasystems.easymodding.loader.fabric

import dev.pandasystems.easymodding.EasyModdingExtension
import dev.pandasystems.easymodding.loader.BaseEasyModdingPlugin
import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import dev.pandasystems.easymodding.loader.neoforge.GenerateNeoForgeMetadataTask
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.TaskProvider
import kotlin.jvm.java

class EasyModdingFabricPlugin : BaseEasyModdingPlugin() {
	override fun applyPlugins(pluginManager: PluginManager) {
		pluginManager.apply("det.fabricmc.fabric-loom")
	}

	override fun registerMetadataTask(project: Project): TaskProvider<out GenerateMetadataTask> {
		return project.tasks.register("generateMetadata", GenerateNeoForgeMetadataTask::class.java) {
			group = "easy-modding"
			description = "Generates the neoforge.mods.toml metadata file"

			extension.convention(project.extensions.getByType(EasyModdingExtension::class.java))
			outputDirectory.convention(project.layout.buildDirectory.dir("generated/easy-modding"))
		}
	}
}