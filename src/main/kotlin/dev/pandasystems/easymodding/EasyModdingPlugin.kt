package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.extensions.EasyModdingExtension
import dev.pandasystems.easymodding.tasks.GenerateFabricModJsonTask
import dev.pandasystems.easymodding.tasks.GenerateForgeModsTomlTask
import dev.pandasystems.easymodding.tasks.GenerateNeoForgeModsTomlTask
import dev.pandasystems.easymodding.tasks.GeneratePackMcmetaTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources

class EasyModdingPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		val easyModdingExtension = target.extensions.create("easyModding", EasyModdingExtension::class.java)

		target.pluginManager.apply("java")
		target.pluginManager.apply("java-library")
		target.pluginManager.apply("idea")

		when (easyModdingExtension.platform) {
			PluginPlatform.FABRIC_LOOM -> target.pluginManager.apply("dev.pandasystems.easymodding.loom-noremap")
			PluginPlatform.FABRIC_LOOM_REMAP -> target.pluginManager.apply("dev.pandasystems.easymodding.loom-remap")
			PluginPlatform.MODDEV -> target.pluginManager.apply("dev.pandasystems.easymodding.moddev")
			PluginPlatform.FORGE_GRADLE -> target.pluginManager.apply("dev.pandasystems.easymodding.forgegradle")
			else -> {}
		}

		val generateFabricModJson =
			target.tasks.register("generateFabricModJson", GenerateFabricModJsonTask::class.java) {
				configFile.convention(easyModdingExtension.configPath)
				outputDir.convention(target.layout.buildDirectory.dir("generated/easy-modding/fabric/resources"))
				onlyIf { easyModdingExtension.fabric.enabled.getOrElse(false) }
			}

		val generateNeoForgeModsToml =
			target.tasks.register("generateNeoForgeModsToml", GenerateNeoForgeModsTomlTask::class.java) {
				configFile.convention(easyModdingExtension.configPath)
				outputDir.convention(target.layout.buildDirectory.dir("generated/easy-modding/neoforge/resources"))
				onlyIf { easyModdingExtension.neoForge.enabled.getOrElse(false) }
			}

		val generateForgeModsToml =
			target.tasks.register("generateForgeModsToml", GenerateForgeModsTomlTask::class.java) {
				configFile.convention(easyModdingExtension.configPath)
				outputDir.convention(target.layout.buildDirectory.dir("generated/easy-modding/forge/resources"))
				onlyIf { easyModdingExtension.forge.enabled.getOrElse(false) }
			}

		val generatePackResources =
			target.tasks.register("generatePackResources", GeneratePackMcmetaTask::class.java) {
				configFile.convention(easyModdingExtension.configPath)
				outputDir.convention(target.layout.buildDirectory.dir("generated/easy-modding/pack/resources"))
				onlyIf {
					easyModdingExtension.neoForge.enabled.getOrElse(false) ||
							easyModdingExtension.forge.enabled.getOrElse(false)
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
