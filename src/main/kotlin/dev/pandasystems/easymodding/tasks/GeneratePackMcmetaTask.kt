package dev.pandasystems.easymodding.tasks

import dev.pandasystems.easymodding.data.loadEasyModdingConfig
import dev.pandasystems.easymodding.data.populatePackJson
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
 * Gradle task that generates the shared `pack.mcmeta` from the unified `easymodding.mod.json`.
 *
 * Unlike `fabric.mod.json`/`neoforge.mods.toml`/`mods.toml`, `pack.mcmeta` is a vanilla Minecraft
 * resource/data pack descriptor with the same meaning on every loader, and it lives at the root of
 * the resources directory (next to `assets`/`data`), **not** under `META-INF`. It is therefore
 * generated exactly once by this dedicated task rather than by each loader-specific resource task.
 * Previously every enabled loader's task wrote its own copy of `META-INF/pack.mcmeta`, which made
 * `processResources` see the same destination path contributed more than once whenever more than
 * one loader was enabled (e.g. a shared/common module preparing metadata for both NeoForge and
 * Forge), failing the build with a duplicate-entry error.
 *
 * Registered as `generatePackResources` by [dev.pandasystems.easymodding.EasyModdingPlugin] and
 * hooked into `processResources`. It is cacheable and only re-runs when the input config changes.
 */
@CacheableTask
abstract class GeneratePackMcmetaTask : DefaultTask() {
    /** The unified config file (`easymodding.mod.json`) to read from. */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val configFile: RegularFileProperty

    /** The directory into which the generated `pack.mcmeta` is written. */
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        val config = loadEasyModdingConfig(configFile.get().asFile)
        val dir = outputDir.get().asFile
        dir.mkdirs()

        val file = dir.resolve("pack.mcmeta")
        file.writeText(config.populatePackJson().toJsonString())
    }
}
