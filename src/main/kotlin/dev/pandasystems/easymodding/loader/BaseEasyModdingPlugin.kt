package dev.pandasystems.easymodding.loader

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.TaskProvider

abstract class BaseEasyModdingPlugin : Plugin<Project> {
	override fun apply(target: Project) {
	}
}