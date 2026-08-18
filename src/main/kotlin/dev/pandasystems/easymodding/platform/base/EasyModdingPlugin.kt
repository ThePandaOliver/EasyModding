package dev.pandasystems.easymodding.platform.base

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class EasyModdingPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		val extension = target.extensions.create("easyModding", EasyModdingExtension::class.java)
	}
}
