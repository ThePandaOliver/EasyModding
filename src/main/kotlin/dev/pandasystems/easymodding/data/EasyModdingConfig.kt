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

/** A map of contact keys (e.g. `homepage`, `issues`) to their URLs/values. */
typealias EasyModdingContact = Map<String, String>

/**
 * The unified, loader-agnostic mod configuration, deserialized from `easymodding.mod.json`.
 *
 * This is the single source of truth for a mod's metadata. At build time it is transformed into
 * the various loader-native metadata files:
 *  - [metadata] + [fabric] -> `fabric.mod.json`
 *  - [metadata] + [neoforge] -> `neoforge.mods.toml`
 *  - [metadata] + [forge] -> `mods.toml`
 *  - [pack] -> `pack.mcmeta`
 *
 * The shared [metadata] is used as a fallback for loader-specific sections, so common fields only
 * need to be declared once. Loader-specific sections may override or extend those values.
 *
 * The shared [dependencies] list works the same way for mod dependency declarations: declare a
 * dependency once and it is translated into every enabled loader's native dependency schema (see
 * [EasyModdingDependency] for the exact per-loader mapping). Loader-specific sections may still
 * declare extra, platform-only dependencies alongside the shared ones.
 */
@Serializable
data class EasyModdingConfig(
	/** Config schema version, for forward compatibility. */
	val schemaVersion: Int = 1,
	/** Shared metadata common to all loaders. */
	val metadata: EasyModdingMetadata,
	/** Mixin config file names shared across loaders. */
	val mixins: List<String>? = null,
	/**
	 * Unified, loader-agnostic mod dependency declarations. Declared once here, they are mapped
	 * into `fabric.mod.json`'s `depends`/`recommends`/`conflicts`/`breaks`, NeoForge's and Forge's
	 * `[[dependencies]]` automatically for every loader that is enabled.
	 */
	val dependencies: List<EasyModdingDependency> = emptyList(),
	/** Fabric-specific overrides/extensions, merged over [metadata]. */
	val fabric: FabricModJson = FabricModJson(),
	/** NeoForge-specific overrides/extensions, merged over [metadata]. */
	val neoforge: NeoForgeModToml = NeoForgeModToml(),
	/**
	 * Legacy Forge-specific overrides/extensions, merged over [metadata]. Note this uses its own
	 * [ForgeModsToml] schema, which is similar to but not identical to [neoforge]'s.
	 */
	val forge: ForgeModsToml = ForgeModsToml(),
	/** Resource/data pack format info used to generate `pack.mcmeta`. */
	val pack: EasyModdingPack? = null,
)

/** Loader-agnostic mod metadata shared by every generated metadata file. */
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

/**
 * Pack format info for `pack.mcmeta`. Supports either the legacy single [packFormat] or the modern
 * [minFormat]/[maxFormat] range; see [PackMcmeta] for how these are normalized on output.
 */
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

/**
 * A person (author or contributor) associated with the mod.
 *
 * Serialized via [EasyModdingPersonSerializer] to match the loader convention of writing a bare
 * string when there is no [contact] info, or an object with `name`/`contact` when there is.
 */
@Serializable(with = EasyModdingPersonSerializer::class)
data class EasyModdingPerson(
	val name: String,
	val contact: EasyModdingContact? = null
)

/**
 * Custom serializer for [EasyModdingPerson] that reads/writes either a bare JSON string
 * (`"Some Name"`) or a full object (`{ "name": ..., "contact": { ... } }`).
 */
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

/** Reads and parses an `easymodding.mod.json` file into an [EasyModdingConfig]. */
internal fun loadEasyModdingConfig(file: File): EasyModdingConfig {
	val json = Json {
		ignoreUnknownKeys = true
		explicitNulls = false
	}
	val easyModding = json.decodeFromString<EasyModdingConfig>(file.readText())
	return easyModding
}