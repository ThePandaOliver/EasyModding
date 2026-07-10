package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.loader.fabric.FabricExtension
import dev.pandasystems.easymodding.loader.neoforge.NeoForgeExtension
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import java.io.File
import javax.inject.Inject

abstract class EasyModdingExtension @Inject constructor(
	objects: ObjectFactory
) {
	val minecraftVersion = objects.property<String>()

	val configPath = objects.fileProperty().convention { File("easymodding.mod.json") }
	val config = configPath.map { loadEasyModdingConfig(it.asFile) }

	val fabric = objects.newInstance(FabricExtension::class.java)
	fun fabric(action: Action<FabricExtension>) = action.execute(fabric).also { fabric.enabled.set(true) }

	val neoForge = objects.newInstance(NeoForgeExtension::class.java)
	fun neoForge(action: Action<NeoForgeExtension>) = action.execute(neoForge).also { neoForge.enabled.set(true) }
}