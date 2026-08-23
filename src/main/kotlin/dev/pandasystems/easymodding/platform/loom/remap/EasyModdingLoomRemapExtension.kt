package dev.pandasystems.easymodding.platform.loom.remap

import dev.pandasystems.easymodding.PluginPlatform
import dev.pandasystems.easymodding.platform.loom.FabricExtension
import dev.pandasystems.easymodding.platform.forgegradle.ForgeExtension
import dev.pandasystems.easymodding.platform.moddev.NeoForgeExtension
import dev.pandasystems.easymodding.platform.base.EasyModdingDependencies
import dev.pandasystems.easymodding.platform.base.EasyModdingExtension
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class EasyModdingLoomRemapExtension @Inject constructor(
	val objects: ObjectFactory
) : EasyModdingExtension {
	abstract override val minecraftVersion: Property<String>

	abstract override val configPath: RegularFileProperty

	override val platform: PluginPlatform = PluginPlatform.FABRIC_LOOM_REMAP

	override val fabric: FabricExtension = objects.newInstance(FabricExtension::class.java)
	override val neoForge: NeoForgeExtension = objects.newInstance(NeoForgeExtension::class.java)
	override val forge: ForgeExtension = objects.newInstance(ForgeExtension::class.java)

	override val modDependencies: EasyModdingDependencies = objects.newInstance(EasyModdingLoomRemapDependencies::class.java)
}