package dev.pandasystems.easymodding.extensions

import dev.pandasystems.easymodding.PluginPlatform
import org.gradle.api.Project
import javax.inject.Inject

abstract class EasyModdingDependencies @Inject constructor(
	private val project: Project
) {
	private val platform: PluginPlatform by lazy { project.easyModding.platform }


	fun modImplementation(notation: Any) {
		when (platform) {
			PluginPlatform.FABRIC_LOOM_REMAP -> project.dependencies.add("modImplementation", notation)
			else -> project.dependencies.add("implementation", notation)
		}
	}


	fun modApi(notation: Any) {
		when (platform) {
			PluginPlatform.FABRIC_LOOM_REMAP -> project.dependencies.add("modApi", notation)
			else -> {
				project.dependencies.add("api", notation)
			}
		}
	}


	fun modCompileOnly(notation: Any) {
		when (platform) {
			PluginPlatform.FABRIC_LOOM_REMAP -> project.dependencies.add("modCompileOnly", notation)
			else -> project.dependencies.add("compileOnly", notation)
		}
	}


	fun modLocalRuntime(notation: Any) {
		when (platform) {
			PluginPlatform.FABRIC_LOOM_REMAP -> project.dependencies.add("modLocalRuntime", notation)
			else -> project.dependencies.add("runtimeOnly", notation)
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
			PluginPlatform.FABRIC_LOOM, PluginPlatform.FABRIC_LOOM_REMAP -> {
				project.dependencies.add("include", notation)
			}
			PluginPlatform.MODDEV, PluginPlatform.FORGE_GRADLE -> {
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
			else -> throw IllegalStateException(
				"Cannot include library: No supported mod loader plugin detected."
			)
		}
	}


	fun includeMod(notation: Any) {
		// First add as mod dependency
		modImplementation(notation)

		// Then add to jar-in-jar configuration
		when (platform) {
			PluginPlatform.FABRIC_LOOM, PluginPlatform.FABRIC_LOOM_REMAP -> {
				project.dependencies.add("include", notation)
			}
			PluginPlatform.MODDEV, PluginPlatform.FORGE_GRADLE -> {
				try {
					project.dependencies.add("jarJar", notation)
				} catch (e: Exception) {
					project.logger.warn(
						"Failed to add jarJar dependency. Make sure jarJar is configured properly in your build script.",
						e
					)
				}
			}
			else -> throw IllegalStateException(
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
