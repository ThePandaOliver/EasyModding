package dev.pandasystems.easymodding.platform.loom

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import sun.jvmstat.monitor.MonitoredVmUtil.jvmArgs

class EasyModdingLoomNoremapPlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.fabricmc.fabric-loom")
		val extension = target.easyModding
		val loom = target.extensions.getByType(LoomGradleExtensionAPI::class.java)

		target.afterEvaluate {
			target.dependencies.add(
				"minecraft",
				extension.minecraftVersion.map { version ->
					"com.mojang:minecraft:$version"
				},
			)

			extension.runs.forEach {
				loom.runConfigs.create(it.name) {
					displayName.convention(it.name)
					runDirectory.convention(it.workingDirectory)
					systemProperties.convention(it.systemProperties)
					jvmArguments.convention(it.jvmArguments)
					programArguments.convention(it.programArguments)
					sourceSet.convention(it.sourceSet)
				}
			}
		}
	}
}
