package dev.pandasystems.easymodding.data

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonPrimitive

// Reference: https://docs.neoforged.net/docs/gettingstarted/modfiles/

/**
 * Data model of NeoForge's `neoforge.mods.toml` mod metadata file.
 *
 * Doubles as the `neoforge` section of `easymodding.mod.json` (for NeoForge-specific overrides)
 * and as the output model serialized to the final `neoforge.mods.toml`. See the NeoForge docs
 * linked above for field semantics.
 */
@Serializable
data class NeoForgeModToml(
	val modLoader: String? = null,
	val loaderVersion: String? = null,
	val license: String? = null,
	val showAsResourcePack: Boolean? = null,
	val showAsDataPack: Boolean? = null,
	val services: List<String>? = null,
	val properties: Map<String, String>? = null,
	val issueTrackerURL: String? = null,

	val mods: List<NeoForgeMod>? = null,
	val features: Map<String, Map<String, String>>? = null,
	val modproperties: Map<String, Map<String, String>>? = null,
	val accessTransformers: List<NeoForgeAccessTransformer>? = null,
	val mixins: List<NeoForgeMixin>? = null,
	val dependencies: List<NeoForgeDependency>? = null
)

/** A single `[[mods]]` entry in `neoforge.mods.toml` describing one mod in the jar. */
@Serializable
data class NeoForgeMod(
	val modId: String? = null,
	val namespace: String? = null,
	val version: String? = null,
	val displayName: String? = null,
	val description: String? = null,
	val logoFile: String? = null,
	val logoBlur: Boolean? = null,
	val updateJSONURL: String? = null,
	val modUrl: String? = null,
	val credits: String? = null,
	val authors: String? = null,
	val displayURL: String? = null,
	val enumExtension: String? = null,
	val featureFlags: String? = null
)

/** An access transformer file declaration. */
@Serializable
data class NeoForgeAccessTransformer(
	val file: String? = null,
)

/** A mixin config declaration for NeoForge. */
@Serializable
data class NeoForgeMixin(
	val config: String? = null,
	val requiredMods: List<String>? = null,
	val behaviorVersion: String? = null
)

/** A mod dependency declaration with its type, version range, load ordering and side. */
@Serializable
data class NeoForgeDependency(
	val modId: String? = null,
	val type: NeoForgeDependencyType? = null,
	val reason: String? = null,
	val versionRange: String? = null,
	val ordering: NeoForgeDependencyOrdering? = null,
	val side: NeoForgeDependencySide? = null,
	val referralUrl: String? = null
)

/** How strictly a dependency is required. Serialized lowercase (e.g. `required`). */
@Serializable(NeoForgeDependencyTypeSerializer::class)
enum class NeoForgeDependencyType {
	Required,
	Optional,
	Incompatible,
	Discouraged
}

/** Maps [NeoForgeDependencyType] to/from its lowercase TOML string representation. */
object NeoForgeDependencyTypeSerializer : KSerializer<NeoForgeDependencyType> {
	override val descriptor: SerialDescriptor = JsonPrimitive.serializer().descriptor

	override fun serialize(
		encoder: Encoder,
		value: NeoForgeDependencyType
	) {
		encoder.encodeString(when(value) {
			NeoForgeDependencyType.Required -> "required"
			NeoForgeDependencyType.Optional -> "optional"
			NeoForgeDependencyType.Incompatible -> "incompatible"
			NeoForgeDependencyType.Discouraged -> "discouraged"
		})
	}

	override fun deserialize(decoder: Decoder): NeoForgeDependencyType {
		return when(decoder.decodeString()) {
			"required" -> NeoForgeDependencyType.Required
			"optional" -> NeoForgeDependencyType.Optional
			"incompatible" -> NeoForgeDependencyType.Incompatible
			"discouraged" -> NeoForgeDependencyType.Discouraged
			else -> throw IllegalArgumentException("Invalid dependency type: ${decoder.decodeString()}")
		}
	}
}

/** Load ordering of this mod relative to a dependency. Serialized uppercase (e.g. `BEFORE`). */
@Serializable(NeoForgeDependencyOrderingSerializer::class)
enum class NeoForgeDependencyOrdering {
	Before,
	After,
	None
}

/** Maps [NeoForgeDependencyOrdering] to/from its uppercase TOML string representation. */
object NeoForgeDependencyOrderingSerializer : KSerializer<NeoForgeDependencyOrdering> {
	override val descriptor: SerialDescriptor = JsonPrimitive.serializer().descriptor

	override fun serialize(
		encoder: Encoder,
		value: NeoForgeDependencyOrdering
	) {
		encoder.encodeString(when(value) {
			NeoForgeDependencyOrdering.Before -> "BEFORE"
			NeoForgeDependencyOrdering.After -> "AFTER"
			NeoForgeDependencyOrdering.None -> "NONE"
		})
	}

	override fun deserialize(decoder: Decoder): NeoForgeDependencyOrdering {
		return when(decoder.decodeString()) {
			"BEFORE" -> NeoForgeDependencyOrdering.Before
			"AFTER" -> NeoForgeDependencyOrdering.After
			"NONE" -> NeoForgeDependencyOrdering.None
			else -> throw IllegalArgumentException("Invalid dependency ordering: ${decoder.decodeString()}")
		}
	}
}

/** Which physical side a dependency applies to. Serialized uppercase (e.g. `BOTH`). */
@Serializable(NeoForgeDependencySideSerializer::class)
enum class NeoForgeDependencySide {
	Client,
	Server,
	Both
}

/** Maps [NeoForgeDependencySide] to/from its uppercase TOML string representation. */
object NeoForgeDependencySideSerializer : KSerializer<NeoForgeDependencySide> {
	override val descriptor: SerialDescriptor = JsonPrimitive.serializer().descriptor

	override fun serialize(
		encoder: Encoder,
		value: NeoForgeDependencySide
	) {
		encoder.encodeString(when(value) {
			NeoForgeDependencySide.Client -> "CLIENT"
			NeoForgeDependencySide.Server -> "SERVER"
			NeoForgeDependencySide.Both -> "BOTH"
		})
	}

	override fun deserialize(decoder: Decoder): NeoForgeDependencySide {
		return when(decoder.decodeString()) {
			"CLIENT" -> NeoForgeDependencySide.Client
			"SERVER" -> NeoForgeDependencySide.Server
			"BOTH" -> NeoForgeDependencySide.Both
			else -> throw IllegalArgumentException("Invalid NeoForgeDependencySide value")
		}
	}
}

/**
 * Builds the final [NeoForgeModToml] by merging the shared [EasyModdingConfig.metadata] into the
 * NeoForge-specific section. If no explicit `mods` list is provided, a single [NeoForgeMod] is
 * synthesized from the shared metadata.
 *
 * The unified [EasyModdingConfig.dependencies] are translated 1:1 into [NeoForgeDependency]
 * entries (NeoForge's schema matches the unified one almost exactly) and prepended to any
 * dependencies declared directly under `neoforge`, so platform-only extras can still be appended.
 */
internal fun EasyModdingConfig.populateNeoForgeModToml(): NeoForgeModToml {
	return neoforge.copy(
		license = neoforge.license ?: metadata.license,
		mods = neoforge.mods ?: listOf(
			NeoForgeMod(
				modId = metadata.id,
				version = metadata.version,
				displayName = metadata.name,
				description = metadata.description,
				logoFile = metadata.icon,
				authors = metadata.authors?.map { it.name }?.joinToString(", ") { it },
			)
		),
		mixins = neoforge.mixins ?: mixins?.map { NeoForgeMixin(config = it) },
		dependencies = (dependencies.map { it.toNeoForgeDependency() } + (neoforge.dependencies ?: emptyList())).ifEmpty { null },
	)
}

/** Translates a unified [EasyModdingDependency] into NeoForge's native [NeoForgeDependency] shape. */
private fun EasyModdingDependency.toNeoForgeDependency() = NeoForgeDependency(
	modId = modId,
	type = type.toNeoForgeDependencyType(),
	reason = reason,
	versionRange = versionRange,
	ordering = ordering?.toNeoForgeDependencyOrdering(),
	side = side?.toNeoForgeDependencySide(),
	referralUrl = referralUrl,
)

/** Serializes this [NeoForgeModToml] to a TOML string (via ktoml) for writing to disk. */
internal fun NeoForgeModToml.toTomlString(): String {
	val tomlFormat = Toml()
	return tomlFormat.encodeToString(this)
}