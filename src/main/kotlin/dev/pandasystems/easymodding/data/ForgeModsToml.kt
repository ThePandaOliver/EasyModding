package dev.pandasystems.easymodding.data

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

// Reference: https://docs.minecraftforge.net/en/latest/gettingstarted/modfiles/

/**
 * Data model of legacy Forge's `mods.toml` mod metadata file.
 *
 * Doubles as the `forge` section of `easymodding.mod.json` (for Forge-specific overrides) and as
 * the output model serialized to the final `mods.toml`. See the Forge docs linked above for field
 * semantics.
 *
 * Although structurally similar to [NeoForgeModToml], Forge's `mods.toml` is **not** identical to
 * NeoForge's `neoforge.mods.toml`: notably, dependencies use a boolean [ForgeDependency.mandatory]
 * flag instead of NeoForge's [NeoForgeDependencyType] enum, `clientSideOnly` replaces
 * `showAsDataPack`, and there is no `[[mixins]]`/`[[accessTransformers]]` support (those are
 * NeoForge-only additions), so this is modeled as its own dedicated type.
 */
@Serializable
data class ForgeModsToml(
	val modLoader: String? = null,
	val loaderVersion: String? = null,
	val license: String? = null,
	val showAsResourcePack: Boolean? = null,
	val clientSideOnly: Boolean? = null,
	val services: List<String>? = null,
	val properties: Map<String, String>? = null,
	val issueTrackerURL: String? = null,

	val mods: List<ForgeMod>? = null,
	val dependencies: List<ForgeDependency>? = null
)

/** A single `[[mods]]` entry in `mods.toml` describing one mod in the jar. */
@Serializable
data class ForgeMod(
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
	val displayTest: String? = null,
)

/**
 * A mod dependency declaration with its requirement, version range, load ordering and side.
 *
 * Unlike [NeoForgeDependency], Forge has no `type` field; whether the dependency is required is
 * instead expressed with the boolean [mandatory] flag.
 */
@Serializable
data class ForgeDependency(
	val modId: String? = null,
	val mandatory: Boolean? = null,
	val reason: String? = null,
	val versionRange: String? = null,
	val ordering: NeoForgeDependencyOrdering? = null,
	val side: NeoForgeDependencySide? = null,
	val referralUrl: String? = null
)

/**
 * Builds the final [ForgeModsToml] by merging the shared [EasyModdingConfig.metadata] into the
 * Forge-specific section. If no explicit `mods` list is provided, a single [ForgeMod] is
 * synthesized from the shared metadata.
 *
 * The unified [EasyModdingConfig.dependencies] are translated into [ForgeDependency] entries and
 * prepended to any dependencies declared directly under `forge`, so platform-only extras can
 * still be appended. Since legacy Forge only has a boolean [ForgeDependency.mandatory] flag,
 * [EasyModdingDependencyType.Required] maps to `mandatory = true` and every other type
 * (`optional`/`incompatible`/`discouraged`) maps to `mandatory = false`.
 */
internal fun EasyModdingConfig.populateForgeModToml(): ForgeModsToml {
	return forge.copy(
		license = forge.license ?: metadata.license,
		mods = forge.mods ?: listOf(
			ForgeMod(
				modId = metadata.id,
				version = metadata.version,
				displayName = metadata.name,
				description = metadata.description,
				logoFile = metadata.icon,
				authors = metadata.authors?.map { it.name }?.joinToString(", ") { it },
			)
		),
		dependencies = (dependencies.map { it.toForgeDependency() } + (forge.dependencies ?: emptyList())).ifEmpty { null },
	)
}

/** Translates a unified [EasyModdingDependency] into Forge's native [ForgeDependency] shape. */
private fun EasyModdingDependency.toForgeDependency() = ForgeDependency(
	modId = modId,
	mandatory = type == EasyModdingDependencyType.Required,
	reason = reason,
	versionRange = versionRange,
	ordering = ordering?.toNeoForgeDependencyOrdering(),
	side = side?.toNeoForgeDependencySide(),
	referralUrl = referralUrl,
)

/** Serializes this [ForgeModsToml] to a TOML string (via ktoml) for writing to disk. */
internal fun ForgeModsToml.toTomlString(): String {
	val tomlFormat = Toml()
	return tomlFormat.encodeToString(this)
}
