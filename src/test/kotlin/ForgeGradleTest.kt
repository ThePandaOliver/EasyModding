import dev.pandasystems.easymodding.extensions.EasyModdingExtension
import dev.pandasystems.easymodding.tasks.GenerateForgeResourcesTask
import org.gradle.api.internal.project.DefaultProject
import org.gradle.kotlin.dsl.getByName
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test

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
		task.run()
	}
}
