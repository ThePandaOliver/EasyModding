package dev.pandasystems.easymodding.platform.loom

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import org.gradle.api.Project

/**
 * Platform plugin for Fabric, backed by Fabric Loom (`net.fabricmc.fabric-loom`).
 *
 * Registered as `dev.pandasystems.easymodding.loom-noremap` and applied by the main plugin when
 * `easy_modding.platform=loom-noremap`. It applies Loom and immediately declares the Minecraft
 * dependency using the version from the shared `easyModding` extension.
 * 
 * This is the no-remap variant of Fabric Loom.
 */
class EasyModdingLoomNoremapPlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.fabricmc.fabric-loom")
		val extension = target.easyModding

		target.dependencies.add("minecraft", "com.mojang:minecraft:${extension.minecraftVersion.get()}")
	}
}
