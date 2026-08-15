package dev.pandasystems.easymodding.tasks

import dev.pandasystems.easymodding.data.loadEasyModdingConfig
import dev.pandasystems.easymodding.data.populateNeoForgeModToml
import dev.pandasystems.easymodding.data.toTomlString
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateNeoForgeModsTomlTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val configFile: RegularFileProperty


    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        val config = loadEasyModdingConfig(configFile.get().asFile)
        val dir = outputDir.get().asFile
        dir.mkdirs()

        val metainfDir = dir.resolve("META-INF")
        metainfDir.mkdirs()

        val metadataFile = metainfDir.resolve("neoforge.mods.toml")
        metadataFile.writeText(config.populateNeoForgeModToml().toTomlString())
    }
}
