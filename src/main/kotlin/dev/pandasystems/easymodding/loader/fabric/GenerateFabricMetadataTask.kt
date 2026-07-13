package dev.pandasystems.easymodding.loader.fabric

import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import org.gradle.api.tasks.CacheableTask
import java.io.File

@CacheableTask
abstract class GenerateFabricMetadataTask : GenerateMetadataTask() {
	override fun writeMetadata(outputFile: File) {
		outputFile.writeText(config.get().populateFabricModJson().toJsonString())
	}
}
