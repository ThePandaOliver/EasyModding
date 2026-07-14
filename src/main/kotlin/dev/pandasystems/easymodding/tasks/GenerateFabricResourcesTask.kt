package dev.pandasystems.easymodding.tasks

import dev.pandasystems.easymodding.loadEasyModdingConfig
import dev.pandasystems.easymodding.data.populateFabricModJson
import dev.pandasystems.easymodding.data.toJsonString
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
abstract class GenerateFabricResourcesTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val configFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        val config = loadEasyModdingConfig(configFile.get().asFile)
        val file = outputDir.get().asFile
        file.parentFile.mkdirs()
        file.writeText(config.populateFabricModJson().toJsonString())
    }
}
