package dev.pandasystems.easymodding.platform.base

import dev.pandasystems.easymodding.tasks.GenerateFabricModJsonTask
import dev.pandasystems.easymodding.tasks.GenerateForgeModsTomlTask
import dev.pandasystems.easymodding.tasks.GenerateNeoForgeModsTomlTask
import dev.pandasystems.easymodding.tasks.GeneratePackMcmetaTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources

abstract class EasyModdingPlugin : Plugin<Project> {
	abstract val pluginExtensionClass: Class<out EasyModdingExtension>

	override fun apply(target: Project) {
		target.pluginManager.apply("java")
		target.pluginManager.apply("java-library")
		target.pluginManager.apply("idea")

		val extension = target.extensions.create("easyModding", pluginExtensionClass)

		val generateFabricModJson =
			target.tasks.register("generateFabricModJson", GenerateFabricModJsonTask::class.java) {
				configFile.convention(extension.configPath)
				outputDir.convention(target.layout.buildDirectory.dir("generated/easy-modding/fabric/resources"))
				onlyIf { extension.fabric.enabled.getOrElse(false) }
			}

		val generateNeoForgeModsToml =
			target.tasks.register("generateNeoForgeModsToml", GenerateNeoForgeModsTomlTask::class.java) {
				configFile.convention(extension.configPath)
				outputDir.convention(target.layout.buildDirectory.dir("generated/easy-modding/neoforge/resources"))
				onlyIf { extension.neoForge.enabled.getOrElse(false) }
			}

		val generateForgeModsToml =
			target.tasks.register("generateForgeModsToml", GenerateForgeModsTomlTask::class.java) {
				configFile.convention(extension.configPath)
				outputDir.convention(target.layout.buildDirectory.dir("generated/easy-modding/forge/resources"))
				onlyIf { extension.forge.enabled.getOrElse(false) }
			}

		val generatePackResources =
			target.tasks.register("generatePackResources", GeneratePackMcmetaTask::class.java) {
				configFile.convention(extension.configPath)
				outputDir.convention(target.layout.buildDirectory.dir("generated/easy-modding/pack/resources"))
				onlyIf {
					extension.neoForge.enabled.getOrElse(false) ||
							extension.forge.enabled.getOrElse(false)
				}
			}

		val generateFabricResources = target.tasks.register("generateFabricResources") {
			group = "easymodding"
			description = "Generates every Fabric metadata file (fabric.mod.json)."
			dependsOn(generateFabricModJson)
		}
		val generateNeoForgeResources = target.tasks.register("generateNeoForgeResources") {
			group = "easymodding"
			description = "Generates every NeoForge metadata file (neoforge.mods.toml, pack.mcmeta)."
			dependsOn(generateNeoForgeModsToml, generatePackResources)
		}
		val generateForgeResources = target.tasks.register("generateForgeResources") {
			group = "easymodding"
			description = "Generates every Forge metadata file (mods.toml, pack.mcmeta)."
			dependsOn(generateForgeModsToml, generatePackResources)
		}

		target.tasks.named("processResources", ProcessResources::class.java) {
			dependsOn(
				generateFabricResources,
				generateNeoForgeResources,
				generateForgeResources
			)
			from(
				generateFabricModJson,
				generateNeoForgeModsToml,
				generateForgeModsToml,
				generatePackResources
			)
		}
	}
}
