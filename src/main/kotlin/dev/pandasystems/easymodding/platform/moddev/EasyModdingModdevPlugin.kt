package dev.pandasystems.easymodding.platform.moddev

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Project

class EasyModdingModdevPlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.neoforged.moddev")
		val extension = target.easyModding
		val neoForgeExtension = target.extensions.getByType(NeoForgeExtension::class.java)

		extension.neoForge.neoForgeVersion.orNull?.let { neoForgeExtension.version = it }
	}
}