package dev.pandasystems.easymodding.platform.moddev

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType

class EasyModdingModdevPlugin : BaseEasyModdingPlatformPlugin() {
    override fun apply(target: Project) {
        target.pluginManager.apply("net.neoforged.moddev")
        val extension = target.easyModding
        val neoForgeExtension = target.extensions.getByType(NeoForgeExtension::class.java)
        val javaExtension = target.extensions.getByType<JavaPluginExtension>()

        // Only override the ModDev version when the user actually specified one via
        // `easyModding { neoForge { neoForgeVersion.set(...) } }`.
        target.afterEvaluate {
            extension.neoForge.neoForgeVersion.orNull?.let { neoForgeExtension.version = it }

            extension.runs.forEach {
                neoForgeExtension.runs.create(it.name) {
                    ideName.convention(it.name)
                    gameDirectory.convention(it.workingDirectory)
                    systemProperties.convention(it.systemProperties)
                    jvmArguments.convention(it.jvmArguments)
                    programArguments.convention(it.programArguments)
                    sourceSet.convention(javaExtension.sourceSets[it.sourceSet.get()])
                }
            }
        }
    }
}
