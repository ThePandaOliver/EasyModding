package dev.pandasystems.easymodding.tasks

import dev.pandasystems.easymodding.data.loadEasyModdingConfig
import dev.pandasystems.easymodding.data.populateNeoForgeModToml
import dev.pandasystems.easymodding.data.populatePackJson
import dev.pandasystems.easymodding.data.toJsonString
import dev.pandasystems.easymodding.data.toTomlString
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * Gradle task that generates the NeoForge metadata from the unified `easymodding.mod.json`.
 *
 * Writes `META-INF/neoforge.mods.toml` and `META-INF/pack.mcmeta`. Registered as
 * `generateNeoForgeResources` by [dev.pandasystems.easymodding.EasyModdingPlugin] and hooked into
 * `processResources`. It is cacheable and only re-runs when the input config changes.
 */
@CacheableTask
abstract class GenerateNeoForgeResourcesTask : DefaultTask() {
    /** The unified config file (`easymodding.mod.json`) to read from. */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val configFile: RegularFileProperty

    /** The directory into which the generated `META-INF` metadata files are written. */
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

        val packFile = metainfDir.resolve("pack.mcmeta")
        packFile.writeText(config.populatePackJson().toJsonString())
    }
}
