import dev.pandasystems.easymodding.EasyModdingExtension
import dev.pandasystems.easymodding.loadEasyModdingConfig
import dev.pandasystems.easymodding.tasks.GenerateFabricMetadataTask
import dev.pandasystems.easymodding.loader.fabric.populateFabricModJson
import dev.pandasystems.easymodding.loader.fabric.toJsonString
import kotlinx.serialization.json.*
import org.gradle.api.internal.project.DefaultProject
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.getByName
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class FabricTest {
	@Test
	fun testTask(@TempDir projectDir: File) {
		val project = ProjectBuilder
			.builder()
			.withProjectDir(projectDir)
			.build()

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
		val task = project.tasks.getByName<GenerateFabricMetadataTask>("generateFabricMetadata")
		task.run()
		println(task.outputFile.get().asFile.readText(Charsets.UTF_8))
	}
}