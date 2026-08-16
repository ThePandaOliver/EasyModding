package dev.pandasystems.easymodding.platform.loom

import dev.pandasystems.easymodding.extensions.easyModding
import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import dev.pandasystems.easymodding.util.setOrElseCurrent
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import sun.jvmstat.monitor.MonitoredVmUtil.jvmArgs

class EasyModdingLoomNoremapPlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.fabricmc.fabric-loom")
		val extension = target.easyModding
		val loom = target.extensions.getByType(LoomGradleExtensionAPI::class.java)

		target.dependencies.add(
			"minecraft",
			extension.minecraftVersion.map { version ->
				"com.mojang:minecraft:$version"
			},
		)

		target.afterEvaluate {
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
