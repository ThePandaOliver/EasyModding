package dev.pandasystems.easymodding.loader.fabric

import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File

abstract class GenerateFabricMetadataTask : GenerateMetadataTask() {
	override fun writeMetadata(outputDir: File) {
		val extension = extension.get()
		val fabricSpec = extension.fabric
		val jsonFile = File(outputDir, "fabric.mod.json")

		fun serializeContact(contacts: Map<String, String>): JsonElement = buildJsonObject {
			contacts.forEach { (key, value) -> put(key, value) }
		}

		fun serializePersonList(people: List<FabricPersonSpec>): JsonElement = buildJsonArray {
			people.forEach { person ->
				add(person.name.get())
				person.contact.orNull?.let {
					add(serializeContact(it))
				}
			}
		}

		fun serializeEntryPoints(entrypoints: Map<String, FabricEntrypointSpec>): JsonElement = buildJsonObject {
			entrypoints.forEach { (key, value) ->
				value.entrypoints.orNull?.let { points ->
					put(key, buildJsonArray {
						points.forEach { point -> add(point) }
					})
				}
			}
		}

		val json = buildJsonObject {
			put("schemaVersion", 1)
			put("id", fabricSpec.id.get())
			put("version", fabricSpec.version.get())

			fabricSpec.name.orNull?.let { put("name", it) }
			fabricSpec.description.orNull?.let { put("description", it) }
			fabricSpec.contact.orNull?.let {
				put("contact", serializeContact(it))
			}
			if (fabricSpec.authors.isNotEmpty())
				put("authors", serializePersonList(fabricSpec.authors.toList()))
			if (fabricSpec.contributors.isNotEmpty())
				put("contributors", serializePersonList(fabricSpec.contributors.toList()))

			fabricSpec.license.orNull?.let { put("license", it) }
			fabricSpec.icon.orNull?.let { put("icon", it) }

			fabricSpec.environment.orNull?.let {
				put(
					"environment",
					when (it) {
						FabricEnvironment.CLIENT -> "client";
						FabricEnvironment.SERVER -> "server";
						else -> "*"
					}
				)
			}
			if (fabricSpec.entrypoints.isNotEmpty())
				put("entrypoints", serializeEntryPoints(fabricSpec.entrypoints.asMap.toMap()))
			fabricSpec.jars.orNull?.let { jars ->
				put("jars", buildJsonArray {
					jars.forEach { jarPath -> add(buildJsonObject { put("file", jarPath) }) }
				})
			}
			fabricSpec.languageAdapters.orNull?.let { languageAdapters ->
				put("languageAdapters", buildJsonObject {
					languageAdapters.forEach { (language, adapter) -> put(language, adapter) }
				})
			}
			if (fabricSpec.mixins.isNotEmpty())
				put("mixins", buildJsonArray {
					fabricSpec.mixins.asMap.forEach { (config, mixin) -> add(config) }
				})
			fabricSpec.accessWideners.orNull?.let { put("accessWidener", it) }
			fabricSpec.provides.orNull?.let { provides ->
				put("provides", buildJsonArray {
					provides.forEach { provide -> add(provide) }
				})
			}

			fabricSpec.depends.orNull?.let { depends ->
				put("depends", buildJsonObject {
					depends.forEach { (modId, version) -> put(modId, version) }
				})
			}
			fabricSpec.recommends.orNull?.let { recommends ->
				put("recommends", buildJsonObject {
					recommends.forEach { (modId, version) -> put(modId, version) }
				})
			}
			fabricSpec.suggests.orNull?.let { suggests ->
				put("suggests", buildJsonObject {
					suggests.forEach { (modId, version) -> put(modId, version) }
				})
			}
			fabricSpec.breaks.orNull?.let { breaks ->
				put("breaks", buildJsonObject {
					breaks.forEach { (modId, version) -> put(modId, version) }
				})
			}
			fabricSpec.conflicts.orNull?.let { conflicts ->
				put("conflicts", buildJsonObject {
					conflicts.forEach { (modId, version) -> put(modId, version) }
				})
			}
		}

		val jsonFormat = Json { prettyPrint = true }
		jsonFile.writeText(jsonFormat.encodeToString(json))
	}
}