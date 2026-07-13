package dev.pandasystems.easymodding.loader.fabric

import dev.pandasystems.easymodding.loader.BaseEasyModdingPlugin
import org.gradle.api.Project

class EasyModdingFabricPlugin : BaseEasyModdingPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.fabricmc.fabric-loom")
		val extension = target.easyModding

		target.afterEvaluate {
			dependencies.add("minecraft", "com.mojang:minecraft:${extension.minecraftVersion.get()}")
		}
	}
}