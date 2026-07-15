package dev.pandasystems.easymodding.extensions

import org.gradle.api.provider.Property

/**
 * Common contract for a loader-specific configuration block (Fabric, NeoForge, ...).
 *
 * The [enabled] flag drives whether the corresponding metadata-generation task actually runs
 * (see the `onlyIf` guards in [dev.pandasystems.easymodding.EasyModdingPlugin]).
 */
interface LoaderExtension {
	/** Whether this loader is enabled for the current build. */
	val enabled: Property<Boolean>
}