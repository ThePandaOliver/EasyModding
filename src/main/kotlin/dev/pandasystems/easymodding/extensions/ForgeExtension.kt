package dev.pandasystems.easymodding.extensions

import org.gradle.api.provider.Property

/**
 * Forge loader configuration block (`easyModding { forge { } }`).
 *
 * Enabling it triggers generation of `mods.toml` and `pack.mcmeta`. The [forgeVersion] is
 * forwarded to ForgeGradle by
 * [dev.pandasystems.easymodding.platform.forgegradle.EasyModdingForgeGradlePlugin] to build the
 * `net.minecraftforge:forge:<minecraftVersion>-<forgeVersion>` dependency notation.
 */
abstract class ForgeExtension : LoaderExtension {
	abstract override val enabled: Property<Boolean>

	/** The Forge version to build against (e.g. `"47.2.0"`). */
	abstract val forgeVersion: Property<String>
}
