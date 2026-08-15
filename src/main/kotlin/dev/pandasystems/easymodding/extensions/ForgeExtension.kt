package dev.pandasystems.easymodding.extensions

import org.gradle.api.provider.Property

abstract class ForgeExtension : LoaderExtension {
	abstract override val enabled: Property<Boolean>


	abstract val forgeVersion: Property<String>
}
