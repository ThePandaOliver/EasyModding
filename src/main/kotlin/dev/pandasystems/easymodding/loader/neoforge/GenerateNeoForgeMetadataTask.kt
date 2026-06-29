package dev.pandasystems.easymodding.loader.neoforge

import com.akuleshov7.ktoml.Toml
import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File

abstract class GenerateNeoForgeMetadataTask : GenerateMetadataTask() {
    override fun writeMetadata(outputDir: File) {
        val metaInfDir = File(outputDir, "META-INF").apply { mkdirs() }
        val tomlFile = File(metaInfDir, "neoforge.mods.toml")

        val tomlFormat = Toml()
        tomlFile.writeText(tomlFormat.encodeToString(json))
    }
}
