package dev.pandasystems.easymodding.tasks

import dev.pandasystems.easymodding.loadEasyModdingConfig
import dev.pandasystems.easymodding.loader.fabric.populateFabricModJson
import dev.pandasystems.easymodding.loader.fabric.toJsonString
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateFabricMetadataTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val configFile: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun run() {
        val config = loadEasyModdingConfig(configFile.get().asFile)
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText(config.populateFabricModJson().toJsonString())
    }
}
