package dev.pandasystems.easymodding.platform

import org.gradle.api.provider.Property

interface LoaderExtension {
	val enabled: Property<Boolean>
}