package dev.pandasystems.easymodding.extensions

import dev.pandasystems.easymodding.PluginPlatform
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class EasyModdingExtension @Inject constructor(
	objects: ObjectFactory,
	layout: ProjectLayout,
	providers: ProviderFactory
) {
	val minecraftVersion = objects.property<String>()
	val configPath = objects.fileProperty().convention(layout.projectDirectory.file("easymodding.mod.json"))

	val platform = objects.property<PluginPlatform>().convention(
		providers.gradleProperty("easy_modding.platform").map { value ->
			when (value) {
				"loom-noremap", "loom" -> PluginPlatform.FABRIC_LOOM
				"loom-remap" -> PluginPlatform.FABRIC_LOOM_REMAP
				"moddev" -> PluginPlatform.MODDEV
				"forgegradle" -> PluginPlatform.FORGE_GRADLE
				else -> throw IllegalArgumentException(
					"Invalid platform: $value (Available: loom, loom-noremap, loom-remap, moddev, forgegradle)"
				)
			}
		}
	)

	val fabric = objects.newInstance(FabricExtension::class.java)

	fun fabric(action: Action<FabricExtension>) {
		fabric.enabled.set(true)
		action.execute(fabric)
	}

	fun fabric() = fabric.enabled.set(true)

	val neoForge = objects.newInstance(NeoForgeExtension::class.java)

	fun neoForge(action: Action<NeoForgeExtension>) {
		neoForge.enabled.set(true)
		action.execute(neoForge)
	}

	fun neoForge() = neoForge.enabled.set(true)

	val forge = objects.newInstance(ForgeExtension::class.java)

	fun forge(action: Action<ForgeExtension>) {
		forge.enabled.set(true)
		action.execute(forge)
	}

	fun forge() = forge.enabled.set(true)

	val modDependencies = objects.newInstance(EasyModdingDependencies::class.java)
	fun modDependencies(action: Action<EasyModdingDependencies>) = action.execute(modDependencies)

	val runs = objects.domainObjectContainer(EasyModdingRunConfig::class.java)
	fun runs(action: Action<NamedDomainObjectContainer<EasyModdingRunConfig>>) = action.execute(runs)
}