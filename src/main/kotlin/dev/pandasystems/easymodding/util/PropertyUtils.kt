package dev.pandasystems.easymodding.util

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.property

inline fun <reified T : Any> Property<T>.setOrElseCurrent(objects: ObjectFactory, override: Provider<T>) {
	val fallback = objects.property<T>().value(this)
	set(override.orElse(fallback))
}

fun DirectoryProperty.setOrElseCurrent(objects: ObjectFactory, override: DirectoryProperty) {
	val fallback = objects.directoryProperty().value(this)
	set(override.orElse(fallback))
}

fun RegularFileProperty.setOrElseCurrent(objects: ObjectFactory, override: RegularFileProperty) {
	val fallback = objects.fileProperty().value(this)
	set(override.orElse(fallback))
}