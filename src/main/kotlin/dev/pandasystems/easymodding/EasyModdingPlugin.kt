package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.tasks.GenerateMetadataTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources
import kotlin.jvm.java

open class EasyModdingPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		val easyModdingExtension = target.extensions.create("easyModding", EasyModdingExtension::class.java)
		val platform = target.findProperty("easy_modding.platform") as? String

		target.pluginManager.apply("java")
		target.pluginManager.apply("idea")

		when (platform) {
			"fabric" -> target.pluginManager.apply("dev.pandasystems.easymodding.fabric")
			"neoforge" -> target.pluginManager.apply("dev.pandasystems.easymodding.neoforge")
			null -> {} // Ignore
			else -> throw IllegalArgumentException("Unknown platform: $platform (Available: fabric, neoforge)")
		}

		val generateMetadataTask =
			target.tasks.register("generateMetadata", GenerateMetadataTask::class.java) {
				config.convention(easyModdingExtension.config)
				outputDirectory.convention(target.layout.buildDirectory.dir("generated/easy-modding/metadata"))
				enableFabric.convention(easyModdingExtension.fabric.enabled)
				enableNeoForge.convention(easyModdingExtension.neoForge.enabled)
			}

		target.tasks.named("processResources", ProcessResources::class.java) {
			from(target.provider { generateMetadataTask })
		}
	}
}