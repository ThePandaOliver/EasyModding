package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.loader.fabric.FabricMetadataSpec
import dev.pandasystems.easymodding.loader.neoforge.NeoForgeMetadataSpec
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class EasyModdingExtension @Inject constructor(
	objects: ObjectFactory
) {
	val metadata = objects.newInstance(EasyModdingMetadata::class.java)
	fun metadata(action: Action<EasyModdingMetadata>) = action.execute(metadata)

	val fabric = objects.newInstance(FabricMetadataSpec::class.java)
	fun fabric(action: Action<FabricMetadataSpec>) = action.execute(fabric)

	val neoForge = objects.newInstance(NeoForgeMetadataSpec::class.java)
	fun neoForge(action: Action<NeoForgeMetadataSpec>) = action.execute(neoForge)

	val mixins = objects.listProperty(String::class.java)
	fun mixin(mixin: String) = mixins.add(mixin)
}

abstract class EasyModdingMetadata @Inject constructor(
	objects: ObjectFactory
) {
	val id = objects.property(String::class.java)
	val version = objects.property(String::class.java)
	val name = objects.property(String::class.java)
	val description = objects.property(String::class.java)
	val license = objects.property(String::class.java)
	val icon = objects.fileProperty()
	val environment = objects.property(Environment::class.java).convention(Environment.BOTH)

	val authors = objects.listProperty(Person::class.java)
	fun author(name: String, contact: String? = null) = authors.add(Person(name, contact))

	val contributors = objects.listProperty(Person::class.java)
	fun contributor(name: String, contact: String? = null) = contributors.add(Person(name, contact))

	data class Person(val name: String, val contact: String? = null)
	enum class Environment { CLIENT, SERVER, BOTH }
}