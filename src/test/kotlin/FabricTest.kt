import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import org.gradle.api.internal.project.DefaultProject
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.getByName
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test

class FabricTest {
	@Test
	fun testFile(@TempDir projectDir: File) {
		val project = ProjectBuilder
			.builder()
			.withProjectDir(projectDir)
			.build()

		project.extra["easy_modding.platform"] = "loom"
		project.repositories.mavenCentral()
		project.plugins.apply("dev.pandasystems.easymodding")

		(project as DefaultProject).evaluate()
		project.tasks.getByName<GenerateMetadataTask>("generateMetadata").run()
	}
}