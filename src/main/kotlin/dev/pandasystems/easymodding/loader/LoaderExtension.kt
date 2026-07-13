package dev.pandasystems.easymodding.loader

import org.gradle.api.provider.Property

interface LoaderExtension {
	val enabled: Property<Boolean>
}