import dev.pandasystems.easymodding.extensions.EasyModdingExtension
import dev.pandasystems.easymodding.tasks.GenerateFabricModJsonTask
import org.gradle.api.internal.project.DefaultProject
import org.gradle.kotlin.dsl.getByName
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class FabricTest {
	@Test
	fun testTask(@TempDir projectDir: File) {
		val project = ProjectBuilder
			.builder()
			.withProjectDir(projectDir)
			.build()

		// Intentionally does not set `easy_modding.platform`, so the real Loom plugin is never
		// applied; this test only exercises resource generation, not the loader toolchain wiring.
		project.repositories.mavenCentral()
		project.plugins.apply("dev.pandasystems.easymodding")

		project.extensions.getByType(EasyModdingExtension::class.java).apply {
			minecraftVersion.set("1.21")
			val resourceUrl = this::class.java.classLoader.getResource("easymodding.mod.json")
				?: throw IllegalArgumentException("Resource not found")
			configPath.set(File(resourceUrl.toURI()))

			fabric()
		}

		(project as DefaultProject).evaluate()

		// `generateFabricResources` is a lifecycle task with no action of its own; the actual
		// file is written by the `generateFabricModJson` leaf task it depends on.
		val task = project.tasks.getByName<GenerateFabricModJsonTask>("generateFabricModJson")

		// Gradle pre-creates a task's `@OutputDirectory` paths before executing it for real
		// (unlike calling `task.run()` directly), so replicate that here to catch bugs where the
		// task writes onto the directory path itself instead of a file inside it.
		task.outputDir.get().asFile.mkdirs()
		task.run()

		// The unified `dependencies` declared in easymodding.mod.json should have been bucketed
		// into Fabric's depends/recommends/breaks maps based on their type.
		val generated = task.outputDir.get().asFile.resolve("fabric.mod.json").readText()
		assertTrue(generated.contains("\"required-mod\": \">=1.0.0\""), "expected required-mod in depends")
		assertTrue(generated.contains("\"optional-mod\": \">=2.0.0\""), "expected optional-mod in recommends")
		assertTrue(generated.contains("\"incompatible-mod\": \"*\""), "expected incompatible-mod in breaks")
	}
}
