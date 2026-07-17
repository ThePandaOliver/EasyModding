import dev.pandasystems.easymodding.extensions.EasyModdingExtension
import dev.pandasystems.easymodding.tasks.GenerateForgeResourcesTask
import org.gradle.api.internal.project.DefaultProject
import org.gradle.kotlin.dsl.getByName
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class ForgeGradleTest {
	@Test
	fun testTask(@TempDir projectDir: File) {
		val project = ProjectBuilder
			.builder()
			.withProjectDir(projectDir)
			.build()

		// Intentionally does not set `easy_modding.platform`, so the real ForgeGradle plugin is
		// never applied; this test only exercises resource generation, not the loader toolchain
		// wiring (applying ForgeGradle triggers the Minecraft Mavenizer, which reaches out over
		// the network and isn't appropriate for a fast, hermetic unit test).
		project.repositories.mavenCentral()
		project.plugins.apply("dev.pandasystems.easymodding")

		project.extensions.getByType(EasyModdingExtension::class.java).apply {
			minecraftVersion.set("1.20.1")
			val resourceUrl = this::class.java.classLoader.getResource("easymodding.mod.json")
				?: throw IllegalArgumentException("Resource not found")
			configPath.set(File(resourceUrl.toURI()))

			forge {
				forgeVersion.set("47.2.0")
			}
		}

		(project as DefaultProject).evaluate()
		val task = project.tasks.getByName<GenerateForgeResourcesTask>("generateForgeResources")

		// Gradle pre-creates a task's `@OutputDirectory` paths before executing it for real
		// (unlike calling `task.run()` directly), so replicate that here.
		task.outputDir.get().asFile.mkdirs()
		task.run()

		// The unified `dependencies` declared in easymodding.mod.json should have been translated
		// into `mods.toml`'s `[[dependencies]]` entries, with `required-mod` marked mandatory and
		// the rest not, since legacy Forge only has a boolean mandatory flag.
		val generated = task.outputDir.get().asFile.resolve("META-INF/mods.toml").readText()
		assertTrue(generated.contains("required-mod"), "expected required-mod dependency")
		assertTrue(generated.contains("optional-mod"), "expected optional-mod dependency")
		assertTrue(generated.contains("incompatible-mod"), "expected incompatible-mod dependency")
	}
}
