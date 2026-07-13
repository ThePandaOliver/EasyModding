package dev.pandasystems.easymodding.loader.neoforge

import dev.pandasystems.easymodding.loader.LoaderExtension
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class NeoForgeExtension @Inject constructor(
	private val objects: ObjectFactory,
) : LoaderExtension {
	override val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)

	val neoForgeVersion: Property<String> = objects.property<String>()
}