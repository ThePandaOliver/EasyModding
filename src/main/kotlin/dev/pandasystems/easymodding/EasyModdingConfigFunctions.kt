package dev.pandasystems.easymodding

import kotlinx.serialization.json.Json
import java.io.File

fun loadEasyModdingConfig(file: File): EasyModdingConfig {
	val json = Json {
		ignoreUnknownKeys = true
	}
	val easyModding = json.decodeFromString<EasyModdingConfig>(file.readText())
	return easyModding
}