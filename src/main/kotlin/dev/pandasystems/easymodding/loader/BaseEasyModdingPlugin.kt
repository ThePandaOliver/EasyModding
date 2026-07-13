package dev.pandasystems.easymodding.loader

import dev.pandasystems.easymodding.EasyModdingExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class BaseEasyModdingPlugin : Plugin<Project> {
	protected val Project.easyModding: EasyModdingExtension
		get() = extensions.getByType(EasyModdingExtension::class.java)
}