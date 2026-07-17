import dev.pandasystems.easymodding.extensions.EasyModdingExtension
import dev.pandasystems.easymodding.tasks.GenerateFabricModJsonTask
import dev.pandasystems.easymodding.tasks.GenerateForgeModsTomlTask
import dev.pandasystems.easymodding.tasks.GenerateNeoForgeModsTomlTask
import dev.pandasystems.easymodding.tasks.GeneratePackMcmetaTask
import org.gradle.api.internal.project.DefaultProject
import org.gradle.kotlin.dsl.getByName
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Regression test for a shared/common module enabling more than one loader at once (e.g. a
 * "core" module that pre-generates metadata for both NeoForge and Forge). Previously each
 * loader's resource task wrote its own `META-INF/pack.mcmeta`, which made `processResources` see
 * the same destination path contributed twice and fail with a duplicate-entry error. `pack.mcmeta`
 * should now be generated exactly once by [GeneratePackMcmetaTask], at the root of its output
 * directory, regardless of how many loaders are enabled.
 *
 * Also verifies the lifecycle task graph: `generateNeoForgeResources`/`generateForgeResources`
 * depend on the shared `generatePackResources` task, but `generateFabricResources` does not.
 */
class PackMcmetaTest {
	@Test
	fun testTask(@TempDir projectDir: File) {
		val project = ProjectBuilder
			.builder()
			.withProjectDir(projectDir)
			.build()

		// Intentionally does not set `easy_modding.platform`, mirroring a shared/common module
		// that only cares about metadata generation, not the loader toolchain wiring.
		project.repositories.mavenCentral()
		project.plugins.apply("dev.pandasystems.easymodding")

		project.extensions.getByType(EasyModdingExtension::class.java).apply {
			val resourceUrl = this::class.java.classLoader.getResource("easymodding.mod.json")
				?: throw IllegalArgumentException("Resource not found")
			configPath.set(File(resourceUrl.toURI()))

			// Enabling multiple loaders at once is exactly the scenario that used to trigger the
			// duplicate `pack.mcmeta` entry error.
			fabric()
			neoForge()
			forge()
		}

		(project as DefaultProject).evaluate()

		val packTask = project.tasks.getByName<GeneratePackMcmetaTask>("generatePackResources")
		packTask.outputDir.get().asFile.mkdirs()
		packTask.run()

		val generatedPack = packTask.outputDir.get().asFile.resolve("pack.mcmeta")
		assertTrue(generatedPack.exists(), "pack.mcmeta should be generated at the resources root")

		// None of the loader-specific leaf tasks should generate their own pack.mcmeta anymore,
		// so there is exactly one `pack.mcmeta` destination path once `processResources` merges
		// every task's output together.
		val fabricTask = project.tasks.getByName<GenerateFabricModJsonTask>("generateFabricModJson")
		fabricTask.outputDir.get().asFile.mkdirs()
		fabricTask.run()
		assertFalse(fabricTask.outputDir.get().asFile.resolve("pack.mcmeta").exists())

		val neoForgeTask = project.tasks.getByName<GenerateNeoForgeModsTomlTask>("generateNeoForgeModsToml")
		neoForgeTask.outputDir.get().asFile.mkdirs()
		neoForgeTask.run()
		assertFalse(neoForgeTask.outputDir.get().asFile.resolve("META-INF/pack.mcmeta").exists())

		val forgeTask = project.tasks.getByName<GenerateForgeModsTomlTask>("generateForgeModsToml")
		forgeTask.outputDir.get().asFile.mkdirs()
		forgeTask.run()
		assertFalse(forgeTask.outputDir.get().asFile.resolve("META-INF/pack.mcmeta").exists())

		// Verify the lifecycle task graph itself: NeoForge/Forge depend on the shared pack task,
		// Fabric does not.
		val generateFabricResources = project.tasks.getByName("generateFabricResources")
		val generateNeoForgeResources = project.tasks.getByName("generateNeoForgeResources")
		val generateForgeResources = project.tasks.getByName("generateForgeResources")

		fun dependsOn(task: org.gradle.api.Task, dependency: org.gradle.api.Task): Boolean =
			task.taskDependencies.getDependencies(task).contains(dependency)

		assertTrue(dependsOn(generateFabricResources, fabricTask), "generateFabricResources should depend on generateFabricModJson")
		assertFalse(dependsOn(generateFabricResources, packTask), "generateFabricResources should NOT depend on generatePackResources")

		assertTrue(dependsOn(generateNeoForgeResources, neoForgeTask), "generateNeoForgeResources should depend on generateNeoForgeModsToml")
		assertTrue(dependsOn(generateNeoForgeResources, packTask), "generateNeoForgeResources should depend on generatePackResources")

		assertTrue(dependsOn(generateForgeResources, forgeTask), "generateForgeResources should depend on generateForgeModsToml")
		assertTrue(dependsOn(generateForgeResources, packTask), "generateForgeResources should depend on generatePackResources")
	}
}
