package dev.pandasystems.easymodding.data

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

// Reference: https://docs.minecraftforge.net/en/latest/gettingstarted/modfiles/

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

private fun EasyModdingDependency.toForgeDependency() = ForgeDependency(
	modId = modId,
	mandatory = type == EasyModdingDependencyType.Required,
	reason = reason,
	versionRange = versionRange,
	ordering = ordering?.toNeoForgeDependencyOrdering(),
	side = side?.toNeoForgeDependencySide(),
	referralUrl = referralUrl,
)

internal fun ForgeModsToml.toTomlString(): String {
	val tomlFormat = Toml()
	return tomlFormat.encodeToString(this)
}
