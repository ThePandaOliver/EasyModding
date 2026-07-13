package dev.pandasystems.easymodding.loader.neoforge

import dev.pandasystems.easymodding.EasyModdingExtension
import dev.pandasystems.easymodding.EasyModdingPlugin
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Project

class EasyModdingNeoForgePlugin : EasyModdingPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.neoforged.moddev")
		val extension = target.extensions.getByType(EasyModdingExtension::class.java)
		val neoForgeExtension = target.extensions.getByType(NeoForgeExtension::class.java)

		extension.neoForge.neoForgeVersion.orNull?.let { neoForgeExtension.version = it }
	}
}