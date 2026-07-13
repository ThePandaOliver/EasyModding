package dev.pandasystems.easymodding.loader

import dev.pandasystems.easymodding.EasyModdingConfig
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask
abstract class GenerateMetadataTask : DefaultTask() {
    @get:Input
    abstract val config: Property<EasyModdingConfig>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    abstract fun writeMetadata(outputDir: File)

    @TaskAction
    fun run() {
        val outputFile = this@GenerateMetadataTask.outputFile.asFile.get()
        val outputDir = outputFile.parentFile
        if (!outputDir.exists()) outputDir.mkdirs()
        writeMetadata(outputFile)
    }
}
