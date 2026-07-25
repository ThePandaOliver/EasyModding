package dev.pandasystems.easymodding.platform.loom

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Project

/**
 * Platform plugin for Fabric, backed by Fabric Loom Remap (`net.fabricmc.fabric-loom-remap`).
 *
 * Registered as `dev.pandasystems.easymodding.loom-remap` and applied by the main plugin when
 * `easy_modding.platform=loom-remap`. It applies Loom Remap and immediately declares the Minecraft
 * dependency and wires in the official Mojang mappings using the shared `easyModding` extension.
 *
 * Use this platform when you need the remap variant of Fabric Loom.
 */
class EasyModdingLoomRemapPlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.fabricmc.fabric-loom-remap")
		val extension = target.easyModding
		val loom = target.extensions.getByType(LoomGradleExtensionAPI::class.java)

		target.dependencies.add("minecraft", "com.mojang:minecraft:${extension.minecraftVersion.get()}")
		target.dependencies.add("mappings", loom.officialMojangMappings())
	}
}
