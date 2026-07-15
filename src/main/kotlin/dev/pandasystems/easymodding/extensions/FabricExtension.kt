package dev.pandasystems.easymodding.extensions

import org.gradle.api.provider.Property

/**
 * Fabric loader configuration block (`easyModding { fabric { } }`).
 *
 * Currently only exposes the [enabled] flag inherited from [LoaderExtension]; enabling it triggers
 * generation of `fabric.mod.json`.
 */
abstract class FabricExtension : LoaderExtension {
	abstract override val enabled: Property<Boolean>
}