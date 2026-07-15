package dev.pandasystems.easymodding.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Data model of Minecraft's `pack.mcmeta` resource/data pack descriptor.
 *
 * Minecraft has two shapes for declaring the supported pack format: the legacy single
 * `pack_format` and the modern `min_format`/`max_format` range. The [legacy] and [modern]
 * accessors normalize whatever was configured into a single consistent representation, filling in
 * missing sides of the range where necessary.
 */
@Serializable
data class PackMcmeta(
	val pack: PackData
) {
	/** This descriptor coerced to the legacy single `pack_format` form. */
	internal val legacy: PackMcmeta
		get() = PackMcmeta(let {
			if (pack.packFormat != null) PackData(packFormat = pack.packFormat)
			else if (pack.minFormat != null || pack.maxFormat != null)
				PackData(packFormat = pack.minFormat ?: pack.maxFormat)
			else throw IllegalArgumentException("Pack format not specified")
		})

	/** This descriptor coerced to the modern `min_format`/`max_format` range form. */
	internal val modern: PackMcmeta
		get() = PackMcmeta(let {
			if (pack.minFormat != null || pack.maxFormat != null)
				PackData(minFormat = pack.minFormat ?: pack.maxFormat, maxFormat = pack.maxFormat ?: pack.minFormat)
			else if (pack.packFormat != null) PackData(minFormat = pack.packFormat, maxFormat = pack.packFormat)
			else throw IllegalArgumentException("Pack format not specified")
		})
}

/** The `pack` object inside `pack.mcmeta`, holding the description and format(s). */
@Serializable
data class PackData(
	val description: String? = null,
	@SerialName("pack_format")
	val packFormat: Float? = null,
	@SerialName("min_format")
	val minFormat: Float? = null,
	@SerialName("max_format")
	val maxFormat: Float? = null,

)

/** Builds a [PackMcmeta] from the [EasyModdingConfig.pack] section. */
internal fun EasyModdingConfig.populatePackJson(): PackMcmeta {
	return PackMcmeta(
		pack = PackData(
			description = pack?.description,
			packFormat = pack?.packFormat,
			minFormat = pack?.minFormat,
			maxFormat = pack?.maxFormat
		)
	)
}

/** Serializes this [PackMcmeta] to a pretty-printed JSON string for writing to disk. */
internal fun PackMcmeta.toJsonString(): String {
	val jsonFormat = Json {
		encodeDefaults = true
		ignoreUnknownKeys = true
		prettyPrint = true
		explicitNulls = false
	}
	return jsonFormat.encodeToString(this)
}