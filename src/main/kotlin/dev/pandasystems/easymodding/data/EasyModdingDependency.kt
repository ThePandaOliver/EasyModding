package dev.pandasystems.easymodding.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EasyModdingDependency(

	val modId: String,

	val type: EasyModdingDependencyType = EasyModdingDependencyType.Required,

	val versionRange: String? = null,

	val reason: String? = null,

	val ordering: EasyModdingDependencyOrdering? = null,

	val side: EasyModdingDependencySide? = null,

	val referralUrl: String? = null,
)

@Serializable
enum class EasyModdingDependencyType {
	@SerialName("required") Required,
	@SerialName("optional") Optional,
	@SerialName("incompatible") Incompatible,
	@SerialName("discouraged") Discouraged,
}

@Serializable
enum class EasyModdingDependencyOrdering {
	@SerialName("before") Before,
	@SerialName("after") After,
	@SerialName("none") None,
}

@Serializable
enum class EasyModdingDependencySide {
	@SerialName("client") Client,
	@SerialName("server") Server,
	@SerialName("both") Both,
}

internal fun EasyModdingDependencyType.toNeoForgeDependencyType(): NeoForgeDependencyType = when (this) {
	EasyModdingDependencyType.Required -> NeoForgeDependencyType.Required
	EasyModdingDependencyType.Optional -> NeoForgeDependencyType.Optional
	EasyModdingDependencyType.Incompatible -> NeoForgeDependencyType.Incompatible
	EasyModdingDependencyType.Discouraged -> NeoForgeDependencyType.Discouraged
}

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

internal fun EasyModdingDependencyOrdering.toNeoForgeDependencyOrdering(): NeoForgeDependencyOrdering = when (this) {
	EasyModdingDependencyOrdering.Before -> NeoForgeDependencyOrdering.Before
	EasyModdingDependencyOrdering.After -> NeoForgeDependencyOrdering.After
	EasyModdingDependencyOrdering.None -> NeoForgeDependencyOrdering.None
}

internal fun EasyModdingDependencySide.toNeoForgeDependencySide(): NeoForgeDependencySide = when (this) {
	EasyModdingDependencySide.Client -> NeoForgeDependencySide.Client
	EasyModdingDependencySide.Server -> NeoForgeDependencySide.Server
	EasyModdingDependencySide.Both -> NeoForgeDependencySide.Both
}
