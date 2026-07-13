package dev.pandasystems.easymodding.loader.neoforge

import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import org.gradle.api.tasks.CacheableTask
import java.io.File

@CacheableTask
abstract class GenerateNeoForgeMetadataTask : GenerateMetadataTask() {
	override fun writeMetadata(outputFile: File) {
		outputFile.writeText(config.get().populateNeoForgeModToml().toTomlString())
	}
}
