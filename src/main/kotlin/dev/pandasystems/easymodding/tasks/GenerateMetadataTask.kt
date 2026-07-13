package dev.pandasystems.easymodding.tasks

import dev.pandasystems.easymodding.EasyModdingConfig
import dev.pandasystems.easymodding.loader.fabric.populateFabricModJson
import dev.pandasystems.easymodding.loader.fabric.toJsonString
import dev.pandasystems.easymodding.loader.neoforge.populateNeoForgeModToml
import dev.pandasystems.easymodding.loader.neoforge.toTomlString
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

@CacheableTask
abstract class GenerateMetadataTask : DefaultTask() {
    @get:Input
    abstract val config: Property<EasyModdingConfig>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Input
    abstract val enableFabric: Property<Boolean>

    @get:Input
    abstract val enableNeoForge: Property<Boolean>

    @TaskAction
    fun run() {
        val outputDirectory = this@GenerateMetadataTask.outputDirectory.asFile.get()
        if (outputDirectory.exists()) outputDirectory.deleteRecursively()
        outputDirectory.mkdirs()

        if (enableFabric.get()) {
            val fabricFile = File(outputDirectory, "fabric.mod.json")
            fabricFile.writeText(config.get().populateFabricModJson().toJsonString())
        }
        if (enableNeoForge.get()) {
            val neoforgeFile = File(outputDirectory, "neoforge.mod.json")
            neoforgeFile.writeText(config.get().populateNeoForgeModToml().toTomlString())
        }
    }
}