package dev.pandasystems.easymodding.platform.loom

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import org.gradle.api.Project

/**
 * Platform plugin for Fabric, backed by Fabric Loom (`net.fabricmc.fabric-loom`).
 *
 * Registered as `dev.pandasystems.easymodding.loom` and applied automatically by the main plugin
 * when `easy_modding.platform=fabric`. It applies Loom and, once the project is fully configured,
 * declares the Minecraft dependency using the version from the shared `easyModding` extension.
 */
class EasyModdingLoomPlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.fabricmc.fabric-loom")
		val extension = target.easyModding

		// Deferred until after evaluation so `minecraftVersion` set inside the `easyModding { }`
		// block has been populated before we read it.
		target.afterEvaluate {
			dependencies.add("minecraft", "com.mojang:minecraft:${extension.minecraftVersion.get()}")
		}
	}
}