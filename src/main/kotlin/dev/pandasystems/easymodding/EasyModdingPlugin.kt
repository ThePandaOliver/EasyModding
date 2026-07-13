package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.loader.fabric.GenerateFabricMetadataTask
import dev.pandasystems.easymodding.loader.neoforge.GenerateNeoForgeMetadataTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import kotlin.jvm.java

open class EasyModdingPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		val easyModdingExtension = target.extensions.create("easyModding", EasyModdingExtension::class.java)
		val platform = target.findProperty("easy_modding.platform") as? String

		when (platform) {
			"fabric" -> target.pluginManager.apply("dev.pandasystems.easymodding.fabric")
			"neoforge" -> target.pluginManager.apply("dev.pandasystems.easymodding.neoforge")
			null -> {} // Ignore
			else -> throw IllegalArgumentException("Unknown platform: $platform (Available: fabric, neoforge)")
		}
	}
}