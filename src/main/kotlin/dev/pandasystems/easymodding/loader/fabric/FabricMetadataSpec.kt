package dev.pandasystems.easymodding.loader.fabric

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

// Reference: https://docs.fabricmc.net/develop/loader/fabric-mod-json

abstract class FabricMetadataSpec @Inject constructor(
	private val objects: ObjectFactory,
) {
	val id = objects.property<String>()
	val version = objects.property<String>()

	val environment = objects.property<FabricEnvironment>()
	val entrypoints = objects.domainObjectContainer(FabricEntrypointSpec::class.java)
	val jars = objects.listProperty<String>()
	val languageAdapters = objects.mapProperty<String, String>()
	val mixins = objects.domainObjectContainer(FabricMixinConfigSpec::class.java)
	val accessWideners = objects.property<String>()
	val provides = objects.listProperty<String>()

	val depends = objects.mapProperty<String, String>()
	val recommends = objects.mapProperty<String, String>()
	val suggests = objects.mapProperty<String, String>()
	val breaks = objects.mapProperty<String, String>()
	val conflicts = objects.mapProperty<String, String>()

	val name = objects.property<String>()
	val description = objects.property<String>()
	val contact = objects.mapProperty<String, String>()
	val authors = objects.domainObjectContainer(FabricPersonSpec::class.java)
	val contributors = objects.domainObjectContainer(FabricPersonSpec::class.java)
	val license = objects.property<String>()
	val icon = objects.property<String>()

	fun provide(modId: String) = provides.add(modId)
	fun entrypoint(entrypointName: String, action: Action<FabricEntrypointSpec>) = entrypoints.register(entrypointName, action)
	fun jar(file: String) = jars.add(file)
	fun languageAdapter(language: String, adapter: String) = languageAdapters.put(language, adapter)

	fun mixin(config: String, action: Action<FabricMixinConfigSpec> = Action {}) {
		val mixin = mixins.create(config)
		mixin.config.set(config)
		action.execute(mixin)
	}

	fun dependency(modId: String, version: String) = depends.put(modId, version)
	fun dependencyRecommend(modId: String, version: String) = recommends.put(modId, version)
	fun dependencySuggest(modId: String, version: String) = suggests.put(modId, version)
	fun dependencyBreak(modId: String, version: String) = breaks.put(modId, version)
	fun dependencyConflict(modId: String, version: String) = conflicts.put(modId, version)

	fun contact(type: String, value: String) = contact.put(type, value)
	fun author(name: String, action: Action<FabricPersonSpec> = Action {}) {
		val person = authors.create(name)
		person.name.set(name)
		action.execute(person)
	}
	fun contributor(name: String, action: Action<FabricPersonSpec> = Action {}) {
		val person = contributors.create(name)
		person.name.set(name)
		action.execute(person)
	}
}

abstract class FabricEntrypointSpec @Inject constructor(
	objects: ObjectFactory,
) {
	val entrypoints = objects.listProperty<String>()
	fun entrypoint(name: String) = entrypoints.add(name)
}

abstract class FabricMixinConfigSpec @Inject constructor(
	objects: ObjectFactory,
) {
	val config = objects.property<String>()
	val environment = objects.property<FabricEnvironment>().convention(FabricEnvironment.BOTH)
}

abstract class FabricPersonSpec @Inject constructor(
	objects: ObjectFactory,
) {
	val name = objects.property<String>()
	val contact = objects.mapProperty<String, String>()

	fun contact(key: String, value: String) = contact.put(key, value)
}

enum class FabricEnvironment { CLIENT, SERVER, BOTH }
