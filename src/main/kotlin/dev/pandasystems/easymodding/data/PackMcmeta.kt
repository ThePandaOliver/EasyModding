package dev.pandasystems.easymodding.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PackMcmeta(
	val pack: PackData
) {
	internal val legacy: PackMcmeta
		get() = PackMcmeta(let {
			if (pack.packFormat != null) PackData(packFormat = pack.packFormat)
			else if (pack.minFormat != null || pack.maxFormat != null)
				PackData(packFormat = pack.minFormat ?: pack.maxFormat)
			else throw IllegalArgumentException("Pack format not specified")
		})
	internal val modern: PackMcmeta
		get() = PackMcmeta(let {
			if (pack.minFormat != null || pack.maxFormat != null)
				PackData(minFormat = pack.minFormat ?: pack.maxFormat, maxFormat = pack.maxFormat ?: pack.minFormat)
			else if (pack.packFormat != null) PackData(minFormat = pack.packFormat, maxFormat = pack.packFormat)
			else throw IllegalArgumentException("Pack format not specified")
		})

	internal fun toJsonString(): String {
		val jsonFormat = Json {
			encodeDefaults = true
			ignoreUnknownKeys = true
			prettyPrint = true
			explicitNulls = false
		}
		return jsonFormat.encodeToString(this)
	}
}

@Serializable
data class PackData(
	val description: String? = null,
	@SerialName("pack_format")
	val packFormat: Int? = null,
	@SerialName("min_format")
	val minFormat: Int? = null,
	@SerialName("max_format")
	val maxFormat: Int? = null,

)