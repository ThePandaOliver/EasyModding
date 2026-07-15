package dev.pandasystems.easymodding.platform.forgegradle

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import org.gradle.api.Project

/**
 * Platform plugin for legacy Forge, backed by ForgeGradle (`net.minecraftforge.gradle`).
 *
 * WORK IN PROGRESS: this plugin is not yet registered in `build.gradle.kts` and is therefore not
 * usable as a Gradle plugin. It currently applies ForgeGradle and the SpongePowered Mixin Gradle
 * plugin, but the Minecraft/Forge dependency wiring is still a TODO (see below). Supporting Forge
 * will likely require an additional `forgeVersion` property on the extension.
 */
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