package dev.pandasystems.easymodding.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

// Reference: https://docs.fabricmc.net/develop/loader/fabric-mod-json

/**
 * Data model of Fabric's `fabric.mod.json` mod metadata file.
 *
 * Doubles as the `fabric` section of `easymodding.mod.json` (allowing Fabric-specific overrides)
 * and as the output model that is serialized to the final `fabric.mod.json`. See the Fabric docs
 * linked above for the meaning of each field.
 */
@Serializable
data class FabricModJson(
	val schemaVersion: Int = 1,
	val id: String? = null,
	val version: String? = null,

	val name: String? = null,
	val description: String? = null,
	val contact: Map<String, String>? = null,
	val authors: List<FabricPerson>? = null,
	val contributors: List<FabricPerson>? = null,
	val license: String? = null,
	val icon: String? = null,

	val environment: String? = null,
	val entrypoints: Map<String, List<String>>? = null,
	val jars: List<FabricJarEntry>? = null,
	val languageAdapters: Map<String, String>? = null,
	val mixins: List<FabricMixinEntry>? = null,
	val accessWidener: String? = null,
	val provides: List<String>? = null,

	val depends: Map<String, String>? = null,
	val recommends: Map<String, String>? = null,
	val suggests: Map<String, String>? = null,
	val breaks: Map<String, String>? = null,
	val conflicts: Map<String, String>? = null,
)

/** A nested (jar-in-jar) jar entry within `fabric.mod.json`. */
@Serializable
data class FabricJarEntry(val file: String)


/**
 * A Fabric author/contributor. Serialized via [FabricPersonSerializer] as either a bare string or
 * a `{ name, contact }` object depending on whether [contact] is present.
 */
@Serializable(with = FabricPersonSerializer::class)
data class FabricPerson(
	val name: String,
	val contact: Map<String, String>? = null,
)

/** Custom serializer emitting a Fabric person as a bare string or a `{ name, contact }` object. */
object FabricPersonSerializer : KSerializer<FabricPerson> {
	override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

	override fun serialize(encoder: Encoder, value: FabricPerson) {
		val element = if (value.contact.isNullOrEmpty()) {
			JsonPrimitive(value.name)
		} else {
			buildJsonObject {
				put("name", value.name)
				put("contact", buildJsonObject { value.contact.forEach { (key, v) -> put(key, v) } })
			}
		}
		encoder.encodeSerializableValue(JsonElement.serializer(), element)
	}

	override fun deserialize(decoder: Decoder): FabricPerson {
		return when (val element = decoder.decodeSerializableValue(JsonElement.serializer())) {
			is JsonPrimitive -> FabricPerson(element.content)
			is JsonObject -> FabricPerson(
				name = element.getValue("name").jsonPrimitive.content,
				contact = element["contact"]?.jsonObject?.mapValues { it.value.jsonPrimitive.content }
			)
			else -> throw SerializationException("Unexpected JSON for FabricPerson: $element")
		}
	}
}

/**
 * A Fabric mixin config entry. Serialized via [FabricMixinEntrySerializer] as a bare string when
 * there is no [environment], or a `{ config, environment }` object otherwise.
 */
@Serializable(with = FabricMixinEntrySerializer::class)
data class FabricMixinEntry(
	val config: String,
	val environment: String? = null,
)

/** Custom serializer emitting a mixin entry as a bare string or `{ config, environment }` object. */
object FabricMixinEntrySerializer : KSerializer<FabricMixinEntry> {
	override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

	override fun serialize(encoder: Encoder, value: FabricMixinEntry) {
		val element = if (value.environment == null) {
			JsonPrimitive(value.config)
		} else {
			buildJsonObject {
				put("config", value.config)
				put("environment", value.environment)
			}
		}
		encoder.encodeSerializableValue(JsonElement.serializer(), element)
	}

	override fun deserialize(decoder: Decoder): FabricMixinEntry {
		return when (val element = decoder.decodeSerializableValue(JsonElement.serializer())) {
			is JsonPrimitive -> FabricMixinEntry(element.content)
			is JsonObject -> FabricMixinEntry(
				config = element.getValue("config").jsonPrimitive.content,
				environment = element["environment"]?.jsonPrimitive?.content
			)
			else -> throw SerializationException("Unexpected JSON for FabricMixinEntry: $element")
		}
	}
}

/**
 * Builds the final [FabricModJson] by merging the shared [EasyModdingConfig.metadata] into the
 * Fabric-specific section. Fabric-specific values take precedence; where absent, the shared
 * metadata is used as a fallback.
 */
internal fun EasyModdingConfig.populateFabricModJson(): FabricModJson {
	return fabric.copy(
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
	)
}

/** Serializes this [FabricModJson] to a pretty-printed JSON string for writing to disk. */
internal fun FabricModJson.toJsonString(): String {
	val jsonFormat = Json {
		ignoreUnknownKeys = true
		prettyPrint = true
		encodeDefaults = true
		explicitNulls = false
	}
	return jsonFormat.encodeToString(this)
}