package dev.pandasystems.easymodding.platform.loom.noremap

import dev.pandasystems.easymodding.PluginPlatform
import dev.pandasystems.easymodding.extensions.FabricExtension
import dev.pandasystems.easymodding.extensions.ForgeExtension
import dev.pandasystems.easymodding.extensions.NeoForgeExtension
import dev.pandasystems.easymodding.platform.base.EasyModdingDependencies
import dev.pandasystems.easymodding.platform.base.EasyModdingExtension
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class EasyModdingLoomNoremapExtension @Inject constructor(
	val objects: ObjectFactory
) : EasyModdingExtension {
	abstract override val minecraftVersion: Property<String>

	abstract override val configPath: RegularFileProperty

	override val platform: PluginPlatform = PluginPlatform.FABRIC_LOOM

	override val fabric: FabricExtension = objects.newInstance(FabricExtension::class.java)
	override val neoForge: NeoForgeExtension = objects.newInstance(NeoForgeExtension::class.java)
	override val forge: ForgeExtension = objects.newInstance(ForgeExtension::class.java)

	override val modDependencies: EasyModdingDependencies = objects.newInstance(EasyModdingLoomNoremapDependencies::class.java)
}