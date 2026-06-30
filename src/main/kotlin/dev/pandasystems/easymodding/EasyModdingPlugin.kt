package dev.pandasystems.easymodding

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

class EasyModdingPlugin : Plugin<Project> {
    override fun apply(target: Project) {
		when (target.extra["easy_modding.platform"]) {
			"loom" -> target.pluginManager.apply("dev.pandasystems.easymodding.fabric")
			"moddev" -> target.pluginManager.apply("dev.pandasystems.easymodding.neoforge")
			else -> throw IllegalArgumentException("Invalid platform: ${target.extra["easy_modding.platform"]} (Available platforms are: loom and moddev)")
		}

		target.extensions.create("easyModding", EasyModdingExtension::class.java)
    }
}