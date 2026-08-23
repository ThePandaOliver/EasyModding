package dev.pandasystems.easymodding.platform.loom.remap

import dev.pandasystems.easymodding.platform.base.EasyModdingExtension
import dev.pandasystems.easymodding.platform.base.EasyModdingPlugin
import dev.pandasystems.easymodding.platform.base.easyModding
import dev.pandasystems.easymodding.util.setOrElseCurrent
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Project

class EasyModdingLoomRemapPlugin : EasyModdingPlugin() {
    override val pluginExtensionClass: Class<out EasyModdingExtension> = EasyModdingLoomRemapExtension::class.java

    override fun apply(target: Project) {
        target.pluginManager.apply("net.fabricmc.fabric-loom-remap")

        super.apply(target)

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
            // Create run configs
            extension.runs.forEach {
                loom.runConfigs.maybeCreate(it.name)
                loom.runConfigs.named(it.name) {
                    displayName.convention(it.name)
                    runDirectory.setOrElseCurrent(target.objects, it.workingDirectory)
                    systemProperties.convention(it.systemProperties)
                    jvmArguments.convention(it.jvmArguments)
                    programArguments.convention(it.programArguments)
                    it.sourceSet.orNull?.let { sourceSetName ->
                        sourceSet.set(sourceSetName)
                    }
                    generateRunConfig.convention(true)
                }
            }
        }
    }
}
