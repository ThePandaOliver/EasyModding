package dev.pandasystems.easymodding.loader.fabric

import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import java.io.File

abstract class GenerateFabricMetadataTask : GenerateMetadataTask() {
	override fun writeMetadata(outputDir: File) {
		val extension = extension.get()

		val jsonFile = File(outputDir, "fabric.mod.json")
		jsonFile.writeText(extension.metadata.get().populateFabricModJson().toJsonString())
	}
}
