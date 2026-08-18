package dev.pandasystems.easymodding.platform.base

import dev.pandasystems.easymodding.PluginPlatform
import dev.pandasystems.easymodding.extensions.EasyModdingRunConfig
import dev.pandasystems.easymodding.extensions.FabricExtension
import dev.pandasystems.easymodding.extensions.ForgeExtension
import dev.pandasystems.easymodding.extensions.NeoForgeExtension
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

interface EasyModdingExtension {
	val minecraftVersion: Property<String>
	val configPath: RegularFileProperty

	val platform: PluginPlatform

	val fabric: FabricExtension

	fun fabric() = fabric.enabled.set(true)

	fun fabric(action: Action<FabricExtension>) {
		fabric.enabled.set(true)
		action.execute(fabric)
	}

	val neoForge: NeoForgeExtension

	fun neoForge() = neoForge.enabled.set(true)

	fun neoForge(action: Action<NeoForgeExtension>) {
		neoForge.enabled.set(true)
		action.execute(neoForge)
	}

	val forge: ForgeExtension

	fun forge() = forge.enabled.set(true)

	fun forge(action: Action<ForgeExtension>) {
		forge.enabled.set(true)
		action.execute(forge)
	}

	val modDependencies: EasyModdingDependencies
	fun modDependencies(action: Action<EasyModdingDependencies>) = action.execute(modDependencies)

	val runs: NamedDomainObjectContainer<EasyModdingRunConfig>
	fun runs(action: Action<NamedDomainObjectContainer<EasyModdingRunConfig>>) = action.execute(runs)
}