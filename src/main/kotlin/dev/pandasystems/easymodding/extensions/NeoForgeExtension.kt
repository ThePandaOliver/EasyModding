package dev.pandasystems.easymodding.extensions

import org.gradle.api.provider.Property

/**
 * NeoForge loader configuration block (`easyModding { neoForge { } }`).
 *
 * Enabling it triggers generation of `neoforge.mods.toml` and `pack.mcmeta`. The [neoForgeVersion]
 * is forwarded to NeoForged ModDev by
 * [dev.pandasystems.easymodding.platform.moddev.EasyModdingModdevPlugin].
 */
abstract class NeoForgeExtension : LoaderExtension {
	abstract override val enabled: Property<Boolean>

	/** The NeoForge version to build against (e.g. `"21.1.0"`). */
	abstract val neoForgeVersion: Property<String>
}