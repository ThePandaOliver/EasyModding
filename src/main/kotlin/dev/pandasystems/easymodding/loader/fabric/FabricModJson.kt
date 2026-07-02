package dev.pandasystems.easymodding.loader.fabric

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

// Reference: https://docs.fabricmc.net/develop/loader/fabric-mod-json

@Serializable
data class FabricModJson(
	val schemaVersion: Int = 1,
	val id: String,
	val version: String,

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

@Serializable
data class FabricJarEntry(val file: String)


@Serializable(with = FabricPersonSerializer::class)
data class FabricPerson(
	val name: String,
	val contact: Map<String, String>? = null,
)

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
 * Serialized as a plain config path string when the environment is unrestricted,
 * otherwise as `{"config": ..., "environment": ...}`, matching the fabric.mod.json mixin format.
 */
@Serializable(with = FabricMixinEntrySerializer::class)
data class FabricMixinEntry(
	val config: String,
	val environment: String? = null,
)

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
