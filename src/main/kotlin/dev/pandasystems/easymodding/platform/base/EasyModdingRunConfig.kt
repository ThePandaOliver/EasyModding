package dev.pandasystems.easymodding.platform.base

import org.gradle.api.Named
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class EasyModdingRunConfig @Inject constructor(
	objects: ObjectFactory
) : Named {
	val displayName = objects.property<String>()
	val runtimeEnvironment = objects.property<String>()
	val workingDirectory = objects.directoryProperty()
	val environmentVariables = objects.mapProperty<String, String>()
	val systemProperties = objects.mapProperty<String, String>()
	val jvmArguments = objects.listProperty<String>()
	val programArguments = objects.listProperty<String>()
	val sourceSet = objects.property<String>()

	fun client() {
		runtimeEnvironment.set("client")
	}

	fun server() {
		runtimeEnvironment.set("server")
		programArguments.add("nogui")
	}

	fun serverWithGui() {
		runtimeEnvironment.set("server")
	}
}