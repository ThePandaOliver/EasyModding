import dev.pandasystems.easymodding.EasyModdingExtension
import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import org.gradle.api.internal.project.DefaultProject
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.getByName
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test

class NoPlatformTest {
	@Test
	fun testTask(@TempDir projectDir: File) {
		val project = ProjectBuilder
			.builder()
			.withProjectDir(projectDir)
			.build()

		project.repositories.mavenCentral()
		project.plugins.apply("dev.pandasystems.easymodding")

		project.extensions.getByType(EasyModdingExtension::class.java).apply {
			val resourceUrl = this::class.java.classLoader.getResource("easymodding.mod.json")
				?: throw IllegalArgumentException("Resource not found")
			configPath.set(File(resourceUrl.toURI()))

			neoForge {
				enabled.set(false)
				enabledMetadataGeneration.set(true)
			}

			fabric {
				enabled.set(false)
				enabledMetadataGeneration.set(true)
			}
		}

		(project as DefaultProject).evaluate()
		project.tasks.getByName<GenerateMetadataTask>("generateNeoForgeMetadata").run()
		project.tasks.getByName<GenerateMetadataTask>("generateFabricMetadata").run()
	}

//	@Test
//	fun testSerialization() {
//		val resourceUrl = this::class.java.classLoader.getResource("easymodding.mod.json")
//			?: throw IllegalArgumentException("Resource not found")
//
//		val file = File(resourceUrl.toURI())
//
//		val config = loadEasyModdingConfig(file)
//		val fabric = config.populateFabricModJson()
//
//		val jsonFormat = Json {
//			ignoreUnknownKeys = true
//			prettyPrint = true
//			encodeDefaults = true
//			explicitNulls = false
//		}
//
//		val jsonString = fabric.toJsonString()
//		println(jsonString)
//		val json = jsonFormat.decodeFromString<JsonObject>(jsonString)
//
//		assertEquals(1, json["schemaVersion"]?.jsonPrimitive?.int)
//		assertEquals("easymodding", json["id"]?.jsonPrimitive?.content)
//		assertEquals("1.0.0", json["version"]?.jsonPrimitive?.content)
//		assertEquals("Easy Modding", json["name"]?.jsonPrimitive?.content)
//		assertEquals("A simple mod for testing purposes", json["description"]?.jsonPrimitive?.content)
//		assertEquals("MIT", json["license"]?.jsonPrimitive?.content)
//		assertEquals("icon.png", json["icon"]?.jsonPrimitive?.content)
//
//		// Author
//		assertEquals("Author name", json["authors"]?.jsonArray?.get(0)?.jsonPrimitive?.content)
//		assertEquals("Author with contacts", json["authors"]?.jsonArray?.get(1)?.jsonObject?.get("name")?.jsonPrimitive?.content)
//		assertContact(json["authors"]?.jsonArray?.get(1)?.jsonObject?.get("contact")?.jsonObject!!)
//		// Contributor
//		assertEquals("Contributor name", json["contributors"]?.jsonArray?.get(0)?.jsonPrimitive?.content)
//		assertEquals("Contributor with contacts", json["contributors"]?.jsonArray?.get(1)?.jsonObject?.get("name")?.jsonPrimitive?.content)
//		assertContact(json["contributors"]?.jsonArray?.get(1)?.jsonObject?.get("contact")?.jsonObject!!)
//
//		assertContact(json["contact"]?.jsonObject!!)
//
//		assertEquals("example.mixins.json", json["mixins"]?.jsonArray?.get(0)?.jsonPrimitive?.content)
//
//		assertEquals("com.example.mod.FabricClient", json["entrypoints"]?.jsonObject?.get("client")?.jsonArray?.get(0)?.jsonPrimitive?.content)
//		assertEquals("com.example.mod.FabricCommon", json["entrypoints"]?.jsonObject?.get("main")?.jsonArray?.get(0)?.jsonPrimitive?.content)
//
//		assertEquals("example.accesswidener", json["accessWidener"]?.jsonPrimitive?.content)
//	}
//
//	fun assertContact(json: JsonObject) {
//		assertEquals("https://example.com", json["homepage"]?.jsonPrimitive?.content)
//		assertEquals("https://example.com/issues", json["issues"]?.jsonPrimitive?.content)
//		assertEquals("custom contact information", json["custom"]?.jsonPrimitive?.content)
//	}
}