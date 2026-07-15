package dev.pandasystems.easymodding.platform

import dev.pandasystems.easymodding.extensions.EasyModdingExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Base class shared by all loader-specific EasyModding platform plugins (Loom, ModDev, ForgeGradle).
 *
 * A platform plugin is applied by [dev.pandasystems.easymodding.EasyModdingPlugin] based on the
 * selected `easy_modding.platform`. Its job is to apply the underlying loader Gradle plugin and to
 * wire up loader-specific configuration (such as the Minecraft or NeoForge version) using values
 * read from the shared [EasyModdingExtension].
 *
 * This base provides a convenient [easyModding] accessor so subclasses can reach the shared
 * configuration without repeating the lookup boilerplate.
 */
abstract class BaseEasyModdingPlatformPlugin : Plugin<Project> {
	/** Convenience accessor for the project's [EasyModdingExtension] (`easyModding { }`). */
	protected val Project.easyModding: EasyModdingExtension
		get() = extensions.getByType(EasyModdingExtension::class.java)
}