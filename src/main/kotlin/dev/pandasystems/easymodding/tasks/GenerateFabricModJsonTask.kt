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
 * Gradle task that generates `fabric.mod.json` from the unified `easymodding.mod.json`.
 *
 * This is the single metadata file Fabric needs, so unlike NeoForge/Forge there is only one such
 * task. Registered as `generateFabricModJson` by [dev.pandasystems.easymodding.EasyModdingPlugin]
 * and depended on by the `generateFabricResources` lifecycle task. It is cacheable and only
 * re-runs when the input config changes.
 */
@CacheableTask
abstract class GenerateFabricModJsonTask : DefaultTask() {
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
        val dir = outputDir.get().asFile
        dir.mkdirs()

        val file = dir.resolve("fabric.mod.json")
        file.writeText(config.populateFabricModJson().toJsonString())
    }
}
