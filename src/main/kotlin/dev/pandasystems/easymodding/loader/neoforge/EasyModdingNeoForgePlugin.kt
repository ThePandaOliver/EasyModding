package dev.pandasystems.easymodding.loader.neoforge

import dev.pandasystems.easymodding.EasyModdingExtension
import dev.pandasystems.easymodding.EasyModdingPlugin
import dev.pandasystems.easymodding.loader.fabric.GenerateFabricMetadataTask
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources
import kotlin.jvm.java

class EasyModdingNeoForgePlugin : EasyModdingPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.neoforged.moddev")
		val extension = target.extensions.getByType(EasyModdingExtension::class.java)
		val neoForgeExtension = target.extensions.getByType(NeoForgeExtension::class.java)

		extension.neoForge.neoForgeVersion.orNull?.let { neoForgeExtension.version = it }

		val generateMetadataTask =
			target.tasks.register("generateMetadata", GenerateNeoForgeMetadataTask::class.java) {
				config.convention(extension.config)
				outputFile.convention(target.layout.buildDirectory.file("generated/easy-modding/neoforge.mods.toml"))
				enabled = extension.neoForge.enabled.get()
			}

		target.tasks.named("processResources", ProcessResources::class.java) {
			from(target.provider {
				if (extension.neoForge.enabled.get()) generateMetadataTask else emptyList<Any>()
			})
		}
	}
}