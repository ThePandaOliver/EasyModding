package dev.pandasystems.easymodding.platform.loom

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import org.gradle.api.Project

/**
 * Platform plugin for Fabric that automatically selects the appropriate Loom variant based on the
 * Minecraft version.
 *
 * Registered as `dev.pandasystems.easymodding.loom` and applied automatically by the main plugin
 * when `easy_modding.platform=loom`. It detects the Minecraft version from the shared `easyModding`
 * extension and:
 *  - For Minecraft <= 1.21.11: applies `dev.pandasystems.easymodding.loom-remap` (with Mojang mappings)
 *  - For Minecraft > 1.21.11: applies `dev.pandasystems.easymodding.loom-noremap` (no-remap variant)
 */
class EasyModdingLoomPlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		val extension = target.easyModding

		// Deferred until after evaluation so `minecraftVersion` set inside the `easyModding { }`
		// block has been populated before we read it.
		target.afterEvaluate {
			val minecraftVersion = extension.minecraftVersion.get()
			val useRemap = shouldUseRemap(target, minecraftVersion)

			if (useRemap) {
				target.logger.info("EasyModding: Minecraft version $minecraftVersion <= 1.21.11, applying loom-remap")
				target.pluginManager.apply("dev.pandasystems.easymodding.loom-remap")
			} else {
				target.logger.info("EasyModding: Minecraft version $minecraftVersion > 1.21.11, applying loom-noremap")
				target.pluginManager.apply("dev.pandasystems.easymodding.loom-noremap")
			}
		}
	}

	/**
	 * Determines whether to use the remap variant based on the Minecraft version.
	 * Returns true if the version is 1.21.11 or below, false otherwise.
	 */
	private fun shouldUseRemap(target: Project, version: String): Boolean {
		return try {
			val parts = version.split(".")
			if (parts.size < 2) return false

			val major = parts[0].toIntOrNull() ?: return false
			val minor = parts[1].toIntOrNull() ?: return false
			val patch = if (parts.size >= 3) parts[2].toIntOrNull() ?: 0 else 0

			// Compare against 1.21.11
			when {
				major < 1 -> true
				major > 1 -> false
				minor < 21 -> true
				minor > 21 -> false
				patch <= 11 -> true  // Changed from < 11 to <= 11
				else -> false
			}
		} catch (e: Exception) {
			// If we can't parse the version, default to remap for safety
			target.logger.warn("EasyModding: Failed to parse Minecraft version '$version', defaulting to loom-remap")
			true
		}
	}
}
