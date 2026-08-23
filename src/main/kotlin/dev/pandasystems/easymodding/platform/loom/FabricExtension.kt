package dev.pandasystems.easymodding.platform.loom

import dev.pandasystems.easymodding.platform.base.LoaderExtension
import org.gradle.api.provider.Property

abstract class FabricExtension : LoaderExtension {
	abstract override val enabled: Property<Boolean>
}
