package dev.pandasystems.easymodding

import dev.pandasystems.easymodding.loader.fabric.FabricExtension
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.provideDelegate
import java.io.File
import javax.inject.Inject
import kotlin.getValue

abstract class EasyModdingExtension @Inject constructor(
	objects: ObjectFactory
) {
	val minecraftVersion = objects.property<String>()

	val metadataPath = objects.fileProperty().convention { File("easymodding.mod.json") }
	val metadata = metadataPath.map { loadEasyModdingConfig(it.asFile) }

	val fabric = objects.newInstance(FabricExtension::class.java)
	fun fabric(action: Action<FabricExtension>) = action.execute(fabric).also { fabric.enabled.set(true) }
}