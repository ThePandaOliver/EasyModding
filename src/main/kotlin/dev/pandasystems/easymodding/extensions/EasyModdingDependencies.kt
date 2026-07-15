package dev.pandasystems.easymodding.extensions

import org.gradle.api.Project
import javax.inject.Inject

/**
 * Unified, cross-platform dependency declaration API (`easyModding { dependencies { } }`).
 *
 * The whole point of this class is to let a mod author write a single set of dependency
 * declarations that map to the correct loader-specific Gradle configurations regardless of the
 * target platform. For example [modImplementation] maps to Loom's `modImplementation` on Fabric
 * but to plain `implementation` on NeoForge/Forge.
 *
 * The active [platform] is detected lazily by inspecting which loader plugin has been applied, so
 * these methods must be called after the platform sub-plugin is applied (which the main plugin
 * guarantees when `easy_modding.platform` is set). If no supported loader is detected the mod-*
 * and include-* methods throw a descriptive [IllegalStateException].
 */
abstract class EasyModdingDependencies @Inject constructor(
	private val project: Project
) {
	/** The loaders whose Gradle configuration names this API knows how to map to. */
	private enum class Platform {
		FABRIC, NEOFORGE, FORGE, UNKNOWN
	}

	/**
	 * Detects the active platform from the applied Gradle plugins. Evaluated lazily so detection
	 * happens the first time a dependency method is invoked, by which point the loader plugin has
	 * been applied.
	 */
	private val platform: Platform by lazy {
		when {
			project.pluginManager.hasPlugin("net.fabricmc.fabric-loom") -> Platform.FABRIC
			project.pluginManager.hasPlugin("net.neoforged.moddev") -> Platform.NEOFORGE
			project.pluginManager.hasPlugin("net.minecraftforge.gradle") -> Platform.FORGE
			else -> Platform.UNKNOWN
		}
	}

	/**
	 * Adds a mod dependency that will be available at compile time and runtime.
	 * 
	 * For Fabric: Uses `modImplementation` configuration
	 * For NeoForge/Forge: Uses `implementation` configuration
	 */
	fun modImplementation(notation: Any) {
		when (platform) {
			Platform.FABRIC -> project.dependencies.add("modImplementation", notation)
			Platform.NEOFORGE, Platform.FORGE -> project.dependencies.add("implementation", notation)
			Platform.UNKNOWN -> throw IllegalStateException(
				"Cannot add mod dependency: No supported mod loader plugin detected. " +
				"Please ensure either fabric-loom, neoforged-moddev, or forgegradle is applied."
			)
		}
	}

	/**
	 * Adds a mod dependency that will be available at compile time and runtime, 
	 * and exposes its API to other mods.
	 * 
	 * For Fabric: Uses `modApi` configuration
	 * For NeoForge/Forge: Uses `api` configuration (requires java-library plugin)
	 */
	fun modApi(notation: Any) {
		when (platform) {
			Platform.FABRIC -> project.dependencies.add("modApi", notation)
			Platform.NEOFORGE, Platform.FORGE -> {
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

	/**
	 * Adds a mod dependency that will be available only at compile time.
	 * 
	 * For Fabric: Uses `modCompileOnly` configuration
	 * For NeoForge/Forge: Uses `compileOnly` configuration
	 */
	fun modCompileOnly(notation: Any) {
		when (platform) {
			Platform.FABRIC -> project.dependencies.add("modCompileOnly", notation)
			Platform.NEOFORGE, Platform.FORGE -> project.dependencies.add("compileOnly", notation)
			Platform.UNKNOWN -> throw IllegalStateException(
				"Cannot add mod compile-only dependency: No supported mod loader plugin detected."
			)
		}
	}

	/**
	 * Adds a mod dependency that will be available only at runtime (for development environment).
	 * 
	 * For Fabric: Uses `modLocalRuntime` configuration
	 * For NeoForge/Forge: Uses `runtimeOnly` configuration
	 */
	fun modLocalRuntime(notation: Any) {
		when (platform) {
			Platform.FABRIC -> project.dependencies.add("modLocalRuntime", notation)
			Platform.NEOFORGE, Platform.FORGE -> project.dependencies.add("runtimeOnly", notation)
			Platform.UNKNOWN -> throw IllegalStateException(
				"Cannot add mod local runtime dependency: No supported mod loader plugin detected."
			)
		}
	}

	/**
	 * Adds a non-mod library dependency that will be available at compile time and runtime.
	 * This is for regular Java/Kotlin libraries that are not mods.
	 *
	 * Uses the standard `implementation` configuration on every platform. Note this does NOT bundle
	 * the library into the output jar; use [includeLibrary] if you want jar-in-jar bundling.
	 */
	fun library(notation: Any) {
		project.dependencies.add("implementation", notation)
	}

	/**
	 * Adds a non-mod library dependency that will be bundled with your mod (jar-in-jar).
	 * 
	 * For Fabric: Uses `include` configuration
	 * For NeoForge: Uses `jarJar` configuration
	 * For Forge: Uses `jarJar` configuration
	 */
	fun includeLibrary(notation: Any) {
		// First add as implementation
		library(notation)
		
		// Then add to jar-in-jar configuration
		when (platform) {
			Platform.FABRIC -> {
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

	/**
	 * Adds a mod dependency and bundles it with your mod (jar-in-jar).
	 * This will both add the mod as a dependency and include it in your output jar.
	 * 
	 * For Fabric: Uses `modImplementation` + `include` configurations
	 * For NeoForge/Forge: Uses `implementation` + `jarJar` configurations
	 */
	fun includeMod(notation: Any) {
		// First add as mod dependency
		modImplementation(notation)
		
		// Then add to jar-in-jar configuration
		when (platform) {
			Platform.FABRIC -> {
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

	/**
	 * Adds a non-mod library dependency that will be available only at compile time.
	 * Uses the standard `compileOnly` configuration on every platform.
	 */
	fun libraryCompileOnly(notation: Any) {
		project.dependencies.add("compileOnly", notation)
	}

	/**
	 * Adds a non-mod library dependency that will be available only at runtime.
	 * Uses the standard `runtimeOnly` configuration on every platform.
	 */
	fun libraryRuntimeOnly(notation: Any) {
		project.dependencies.add("runtimeOnly", notation)
	}
}