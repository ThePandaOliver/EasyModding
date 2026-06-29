package dev.pandasystems.easymodding.loader.neoforge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Reference: https://docs.neoforged.net/docs/gettingstarted/modfiles/

@Serializable
data class NeoForgeMetadata(
	val modloader: String = "javafml",
	val loaderVersion: String = "",
	val license: String,
	val showAsResourcePack: Boolean = false,
	val showAsDataPack: Boolean = false,
	val services: List<String> = emptyList(),
	val properties: Map<String, String> = emptyMap(),
	val issueTrackerURL: String? = null,

	val mods: List<NeoForgeMod> = emptyList(),
	val features: Map<String, Map<String, String>> = emptyMap(),
	val modproperties: Map<String, Map<String, String>> = emptyMap(),
	val accessTransformers: List<NeoForgeAccessTransformer> = emptyList(),
	val mixins: List<NeoForgeMixinConfig> = emptyList(),
	val dependencies: Map<String, List<NeoForgeDependency>> = emptyMap(),
)

@Serializable
data class NeoForgeMod(
	val modId: String,
	val namespace: String = modId,
	val version: String = "1",
	val displayName: String = modId,
	val description: String = "MISSING DESCRIPTION",
	val logoFile: String? = null,
	val logoBlur: Boolean = true,
	val updateJSONURL: String? = null,
	val modUrl: String? = null,
	val credits: String? = null,
	val authors: String? = null,
	val displayURL: String? = null,
	val enumExtension: String? = null,
	val featureFlags: String? = null,
)

@Serializable
data class NeoForgeAccessTransformer(
	val file: String
)

@Serializable
data class NeoForgeMixinConfig(
	val config: String,
	val requiredMods: List<String> = emptyList(),
	val behaviorVersion: String? = null,
)

@Serializable
data class NeoForgeDependency(
	val modId: String,
	val type: NeoForgeDependencyType = NeoForgeDependencyType.REQUIRED,
	val reason: String? = null,
	val versionRange: String = "",
	val ordering: NeoForgeDependencyOrdering = NeoForgeDependencyOrdering.NONE,
	val side: NeoForgeDependencySide = NeoForgeDependencySide.BOTH,
	val referralUrl: String? = null
)

@Serializable
enum class NeoForgeDependencyType {
	@SerialName("required")
	REQUIRED,
	@SerialName("optional")
	OPTIONAL,
	@SerialName("incompatible")
	INCOMPATIBLE,
	@SerialName("discouraged")
	DISCOURAGED
}

@Serializable
enum class NeoForgeDependencyOrdering {
	NONE,
	BEFORE,
	AFTER
}

@Serializable
enum class NeoForgeDependencySide {
	CLIENT,
	SERVER,
	BOTH
}