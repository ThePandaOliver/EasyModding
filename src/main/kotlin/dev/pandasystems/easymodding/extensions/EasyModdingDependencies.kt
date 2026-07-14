package dev.pandasystems.easymodding.extensions

import org.gradle.api.Project
import javax.inject.Inject

abstract class EasyModdingDependencies @Inject constructor(
	private val project: Project
) {
	fun modImplementation(notation: Any) {
		addMappedDependency("implementation", "modImplementation", notation)
	}

	fun modApi(notation: Any) {
		addMappedDependency("api", "modApi", notation)
	}

	fun modCompileOnly(notation: Any) {
		addMappedDependency("compileOnly", "modCompileOnly", notation)
	}

	fun modLocalRuntime(notation: Any) {
		addMappedDependency("runtimeOnly", "modLocalRuntime", notation)
	}

	private fun addMappedDependency(standardConfig: String, fabricConfig: String, notation: Any) {
		val platform = project.findProperty("easy_modding.platform") as? String

		when (platform) {
			"fabric" -> {
				// Fabric Loom provides modImplementation, modApi, etc., which handle remapping.
				project.dependencies.add(fabricConfig, notation)
			}
			"neoforge" -> {
				// ModDevGradle automatically handles remapping on standard configurations.
				project.dependencies.add(standardConfig, notation)
			}
			"forge" -> {
				// ForgeGradle requires dependencies to be wrapped in fg.deobf() for older versions.
				val deobfNotation = try {
					// Dynamically invoke fg.deobf to avoid hard compile-time coupling to ForgeGradle
					val fg = project.dependencies.extensions.findByName("fg")
					if (fg != null) {
						fg.javaClass.getMethod("deobf", Any::class.java).invoke(fg, notation)
					} else {
						notation
					}
				} catch (e: Exception) {
					project.logger.warn("Could not apply fg.deobf to $notation. Falling back to standard notation.")
					notation
				}
				project.dependencies.add(standardConfig, deobfNotation)
			}
			else -> {
				// Fallback if no platform is detected
				project.dependencies.add(standardConfig, notation)
			}
		}
	}
}