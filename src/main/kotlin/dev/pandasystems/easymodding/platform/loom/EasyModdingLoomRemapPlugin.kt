package dev.pandasystems.easymodding.platform.loom

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Project

class EasyModdingLoomRemapPlugin : BaseEasyModdingPlatformPlugin() {
    override fun apply(target: Project) {
        target.pluginManager.apply("net.fabricmc.fabric-loom-remap")
        val extension = target.easyModding
        val loom = target.extensions.getByType(LoomGradleExtensionAPI::class.java)

        target.dependencies.add(
            "minecraft",
            extension.minecraftVersion.map { version ->
                "com.mojang:minecraft:$version"
            },
        )
        target.dependencies.add("mappings", loom.officialMojangMappings())

        target.afterEvaluate {
            extension.runs.forEach {
                loom.runConfigs.maybeCreate(it.name)
                loom.runConfigs.named(it.name) {
                    displayName.convention(it.name)
                    runDirectory.convention(it.workingDirectory)
                    systemProperties.convention(it.systemProperties)
                    jvmArguments.convention(it.jvmArguments)
                    programArguments.convention(it.programArguments)
                    sourceSet.convention(it.sourceSet)
                    generateRunConfig.convention(true)
                }
            }
        }
    }
}
