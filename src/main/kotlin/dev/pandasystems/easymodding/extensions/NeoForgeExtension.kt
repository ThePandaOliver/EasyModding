package dev.pandasystems.easymodding.extensions

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class NeoForgeExtension : LoaderExtension {
	abstract override val enabled: Property<Boolean>
	abstract val neoForgeVersion: Property<String>
}