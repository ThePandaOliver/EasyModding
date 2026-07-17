package dev.pandasystems.easymodding.tasks

import dev.pandasystems.easymodding.data.loadEasyModdingConfig
import dev.pandasystems.easymodding.data.populateForgeModToml
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

/**
 * Gradle task that generates legacy Forge's `META-INF/mods.toml` from the unified
 * `easymodding.mod.json`.
 *
 * This only writes `mods.toml`; `pack.mcmeta` is generated separately by
 * [GeneratePackMcmetaTask], since it's shared across every loader and lives at the resources root
 * rather than under `META-INF`. Registered as `generateForgeModsToml` by
 * [dev.pandasystems.easymodding.EasyModdingPlugin] and depended on (alongside
 * [GeneratePackMcmetaTask]) by the `generateForgeResources` lifecycle task. It is cacheable and
 * only re-runs when the input config changes.
 */
@CacheableTask
abstract class GenerateForgeModsTomlTask : DefaultTask() {
    /** The unified config file (`easymodding.mod.json`) to read from. */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val configFile: RegularFileProperty

    /** The directory into which the generated `META-INF/mods.toml` is written. */
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        val config = loadEasyModdingConfig(configFile.get().asFile)
        val dir = outputDir.get().asFile
        dir.mkdirs()

        val metainfDir = dir.resolve("META-INF")
        metainfDir.mkdirs()

        val metadataFile = metainfDir.resolve("mods.toml")
        metadataFile.writeText(config.populateForgeModToml().toTomlString())
    }
}
