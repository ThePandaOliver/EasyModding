package dev.pandasystems.easymodding.platform.loom

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Project

/**
 * Platform plugin for Fabric, backed by Fabric Loom Remap (`net.fabricmc.fabric-loom-remap`).
 *
 * Registered as `dev.pandasystems.easymodding.loom-remap` and applied automatically by the main
 * plugin when `easy_modding.platform=loom-remap`. It applies Loom Remap and, once the project is
 * fully configured, declares the Minecraft dependency and wires in the official Mojang mappings
 * using the version from the shared `easyModding` extension.
 *
 * Use this instead of the `loom` platform when you need the remap variant of Fabric Loom.
 */
class EasyModdingLoomRemapPlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.fabricmc.fabric-loom-remap")
		val extension = target.easyModding

		// Deferred until after evaluation so `minecraftVersion` set inside the `easyModding { }`
		// block has been populated before we read it.
		target.afterEvaluate {
			val loom = extensions.getByType(LoomGradleExtensionAPI::class.java)

			dependencies.add("minecraft", "com.mojang:minecraft:${extension.minecraftVersion.get()}")
			dependencies.add("mappings", loom.officialMojangMappings())
		}
	}
}
