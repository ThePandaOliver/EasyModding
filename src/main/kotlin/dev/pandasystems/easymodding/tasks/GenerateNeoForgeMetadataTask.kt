package dev.pandasystems.easymodding.tasks

import dev.pandasystems.easymodding.loadEasyModdingConfig
import dev.pandasystems.easymodding.loader.neoforge.populateNeoForgeModToml
import dev.pandasystems.easymodding.loader.neoforge.toTomlString
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateNeoForgeMetadataTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val configFile: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun run() {
        val config = loadEasyModdingConfig(configFile.get().asFile)
        outputFile.get().asFile.writeText(config.populateNeoForgeModToml().toTomlString())
    }
}
