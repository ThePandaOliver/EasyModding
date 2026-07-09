package dev.pandasystems.easymodding

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

class EasyModdingPlugin : Plugin<Project> {
    override fun apply(target: Project) {
		val extension = target.extensions.create("easyModding", EasyModdingExtension::class.java)

		when (target.extra["easy_modding.platform"]) {
			"loom" -> target.pluginManager.apply("dev.pandasystems.easymodding.fabric")
			else -> throw IllegalArgumentException("Invalid platform: ${target.extra["easy_modding.platform"]} (Available platforms are: loom)")
		}
    }
}