package dev.pandasystems.easymodding.loader.fabric

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Reference: https://docs.fabricmc.net/develop/loader/fabric-mod-json

@Serializable
data class FabricMetadata(
	val schemaVersion: Int = 1,
	val id: String,
	val version: String,

	val provides: List<String>? = null,
	val environment: FabricEnvironment? = null,
	val entrypoints: Map<String, List<String>>? = null,
	val jars: List<FabricJar>? = null,
	val languageAdapters: Map<String, String>? = null,
	val mixins: List<FabricMixinConfig>? = null,
	val depends: Map<String, String>? = null,
	val recommends: Map<String, String>? = null,
	val suggests: Map<String, String>? = null,
	val breaks: Map<String, String>? = null,
	val conflicts: Map<String, String>? = null,

	val name: String? = null,
	val description: String? = null,
	val contact: Map<String, String>? = null,
	val authors: List<FabricPerson>? = null,
	val contributors: List<FabricPerson>? = null,
	val license: String? = null,
	val icon: String? = null,
)

@Serializable
enum class FabricEnvironment {
	@SerialName("client")
	CLIENT,

	@SerialName("server")
	SERVER,

	@SerialName("*")
	BOTH
}

@Serializable
data class FabricJar(
	val file: String
)

@Serializable
data class FabricMixinConfig(
	val config: String,
	val environment: FabricEnvironment = FabricEnvironment.BOTH
)

data class FabricPerson(
	val name: String,
	val contact: Map<String, String>? = null,
)