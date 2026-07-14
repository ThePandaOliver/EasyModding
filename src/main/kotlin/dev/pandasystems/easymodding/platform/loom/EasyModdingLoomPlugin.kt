package dev.pandasystems.easymodding.platform.loom

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import org.gradle.api.Project

class EasyModdingLoomPlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.fabricmc.fabric-loom")
		val extension = target.easyModding

		target.afterEvaluate {
			dependencies.add("minecraft", "com.mojang:minecraft:${extension.minecraftVersion.get()}")
		}
	}
}