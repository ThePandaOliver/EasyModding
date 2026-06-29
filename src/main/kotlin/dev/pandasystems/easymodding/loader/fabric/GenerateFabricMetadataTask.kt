package dev.pandasystems.easymodding.loader.fabric

import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.io.File

abstract class GenerateFabricMetadataTask : GenerateMetadataTask() {
	override fun writeMetadata(outputDir: File) {

		val jsonFile = File(outputDir, "fabric.mod.json")

		val json = buildJsonObject {
			put("schemaVersion", 1)
			modId.orNull?.let { put("id", it) }
			modVersion.orNull?.let { put("version", it) }
			modName.orNull?.let { put("name", it) }
			modDescription.orNull?.let { put("description", it) }
		}

		val jsonFormat = Json { prettyPrint = true }
		jsonFile.writeText(jsonFormat.encodeToString(json))
	}
}
