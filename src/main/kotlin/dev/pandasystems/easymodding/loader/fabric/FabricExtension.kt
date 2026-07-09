package dev.pandasystems.easymodding.loader.fabric

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class FabricExtension @Inject constructor(
	private val objects: ObjectFactory,
) {
	val enabled = objects.property<Boolean>().convention(false)
}