package dev.pandasystems.easymodding.loader.fabric

import dev.pandasystems.easymodding.loader.LoaderExtension
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class FabricExtension @Inject constructor(
	private val objects: ObjectFactory,
) : LoaderExtension {
	override val enabled = objects.property<Boolean>().convention(false)
}