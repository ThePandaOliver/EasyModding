package dev.pandasystems.easymodding.platform.moddev

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Project

/**
 * Platform plugin for NeoForge, backed by NeoForged ModDev (`net.neoforged.moddev`).
 *
 * Registered as `dev.pandasystems.easymodding.moddev` and applied automatically by the main plugin
 * when `easy_modding.platform=neoforge`. It applies ModDev and forwards the NeoForge version from
 * the shared `easyModding` extension to ModDev's own [NeoForgeExtension].
 *
 * Note: [net.neoforged.moddevgradle.dsl.NeoForgeExtension] is ModDev's extension and is distinct
 * from EasyModding's own [dev.pandasystems.easymodding.extensions.NeoForgeExtension].
 */
class EasyModdingModdevPlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.neoforged.moddev")
		val extension = target.easyModding
		val neoForgeExtension = target.extensions.getByType(NeoForgeExtension::class.java)

		// Only override the ModDev version when the user actually specified one via
		// `easyModding { neoForge { neoForgeVersion.set(...) } }`.
		extension.neoForge.neoForgeVersion.orNull?.let { neoForgeExtension.version = it }
	}
}