package dev.pandasystems.easymodding.loader

import dev.pandasystems.easymodding.EasyModdingConfig
import dev.pandasystems.easymodding.EasyModdingExtension
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask
abstract class GenerateMetadataTask : DefaultTask() {
    @get:Input
    abstract val config: Property<EasyModdingConfig>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    abstract fun writeMetadata(outputDir: File)

    @TaskAction
    fun run() {
        val outputDir = outputDirectory.get().asFile
        if (!outputDir.exists()) outputDir.mkdirs()
        writeMetadata(outputDir)
    }
}
