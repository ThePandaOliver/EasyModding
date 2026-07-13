package dev.pandasystems.easymodding.loader.neoforge

import dev.pandasystems.easymodding.loader.BaseEasyModdingPlugin
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Project

class EasyModdingNeoForgePlugin : BaseEasyModdingPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.neoforged.moddev")
		val extension = target.easyModding
		val neoForgeExtension = target.extensions.getByType(NeoForgeExtension::class.java)

		extension.neoForge.neoForgeVersion.orNull?.let { neoForgeExtension.version = it }
	}
}