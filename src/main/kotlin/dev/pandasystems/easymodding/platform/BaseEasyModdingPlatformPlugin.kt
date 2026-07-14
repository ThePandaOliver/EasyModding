package dev.pandasystems.easymodding.platform

import dev.pandasystems.easymodding.extensions.EasyModdingExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class BaseEasyModdingPlatformPlugin : Plugin<Project> {
	protected val Project.easyModding: EasyModdingExtension
		get() = extensions.getByType(EasyModdingExtension::class.java)
}