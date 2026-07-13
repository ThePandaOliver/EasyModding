package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.loader.fabric.FabricExtension
import dev.pandasystems.easymodding.loader.neoforge.NeoForgeExtension
import org.gradle.api.Action
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
}