package dev.pandasystems.easymodding.loader.fabric

import dev.pandasystems.easymodding.EasyModdingExtension
import dev.pandasystems.easymodding.EasyModdingPlugin
import org.gradle.api.Project

class EasyModdingFabricPlugin : EasyModdingPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.fabricmc.fabric-loom")
		val extension = target.extensions.getByType(EasyModdingExtension::class.java)

		target.beforeEvaluate {
			dependencies.add("minecraft", "com.mojang:minecraft:${extension.minecraftVersion.get()}")
		}
	}
}