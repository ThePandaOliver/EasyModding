package dev.pandasystems.easymodding.platform.forgegradle

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import org.gradle.api.Project

class EasyModdingForgeGradlePlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		// Apply ForgeGradle
		target.pluginManager.apply("net.minecraftforge.gradle")

		// Older Forge versions usually require the Mixin Gradle plugin explicitly
		target.pluginManager.apply("org.spongepowered.mixin")

		val extension = target.easyModding

		target.afterEvaluate {
			// Apply the minecraft dependency for ForgeGradle
			// Note: ForgeGradle typically requires the Forge version as well,
			// you might want to add a `forgeVersion` property to your extension.
			// dependencies.add("minecraft", "net.minecraftforge:forge:${extension.minecraftVersion.get()}-...")
		}
	}
}