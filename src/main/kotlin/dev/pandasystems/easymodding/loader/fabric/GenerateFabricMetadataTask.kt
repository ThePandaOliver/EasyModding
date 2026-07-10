package dev.pandasystems.easymodding.loader.fabric

import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import org.gradle.api.tasks.CacheableTask
import java.io.File

@CacheableTask
abstract class GenerateFabricMetadataTask : GenerateMetadataTask() {
	override fun writeMetadata(outputDir: File) {
		val extension = extension.get()
		val jsonFile = File(outputDir, "fabric.mod.json")
		jsonFile.writeText(extension.config.get().populateFabricModJson().toJsonString())
	}
}
