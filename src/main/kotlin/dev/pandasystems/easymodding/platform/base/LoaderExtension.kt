package dev.pandasystems.easymodding.platform.base

import org.gradle.api.provider.Property

interface LoaderExtension {

	val enabled: Property<Boolean>
}
