package dev.pandasystems.easymodding.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A single, loader-agnostic mod dependency declaration (`dependencies` in `easymodding.mod.json`).
 *
 * This is the single place to declare a mod's dependency relationships (on other mods, on
 * loader-provided APIs, etc.). At generation time [dev.pandasystems.easymodding.data.populateFabricModJson],
 * [dev.pandasystems.easymodding.data.populateNeoForgeModToml] and
 * [dev.pandasystems.easymodding.data.populateForgeModToml] translate this list into the
 * loader-native shape:
 *  - Fabric: bucketed into `depends`/`recommends`/`conflicts`/`breaks` based on [type] (see
 *    [EasyModdingDependencyType] for the exact mapping).
 *  - NeoForge: appended to `[[dependencies]]`, converting the shared semver-style version range
 *    to NeoForge's Maven version-range syntax.
 *  - Forge: appended to `[[dependencies]]`, with [type] collapsed to the boolean `mandatory` flag
 *    Forge's legacy schema uses ([EasyModdingDependencyType.Required] only).
 *
 * Loader-specific `dependencies` declared directly under the `fabric`/`neoforge`/`forge` sections
 * of `easymodding.mod.json` are still supported and are appended alongside (Fabric) or after
 * (NeoForge/Forge) the ones generated from this unified list, so platform-specific extras can
 * still be added without repeating the shared ones.
 */
@Serializable
data class EasyModdingDependency(
	/** The mod (or loader-provided API, e.g. `fabricloader`, `minecraft`) this depends on. */
	val modId: String,
	/** How strictly this dependency is required. Defaults to [EasyModdingDependencyType.Required]. */
	val type: EasyModdingDependencyType = EasyModdingDependencyType.Required,
	/**
	 * The accepted version range in shared semver-style syntax (for example, `>=1.0.0`). It is
	 * converted to Maven range syntax for NeoForge. Left unset, any version is accepted.
	 */
	val versionRange: String? = null,
	/** Human-readable explanation shown to the user when this dependency isn't satisfied. */
	val reason: String? = null,
	/** Load ordering relative to this dependency. Unset lets the loader decide. */
	val ordering: EasyModdingDependencyOrdering? = null,
	/** Which physical side this dependency applies to. Unset means both. */
	val side: EasyModdingDependencySide? = null,
	/** A URL to visit for more information about (or to obtain) this dependency. */
	val referralUrl: String? = null,
)

/**
 * How strictly a unified dependency is required, mirroring NeoForge's own dependency type.
 *
 * Mapping to each loader's native schema:
 *  - Fabric: [Required] -> `depends`, [Optional] -> `recommends`, [Discouraged] -> `conflicts`,
 *    [Incompatible] -> `breaks`. Fabric's `suggests` bucket has no unified equivalent since it
 *    carries no version-range semantics; use the `fabric.suggests` override for that.
 *  - NeoForge: maps 1:1 to `net.neoforged.neoforgespi.locating.IModFile$DependencyType`.
 *  - Forge: collapsed to the boolean `mandatory` flag ([Required] -> `true`, everything else ->
 *    `false`), as legacy Forge has no concept of incompatible/discouraged dependencies.
 */
@Serializable
enum class EasyModdingDependencyType {
	@SerialName("required") Required,
	@SerialName("optional") Optional,
	@SerialName("incompatible") Incompatible,
	@SerialName("discouraged") Discouraged,
}

/** Load ordering of the owning mod relative to a dependency. */
@Serializable
enum class EasyModdingDependencyOrdering {
	@SerialName("before") Before,
	@SerialName("after") After,
	@SerialName("none") None,
}

/** Which physical side a dependency applies to. */
@Serializable
enum class EasyModdingDependencySide {
	@SerialName("client") Client,
	@SerialName("server") Server,
	@SerialName("both") Both,
}

/** Maps the unified [EasyModdingDependencyType] to its NeoForge equivalent (a 1:1 mapping). */
internal fun EasyModdingDependencyType.toNeoForgeDependencyType(): NeoForgeDependencyType = when (this) {
	EasyModdingDependencyType.Required -> NeoForgeDependencyType.Required
	EasyModdingDependencyType.Optional -> NeoForgeDependencyType.Optional
	EasyModdingDependencyType.Incompatible -> NeoForgeDependencyType.Incompatible
	EasyModdingDependencyType.Discouraged -> NeoForgeDependencyType.Discouraged
}

/**
 * Converts a single semver-style bound to the Maven range syntax used by NeoForge.
 *
 * Already-native ranges and expressions that are not a single bound are preserved so that
 * loader-specific syntax is not silently changed or made less precise.
 */
internal fun String.toNeoForgeVersionRange(): String {
	val match = Regex("^(>=|>|<=|<)\\s*(.+)$").matchEntire(trim()) ?: return this
	val (operator, version) = match.destructured
	return when (operator) {
		">=" -> "[$version,)"
		">" -> "($version,)"
		"<=" -> "(,$version]"
		"<" -> "(,$version)"
		else -> this
	}
}

/** Maps the unified [EasyModdingDependencyOrdering] to the shared NeoForge/Forge ordering enum. */
internal fun EasyModdingDependencyOrdering.toNeoForgeDependencyOrdering(): NeoForgeDependencyOrdering = when (this) {
	EasyModdingDependencyOrdering.Before -> NeoForgeDependencyOrdering.Before
	EasyModdingDependencyOrdering.After -> NeoForgeDependencyOrdering.After
	EasyModdingDependencyOrdering.None -> NeoForgeDependencyOrdering.None
}

/** Maps the unified [EasyModdingDependencySide] to the shared NeoForge/Forge side enum. */
internal fun EasyModdingDependencySide.toNeoForgeDependencySide(): NeoForgeDependencySide = when (this) {
	EasyModdingDependencySide.Client -> NeoForgeDependencySide.Client
	EasyModdingDependencySide.Server -> NeoForgeDependencySide.Server
	EasyModdingDependencySide.Both -> NeoForgeDependencySide.Both
}
