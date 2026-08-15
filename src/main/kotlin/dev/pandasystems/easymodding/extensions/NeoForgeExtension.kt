package dev.pandasystems.easymodding.extensions

import org.gradle.api.provider.Property

abstract class NeoForgeExtension : LoaderExtension {
	abstract override val enabled: Property<Boolean>


	abstract val neoForgeVersion: Property<String>
}
