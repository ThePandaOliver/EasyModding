import dev.pandasystems.easymodding.loadEasyModdingConfig
import dev.pandasystems.easymodding.populateFabricModJson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.get

class FabricTest {
//	@Test
//	fun testFile(@TempDir projectDir: File) {
//		val project = ProjectBuilder
//			.builder()
//			.withProjectDir(projectDir)
//			.build()
//
//		project.extra["easy_modding.platform"] = "loom"
//		project.repositories.mavenCentral()
//		project.plugins.apply("dev.pandasystems.easymodding")
//
//		val easyModdingExtension = project.extensions.getByType(EasyModdingExtension::class.java)
//		easyModdingExtension.minecraftVersion.set("26.2")
//		easyModdingExtension.fabric.id.set("test")
//		easyModdingExtension.fabric.version.set("1")
//
//		(project as DefaultProject).evaluate()
//		val task = project.tasks.getByName<GenerateMetadataTask>("generateMetadata")
//		task.run()
//		println(task.outputDirectory.get().asFile.resolve("fabric.mod.json").readText(Charsets.UTF_8))
//	}

	@Test
	fun testSerialization() {
		val resourceUrl = this::class.java.classLoader.getResource("example.mod.json")
			?: throw IllegalArgumentException("Resource not found")

		val file = File(resourceUrl.toURI())

		val config = loadEasyModdingConfig(file)
		val fabric = config.populateFabricModJson()

		val jsonFormat = Json {
			ignoreUnknownKeys = true
			prettyPrint = true
			encodeDefaults = true
			prettyPrintIndent = "	"
			explicitNulls = false
		}

		val json = jsonFormat.encodeToJsonElement(fabric).jsonObject
		println(jsonFormat.encodeToString(json))

		assertEquals(1, json["schemaVersion"]?.jsonPrimitive?.int)
		assertEquals("easymodding", json["id"]?.jsonPrimitive?.content)
		assertEquals("1.0.0", json["version"]?.jsonPrimitive?.content)
		assertEquals("Easy Modding", json["name"]?.jsonPrimitive?.content)
		assertEquals("A simple mod for testing purposes", json["description"]?.jsonPrimitive?.content)
		assertEquals("MIT", json["license"]?.jsonPrimitive?.content)
		assertEquals("icon.png", json["icon"]?.jsonPrimitive?.content)

		// Author
		assertEquals("Author name", json["authors"]?.jsonArray?.get(0)?.jsonPrimitive?.content)
		assertEquals("Author with contacts", json["authors"]?.jsonArray?.get(1)?.jsonObject?.get("name")?.jsonPrimitive?.content)
		assertContact(json["authors"]?.jsonArray?.get(1)?.jsonObject?.get("contact")?.jsonObject!!)
		// Contributor
		assertEquals("Contributor name", json["contributors"]?.jsonArray?.get(0)?.jsonPrimitive?.content)
		assertEquals("Contributor with contacts", json["contributors"]?.jsonArray?.get(1)?.jsonObject?.get("name")?.jsonPrimitive?.content)
		assertContact(json["contributors"]?.jsonArray?.get(1)?.jsonObject?.get("contact")?.jsonObject!!)

		assertContact(json["contact"]?.jsonObject!!)

		assertEquals("example.mixins.json", json["mixins"]?.jsonArray?.get(0)?.jsonPrimitive?.content)

		assertEquals("com.example.mod.FabricClient", json["entrypoints"]?.jsonObject?.get("client")?.jsonArray?.get(0)?.jsonPrimitive?.content)
		assertEquals("com.example.mod.FabricCommon", json["entrypoints"]?.jsonObject?.get("main")?.jsonArray?.get(0)?.jsonPrimitive?.content)

		assertEquals("example.accesswidener", json["accessWidener"]?.jsonPrimitive?.content)
	}

	fun assertContact(json: JsonObject) {
		assertEquals("https://example.com", json["homepage"]?.jsonPrimitive?.content)
		assertEquals("https://example.com/issues", json["issues"]?.jsonPrimitive?.content)
		assertEquals("custom contact information", json["custom"]?.jsonPrimitive?.content)
	}
}