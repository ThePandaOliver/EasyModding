import dev.pandasystems.easymodding.extensions.EasyModdingExtension
import dev.pandasystems.easymodding.tasks.GenerateNeoForgeResourcesTask
import org.gradle.api.internal.project.DefaultProject
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.getByName
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test

class NeoForgeTest {
	@Test
	fun testTask(@TempDir projectDir: File) {
		val project = ProjectBuilder
			.builder()
			.withProjectDir(projectDir)
			.build()

		project.extra["easy_modding.platform"] = "neoforge"
		project.repositories.mavenCentral()
		project.plugins.apply("dev.pandasystems.easymodding")

		project.extensions.getByType(EasyModdingExtension::class.java).apply {
			minecraftVersion.set("1.21")
			val resourceUrl = this::class.java.classLoader.getResource("easymodding.mod.json")
				?: throw IllegalArgumentException("Resource not found")
			configPath.set(File(resourceUrl.toURI()))

			neoForge {
				neoForgeVersion.set("21.0.167")
			}
		}

		(project as DefaultProject).evaluate()
		val task = project.tasks.getByName<GenerateNeoForgeResourcesTask>("GenerateNeoForgeResources")
		task.run()
	}
}