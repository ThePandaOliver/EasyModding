package dev.pandasystems.easymodding.loader

import dev.pandasystems.easymodding.EasyModdingExtension
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateMetadataTask : DefaultTask() {
    @get:Internal
    abstract val extension: Property<EasyModdingExtension>

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
