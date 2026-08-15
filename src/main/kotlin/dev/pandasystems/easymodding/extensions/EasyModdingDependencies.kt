package dev.pandasystems.easymodding.extensions

import org.gradle.api.Project
import javax.inject.Inject

abstract class EasyModdingDependencies @Inject constructor(
	private val project: Project
) {

	private enum class Platform {
		FABRIC, FABRIC_REMAP, NEOFORGE, FORGE, UNKNOWN
	}


	private val platform: Platform by lazy {
		when {
			project.pluginManager.hasPlugin("net.fabricmc.fabric-loom") -> Platform.FABRIC
			project.pluginManager.hasPlugin("net.fabricmc.fabric-loom-remap") -> Platform.FABRIC_REMAP
			project.pluginManager.hasPlugin("net.neoforged.moddev") -> Platform.NEOFORGE
			project.pluginManager.hasPlugin("net.minecraftforge.gradle") -> Platform.FORGE
			else -> Platform.UNKNOWN
		}
	}


	fun modImplementation(notation: Any) {
		when (platform) {
			Platform.FABRIC_REMAP -> project.dependencies.add("modImplementation", notation)
			Platform.FABRIC, Platform.NEOFORGE, Platform.FORGE -> project.dependencies.add("implementation", notation)
			Platform.UNKNOWN -> throw IllegalStateException(
				"Cannot add mod dependency: No supported mod loader plugin detected. " +
				"Please ensure either fabric-loom, neoforged-moddev, or forgegradle is applied."
			)
		}
	}


	fun modApi(notation: Any) {
		when (platform) {
			Platform.FABRIC_REMAP -> project.dependencies.add("modApi", notation)
			Platform.FABRIC, Platform.NEOFORGE, Platform.FORGE -> {
				// Ensure java-library plugin is applied for api configuration
				if (!project.pluginManager.hasPlugin("java-library")) {
					project.pluginManager.apply("java-library")
				}
				project.dependencies.add("api", notation)
			}
			Platform.UNKNOWN -> throw IllegalStateException(
				"Cannot add mod API dependency: No supported mod loader plugin detected."
			)
		}
	}


	fun modCompileOnly(notation: Any) {
		when (platform) {
			Platform.FABRIC_REMAP -> project.dependencies.add("modCompileOnly", notation)
			Platform.FABRIC, Platform.NEOFORGE, Platform.FORGE -> project.dependencies.add("compileOnly", notation)
			Platform.UNKNOWN -> throw IllegalStateException(
				"Cannot add mod compile-only dependency: No supported mod loader plugin detected."
			)
		}
	}


	fun modLocalRuntime(notation: Any) {
		when (platform) {
			Platform.FABRIC_REMAP -> project.dependencies.add("modLocalRuntime", notation)
			Platform.FABRIC, Platform.NEOFORGE, Platform.FORGE -> project.dependencies.add("runtimeOnly", notation)
			Platform.UNKNOWN -> throw IllegalStateException(
				"Cannot add mod local runtime dependency: No supported mod loader plugin detected."
			)
		}
	}


	fun library(notation: Any) {
		project.dependencies.add("implementation", notation)
	}


	fun includeLibrary(notation: Any) {
		// First add as implementation
		library(notation)

		// Then add to jar-in-jar configuration
		when (platform) {
			Platform.FABRIC, Platform.FABRIC_REMAP -> {
				project.dependencies.add("include", notation)
			}
			Platform.NEOFORGE, Platform.FORGE -> {
				// NeoForge ModDev and ForgeGradle use jarJar
				try {
					project.dependencies.add("jarJar", notation)
				} catch (e: Exception) {
					project.logger.warn(
						"Failed to add jarJar dependency. Make sure jarJar is configured properly in your build script.",
						e
					)
				}
			}
			Platform.UNKNOWN -> throw IllegalStateException(
				"Cannot include library: No supported mod loader plugin detected."
			)
		}
	}


	fun includeMod(notation: Any) {
		// First add as mod dependency
		modImplementation(notation)

		// Then add to jar-in-jar configuration
		when (platform) {
			Platform.FABRIC, Platform.FABRIC_REMAP -> {
				project.dependencies.add("include", notation)
			}
			Platform.NEOFORGE, Platform.FORGE -> {
				try {
					project.dependencies.add("jarJar", notation)
				} catch (e: Exception) {
					project.logger.warn(
						"Failed to add jarJar dependency. Make sure jarJar is configured properly in your build script.",
						e
					)
				}
			}
			Platform.UNKNOWN -> throw IllegalStateException(
				"Cannot include mod: No supported mod loader plugin detected."
			)
		}
	}


	fun libraryCompileOnly(notation: Any) {
		project.dependencies.add("compileOnly", notation)
	}


	fun libraryRuntimeOnly(notation: Any) {
		project.dependencies.add("runtimeOnly", notation)
	}
}
