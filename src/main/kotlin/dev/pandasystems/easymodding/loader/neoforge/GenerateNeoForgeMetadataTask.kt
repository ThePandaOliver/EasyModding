package dev.pandasystems.easymodding.loader.neoforge

import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import org.gradle.api.tasks.CacheableTask
import java.io.File

@CacheableTask
abstract class GenerateNeoForgeMetadataTask : GenerateMetadataTask() {
	override fun writeMetadata(outputDir: File) {
		val extension = extension.get()
		val jsonFile = File(outputDir, "META-INF/neoforge.mods.toml")
		if (!jsonFile.parentFile.exists()) {
			jsonFile.parentFile.mkdirs()
		}
		jsonFile.writeText(extension.config.get().populateNeoForgeModToml().toTomlString())
	}
}
