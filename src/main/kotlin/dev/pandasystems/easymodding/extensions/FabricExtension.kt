package dev.pandasystems.easymodding.extensions

import dev.pandasystems.easymodding.platform.LoaderExtension
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class FabricExtension @Inject constructor(
	private val objects: ObjectFactory,
) : LoaderExtension {
	override val enabled = objects.property<Boolean>().convention(false)
}