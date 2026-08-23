package dev.pandasystems.easymodding.platform.forgegradle

import dev.pandasystems.easymodding.platform.base.LoaderExtension
import org.gradle.api.provider.Property

abstract class ForgeExtension : LoaderExtension {
	abstract override val enabled: Property<Boolean>


	abstract val forgeVersion: Property<String>
}
