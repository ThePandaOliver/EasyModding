package dev.pandasystems.easymodding.platform.forgegradle

import dev.pandasystems.easymodding.platform.base.EasyModdingExtension
import dev.pandasystems.easymodding.platform.base.EasyModdingPlugin
import dev.pandasystems.easymodding.platform.base.easyModding
import net.minecraftforge.gradle.ForgeGradleExtension
import net.minecraftforge.gradle.MinecraftExtensionForProject
import org.gradle.api.Project

class EasyModdingForgeGradlePlugin : EasyModdingPlugin() {
	override val pluginExtensionClass: Class<out EasyModdingExtension> = EasyModdingForgeGradleExtension::class.java

	override fun apply(target: Project) {
		target.pluginManager.apply("net.minecraftforge.gradle")

		super.apply(target)

		val extension = target.easyModding

		val minecraft = target.extensions.getByType(MinecraftExtensionForProject::class.java)
		val forgeGradle = target.extensions.getByType(ForgeGradleExtension::class.java)

		// Repositories ForgeGradle needs to resolve the Minecraft Mavenizer output, Forge, and
		// Minecraft's own library dependencies.
		minecraft.mavenizer(target.repositories)
		target.repositories.maven(forgeGradle.forgeMaven)
		target.repositories.maven(forgeGradle.minecraftLibsMaven)

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

			// Create run configs
			extension.runs.forEach {
				minecraft.runs.create(it.name) {
					workingDir.convention(it.workingDirectory)
					systemProperties.convention(it.systemProperties)
					jvmArgs.convention(it.jvmArguments)
					args.convention(it.programArguments)
				}
			}
		}
	}
}
