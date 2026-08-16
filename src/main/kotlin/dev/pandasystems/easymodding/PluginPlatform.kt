package dev.pandasystems.easymodding

enum class PluginPlatform {
	FABRIC_LOOM,
	FABRIC_LOOM_REMAP,
	MODDEV,
	FORGE_GRADLE;

	val isLoom: Boolean
		get() = this == FABRIC_LOOM

	val isLoomRemap: Boolean
		get() = this == FABRIC_LOOM_REMAP

	val isAnyLoom: Boolean
		get() = isLoom || isLoomRemap

	val isForgeGradle: Boolean
		get() = this == FORGE_GRADLE

	val isModDev: Boolean
		get() = this == MODDEV
}