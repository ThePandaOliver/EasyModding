package dev.pandasystems.easymodding.extensions

import org.gradle.api.provider.Property

interface LoaderExtension {
	val enabled: Property<Boolean>
}