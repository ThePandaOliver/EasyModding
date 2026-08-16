package dev.pandasystems.easymodding.extensions

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class EasyModdingExtension @Inject constructor(
	objects: ObjectFactory,
	layout: ProjectLayout,
) {
	val minecraftVersion = objects.property<String>()
	val configPath = objects.fileProperty().convention(layout.projectDirectory.file("easymodding.mod.json"))

	val fabric = objects.newInstance(FabricExtension::class.java)
	val neoForge = objects.newInstance(NeoForgeExtension::class.java)

	val forge = objects.newInstance(ForgeExtension::class.java)

	val modDependencies = objects.newInstance(EasyModdingDependencies::class.java)
	val runs = objects.domainObjectContainer(EasyModdingRunConfig::class.java)

	fun fabric(action: Action<FabricExtension>) {
		fabric.enabled.set(true)
		action.execute(fabric)
	}

	fun fabric() = fabric.enabled.set(true)

	fun neoForge(action: Action<NeoForgeExtension>) {
		neoForge.enabled.set(true)
		action.execute(neoForge)
	}

	fun neoForge() = neoForge.enabled.set(true)

	fun forge(action: Action<ForgeExtension>) {
		forge.enabled.set(true)
		action.execute(forge)
	}

	fun forge() = forge.enabled.set(true)

	fun modDependencies(action: Action<EasyModdingDependencies>) = action.execute(modDependencies)

	fun runs(action: Action<NamedDomainObjectContainer<EasyModdingRunConfig>>) = action.execute(runs)
}