package dev.pandasystems.easymodding.loader

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.TaskProvider

abstract class BaseEasyModdingPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		applyPlugins(target.pluginManager)

		val generateMetadataTask = registerMetadataTask(target)

		target.plugins.withId("java") {
			val javaExtension = target.extensions.getByType(JavaPluginExtension::class.java)
			val mainSourceSet = javaExtension.sourceSets.getByName("main")

			mainSourceSet.resources.srcDir(generateMetadataTask.flatMap { it.outputDirectory })
		}
	}

	abstract fun applyPlugins(pluginManager: PluginManager)

	abstract fun registerMetadataTask(project: Project): TaskProvider<out GenerateMetadataTask>
}