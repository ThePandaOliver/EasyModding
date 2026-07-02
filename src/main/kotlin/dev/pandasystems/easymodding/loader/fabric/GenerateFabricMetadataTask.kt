package dev.pandasystems.easymodding.loader.fabric

import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

abstract class GenerateFabricMetadataTask : GenerateMetadataTask() {
	override fun writeMetadata(outputDir: File) {
		val extension = extension.get()
		val fabricSpec = extension.fabric
		val jsonFile = File(outputDir, "fabric.mod.json")

		val entrypoints = fabricSpec.entrypoints.asMap
			.mapNotNull { (name, spec) -> spec.entrypoints.orNull?.takeIf { it.isNotEmpty() }?.let { name to it } }
			.toMap()
			.takeIf { it.isNotEmpty() }

		val mixins = fabricSpec.mixins.asMap.values
			.map { mixin ->
				val environment = mixin.environment.get()
				FabricMixinEntry(
					config = mixin.config.get(),
					environment = if (environment == FabricEnvironment.BOTH) null else environment.toJsonValue()
				)
			}
			.takeIf { it.isNotEmpty() }

		val modJson = FabricModJson(
			id = fabricSpec.id.get(),
			version = fabricSpec.version.get(),

			name = fabricSpec.name.orNull,
			description = fabricSpec.description.orNull,
			contact = fabricSpec.contact.orNull?.takeIf { it.isNotEmpty() },
			authors = fabricSpec.authors.takeIf { it.isNotEmpty() }?.map { it.toFabricPerson() },
			contributors = fabricSpec.contributors.takeIf { it.isNotEmpty() }?.map { it.toFabricPerson() },
			license = fabricSpec.license.orNull,
			icon = fabricSpec.icon.orNull,

			environment = fabricSpec.environment.orNull?.toJsonValue(),
			entrypoints = entrypoints,
			jars = fabricSpec.jars.orNull?.takeIf { it.isNotEmpty() }?.map { FabricJarEntry(it) },
			languageAdapters = fabricSpec.languageAdapters.orNull?.takeIf { it.isNotEmpty() },
			mixins = mixins,
			accessWidener = fabricSpec.accessWideners.orNull,
			provides = fabricSpec.provides.orNull?.takeIf { it.isNotEmpty() },

			depends = fabricSpec.depends.orNull?.takeIf { it.isNotEmpty() },
			recommends = fabricSpec.recommends.orNull?.takeIf { it.isNotEmpty() },
			suggests = fabricSpec.suggests.orNull?.takeIf { it.isNotEmpty() },
			breaks = fabricSpec.breaks.orNull?.takeIf { it.isNotEmpty() },
			conflicts = fabricSpec.conflicts.orNull?.takeIf { it.isNotEmpty() },
		)

		val jsonFormat = Json {
			prettyPrint = true
			explicitNulls = false
		}
		jsonFile.writeText(jsonFormat.encodeToString(modJson))
	}

	private fun FabricEnvironment.toJsonValue(): String = when (this) {
		FabricEnvironment.CLIENT -> "client"
		FabricEnvironment.SERVER -> "server"
		FabricEnvironment.BOTH -> "*"
	}

	private fun FabricPersonSpec.toFabricPerson(): FabricPerson =
		FabricPerson(name.get(), contact.orNull?.takeIf { it.isNotEmpty() })
}
