package dev.pandasystems.easymodding.tasks

import dev.pandasystems.easymodding.data.loadEasyModdingConfig
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

/**
 * Gradle task that generates the Fabric `fabric.mod.json` from the unified `easymodding.mod.json`.
 *
 * Registered as `generateFabricResources` by [dev.pandasystems.easymodding.EasyModdingPlugin] and
 * hooked into `processResources`. It is cacheable and only re-runs when the input config changes.
 */
@CacheableTask
abstract class GenerateFabricResourcesTask : DefaultTask() {
    /** The unified config file (`easymodding.mod.json`) to read from. */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val configFile: RegularFileProperty

    /** The directory into which the generated `fabric.mod.json` is written. */
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
