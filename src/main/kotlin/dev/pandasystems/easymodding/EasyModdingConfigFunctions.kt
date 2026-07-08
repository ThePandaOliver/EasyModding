package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.loader.fabric.FabricMixinEntry
import dev.pandasystems.easymodding.loader.fabric.FabricModJson
import dev.pandasystems.easymodding.loader.fabric.FabricPerson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

fun loadEasyModdingConfig(file: File): EasyModdingConfig {
	val json = Json {
		ignoreUnknownKeys = true
	}
	val easyModding = json.decodeFromStream<EasyModdingConfig>(file.inputStream())
	return easyModding
}

fun EasyModdingConfig.populateFabricModJson(): FabricModJson {
	return fabric?.copy(
		id = fabric.id ?: metadata.id,
		version = fabric.version ?: metadata.version,
		name = fabric.name ?: metadata.name,
		description = fabric.description ?: metadata.description,
		license = fabric.license ?: metadata.license,
		icon = fabric.icon ?: metadata.icon,
		authors = fabric.authors ?: metadata.authors?.map { (name, contact) -> FabricPerson(name, contact) },
		contributors = fabric.contributors ?: metadata.contributors?.map { (name, contact) -> FabricPerson(name, contact) },
		contact = fabric.contact ?: metadata.contact,
		mixins = fabric.mixins ?: mixins?.map { FabricMixinEntry(it) },
	) ?: FabricModJson()
}