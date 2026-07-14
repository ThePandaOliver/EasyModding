package dev.pandasystems.easymodding.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
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
import java.io.File

typealias EasyModdingContact = Map<String, String>

@Serializable
data class EasyModdingConfig(
	val schemaVersion: Int = 1,
	val metadata: EasyModdingMetadata,
	val mixins: List<String>? = null,
	val fabric: FabricModJson = FabricModJson(),
	val neoforge: NeoForgeModToml = NeoForgeModToml(),
	val pack: EasyModdingPack? = null,
)

@Serializable
data class EasyModdingMetadata(
	val id: String,
	val version: String,
	val name: String? = null,
	val description: String? = null,
	val license: String? = null,
	val icon: String? = null,
	val authors: List<EasyModdingPerson>? = null,
	val contributors: List<EasyModdingPerson>? = null,
	val contact: EasyModdingContact? = null,
)

@Serializable
data class EasyModdingPack(
	val description: String? = null,
	@SerialName("pack_format")
	val packFormat: Float? = null,
	@SerialName("min_format")
	val minFormat: Float? = null,
	@SerialName("max_format")
	val maxFormat: Float? = null,
)

@Serializable(with = EasyModdingPersonSerializer::class)
data class EasyModdingPerson(
	val name: String,
	val contact: EasyModdingContact? = null
)

object EasyModdingPersonSerializer : KSerializer<EasyModdingPerson> {
	override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

	override fun serialize(encoder: Encoder, value: EasyModdingPerson) {
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

	override fun deserialize(decoder: Decoder): EasyModdingPerson {
		return when (val element = decoder.decodeSerializableValue(JsonElement.serializer())) {
			is JsonPrimitive -> EasyModdingPerson(element.content)
			is JsonObject -> EasyModdingPerson(
				name = element.getValue("name").jsonPrimitive.content,
				contact = element["contact"]?.jsonObject?.mapValues { it.value.jsonPrimitive.content }
			)
			else -> throw SerializationException("Unexpected JSON for EasyModdingPerson: $element")
		}
	}
}

internal fun loadEasyModdingConfig(file: File): EasyModdingConfig {
	val json = Json {
		ignoreUnknownKeys = true
		explicitNulls = false
	}
	val easyModding = json.decodeFromString<EasyModdingConfig>(file.readText())
	return easyModding
}