package dev.pandasystems.easymodding.platform.moddev

import dev.pandasystems.easymodding.platform.base.LoaderExtension
import org.gradle.api.provider.Property

abstract class NeoForgeExtension : LoaderExtension {
	abstract override val enabled: Property<Boolean>


	abstract val neoForgeVersion: Property<String>
}
