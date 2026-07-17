import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * End-to-end functional test (via Gradle TestKit, running a real build through the real task
 * execution graph) for the "generate everything at once" scenario: a project — e.g. a
 * shared/common module — that enables Fabric, NeoForge, and Forge all at the same time and asks
 * Gradle to generate every loader's metadata in a single build.
 *
 * Unlike the other tests in this suite, which call a task's action directly via `task.run()` and
 * never go through Gradle's real `Copy`/duplicate-handling machinery, this test drives an actual
 * build via [GradleRunner]. It is a regression test for a real bug where enabling more than one
 * loader made `processResources` see `pack.mcmeta` contributed by more than one task at the same
 * destination path, failing the build with `Entry META-INF/pack.mcmeta is a duplicate...`.
 */
class GenerateAllTest {
	@Test
	fun generatingEveryLoaderAtOnceDoesNotConflict(@TempDir projectDir: File) {
		File(projectDir, "settings.gradle.kts").writeText(
			"""
			rootProject.name = "generate-all-test"
			""".trimIndent()
		)

		// Intentionally does not set `easy_modding.platform`, mirroring a shared/common module
		// that only cares about metadata generation, not the loader toolchain wiring (which would
		// otherwise require ForgeGradle/ModDev/Loom to reach out over the network).
		File(projectDir, "build.gradle.kts").writeText(
			"""
			plugins {
			    id("dev.pandasystems.easymodding")
			}

			easyModding {
			    configPath.set(file("easymodding.mod.json"))
			    fabric()
			    neoForge()
			    forge()
			}
			""".trimIndent()
		)

		File(projectDir, "easymodding.mod.json").writeText(
			"""
			{
			  "schemaVersion": 1,
			  "metadata": {
			    "id": "generateall",
			    "version": "1.0.0",
			    "name": "Generate All Test"
			  },
			  "dependencies": [
			    { "modId": "required-mod", "versionRange": ">=1.0.0" },
			    { "modId": "optional-mod", "type": "optional" }
			  ]
			}
			""".trimIndent()
		)

		val result = GradleRunner.create()
			.withProjectDir(projectDir)
			.withPluginClasspath()
			.withArguments(
				"generateFabricResources",
				"generateNeoForgeResources",
				"generateForgeResources",
				"processResources",
				"--stacktrace",
			)
			.build()

		assertEquals(TaskOutcome.SUCCESS, result.task(":generateFabricResources")?.outcome)
		assertEquals(TaskOutcome.SUCCESS, result.task(":generateNeoForgeResources")?.outcome)
		assertEquals(TaskOutcome.SUCCESS, result.task(":generateForgeResources")?.outcome)
		assertEquals(TaskOutcome.SUCCESS, result.task(":processResources")?.outcome)

		// Every loader's metadata should have made it into the processed resources...
		val resourcesDir = File(projectDir, "build/resources/main")
		assertTrue(File(resourcesDir, "fabric.mod.json").exists(), "fabric.mod.json should be generated")
		assertTrue(File(resourcesDir, "META-INF/neoforge.mods.toml").exists(), "neoforge.mods.toml should be generated")
		assertTrue(File(resourcesDir, "META-INF/mods.toml").exists(), "mods.toml should be generated")
		assertTrue(File(resourcesDir, "pack.mcmeta").exists(), "pack.mcmeta should be generated once, at the resources root")

		// ...and pack.mcmeta must not also show up under META-INF (that duplicate is what used to
		// make `processResources` fail).
		assertFalse(File(resourcesDir, "META-INF/pack.mcmeta").exists(), "pack.mcmeta must not be duplicated under META-INF")
	}
}
