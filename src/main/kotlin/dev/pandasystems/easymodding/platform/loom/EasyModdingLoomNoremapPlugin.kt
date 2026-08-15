package dev.pandasystems.easymodding.platform.loom

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import org.gradle.api.Project

class EasyModdingLoomNoremapPlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.fabricmc.fabric-loom")
		val extension = target.easyModding

		target.dependencies.add(
			"minecraft",
			extension.minecraftVersion.map { version ->
				"com.mojang:minecraft:$version"
			},
		)
	}
}
