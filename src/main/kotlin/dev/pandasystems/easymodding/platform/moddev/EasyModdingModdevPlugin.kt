package dev.pandasystems.easymodding.platform.moddev

import dev.pandasystems.easymodding.platform.base.EasyModdingExtension
import dev.pandasystems.easymodding.platform.base.EasyModdingPlugin
import dev.pandasystems.easymodding.platform.base.easyModding
import dev.pandasystems.easymodding.util.setOrElseCurrent
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType

class EasyModdingModdevPlugin : EasyModdingPlugin() {
	override val pluginExtensionClass: Class<out EasyModdingExtension> = EasyModdingModdevExtension::class.java

	override fun apply(target: Project) {
		target.pluginManager.apply("net.neoforged.moddev")

		super.apply(target)

		val extension = target.easyModding
		val neoForgeExtension = target.extensions.getByType(NeoForgeExtension::class.java)
		val javaExtension = target.extensions.getByType<JavaPluginExtension>()

		target.afterEvaluate {
			extension.neoForge.neoForgeVersion.orNull?.let { neoForgeExtension.version = it }

			// Create run configs
			extension.runs.forEach {
				neoForgeExtension.runs.create(it.name) {
					when (it.runtimeEnvironment.get()) {
						"client" -> {
							client()
						}

						"server" -> {
							server()
						}
					}
					ideName.convention(it.name)
					gameDirectory.setOrElseCurrent(target.objects, it.workingDirectory)
					systemProperties.convention(it.systemProperties)
					jvmArguments.convention(it.jvmArguments)
					programArguments.convention(it.programArguments)
					it.sourceSet.orNull?.let { sourceSetName ->
						sourceSet.set(javaExtension.sourceSets.named(sourceSetName))
					}
				}
			}

			// set mod source
			neoForgeExtension.mods {
				create("main") {
					sourceSet(javaExtension.sourceSets["main"])
				}
			}
		}
	}
}
