package dev.pandasystems.easymodding.extensions

import org.gradle.api.provider.Property

abstract class FabricExtension : LoaderExtension {
	abstract override val enabled: Property<Boolean>
}