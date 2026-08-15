package dev.pandasystems.easymodding.extensions

import org.gradle.api.Action
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class EasyModdingExtension @Inject constructor(
	objects: ObjectFactory,
	layout: ProjectLayout,
) {

	abstract val minecraftVersion: Property<String>


	abstract val configPath: RegularFileProperty


	val fabric = objects.newInstance(FabricExtension::class.java)


	val neoForge = objects.newInstance(NeoForgeExtension::class.java)


	val forge = objects.newInstance(ForgeExtension::class.java)


	val dependencies = objects.newInstance(EasyModdingDependencies::class.java)

	init {
		configPath.convention(layout.projectDirectory.file("easymodding.mod.json"))
	}


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


	fun modDependencies(action: Action<EasyModdingDependencies>) {
		action.execute(dependencies)
	}
}
