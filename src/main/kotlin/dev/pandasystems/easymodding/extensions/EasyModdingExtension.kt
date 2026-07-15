package dev.pandasystems.easymodding.extensions

import org.gradle.api.Action
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

/**
 * The `easyModding { }` configuration DSL exposed on the project by
 * [dev.pandasystems.easymodding.EasyModdingPlugin].
 *
 * This is the single place where a mod project declares its shared settings, which loaders it
 * targets, and its cross-platform dependencies. Example:
 *
 * ```kotlin
 * easyModding {
 *     minecraftVersion.set("1.21.1")
 *     fabric()
 *     dependencies {
 *         modImplementation("net.fabricmc.fabric-api:fabric-api:...")
 *     }
 * }
 * ```
 */
abstract class EasyModdingExtension @Inject constructor(
	objects: ObjectFactory,
	layout: ProjectLayout,
) {
	/** The target Minecraft version (e.g. `"1.21.1"`), consumed by the platform sub-plugins. */
	abstract val minecraftVersion: Property<String>

	/**
	 * Path to the unified mod config file. Defaults to `easymodding.mod.json` in the project
	 * directory. This file is the single source of truth from which the loader metadata files are
	 * generated.
	 */
	abstract val configPath: RegularFileProperty

	/** Fabric loader configuration block (`fabric { }`). */
	val fabric = objects.newInstance(FabricExtension::class.java)

	/** NeoForge loader configuration block (`neoForge { }`). */
	val neoForge = objects.newInstance(NeoForgeExtension::class.java)

	/** The unified, cross-platform dependency declaration container (`dependencies { }`). */
	val dependencies = objects.newInstance(EasyModdingDependencies::class.java)

	init {
		configPath.convention(layout.projectDirectory.file("easymodding.mod.json"))
	}

	/** Enables the Fabric loader and configures it via the supplied [action]. */
	fun fabric(action: Action<FabricExtension>) {
		fabric.enabled.set(true)
		action.execute(fabric)
	}

	/** Enables the Fabric loader with default configuration. */
	fun fabric() = fabric.enabled.set(true)

	/** Enables the NeoForge loader and configures it via the supplied [action]. */
	fun neoForge(action: Action<NeoForgeExtension>) {
		neoForge.enabled.set(true)
		action.execute(neoForge)
	}

	/** Enables the NeoForge loader with default configuration. */
	fun neoForge() = neoForge.enabled.set(true)

	/** Declares cross-platform dependencies via the [EasyModdingDependencies] API. */
	fun dependencies(action: Action<EasyModdingDependencies>) {
		action.execute(dependencies)
	}
}