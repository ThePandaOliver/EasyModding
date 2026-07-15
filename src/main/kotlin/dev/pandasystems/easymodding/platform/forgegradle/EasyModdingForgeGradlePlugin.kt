package dev.pandasystems.easymodding.platform.forgegradle

import dev.pandasystems.easymodding.platform.BaseEasyModdingPlatformPlugin
import net.minecraftforge.gradle.ForgeGradleExtension
import net.minecraftforge.gradle.MinecraftExtensionForProject
import org.gradle.api.Project

/**
 * Platform plugin for legacy Forge, backed by ForgeGradle (`net.minecraftforge.gradle`).
 *
 * Registered as `dev.pandasystems.easymodding.forgegradle` and applied automatically by the main
 * plugin when `easy_modding.platform=forgegradle`. It applies ForgeGradle 7, registers the
 * repositories ForgeGradle needs to resolve the generated Minecraft/Forge artifacts, and, once the
 * project is fully configured, declares the Minecraft/Forge dependency using the Minecraft version
 * from the shared `easyModding` extension and the Forge version from `easyModding { forge { } }`.
 */
class EasyModdingForgeGradlePlugin : BaseEasyModdingPlatformPlugin() {
	override fun apply(target: Project) {
		target.pluginManager.apply("net.minecraftforge.gradle")
		val extension = target.easyModding

		val minecraft = target.extensions.getByType(MinecraftExtensionForProject::class.java)
		val forgeGradle = target.extensions.getByType(ForgeGradleExtension::class.java)

		// Repositories ForgeGradle needs to resolve the Minecraft Mavenizer output, Forge, and
		// Minecraft's own library dependencies.
		minecraft.mavenizer(target.repositories)
		target.repositories.maven(forgeGradle.forgeMaven)
		target.repositories.maven(forgeGradle.minecraftLibsMaven)

		// Deferred until after evaluation so `minecraftVersion`/`forge.forgeVersion` set inside
		// the `easyModding { }` block have been populated before we read them.
		target.afterEvaluate {
			val forgeVersion = extension.forge.forgeVersion.orNull
				?: throw IllegalStateException(
					"easyModding { forge { forgeVersion.set(\"...\") } } must be set when " +
						"targeting the ForgeGradle platform."
				)
			dependencies.add(
				"implementation",
				minecraft.dependency("net.minecraftforge:forge:${extension.minecraftVersion.get()}-$forgeVersion")
			)
		}
	}
}
