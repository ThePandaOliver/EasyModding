import dev.pandasystems.easymodding.EasyModdingExtension
import dev.pandasystems.easymodding.loader.GenerateMetadataTask
import org.gradle.api.internal.project.DefaultProject
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.getByName
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.jvm.java
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

		val easyModdingExtension = project.extensions.getByType(EasyModdingExtension::class.java)
		easyModdingExtension.minecraftVersion.set("26.2")
		easyModdingExtension.fabric.id.set("test")
		easyModdingExtension.fabric.version.set("1")

		(project as DefaultProject).evaluate()
		val task = project.tasks.getByName<GenerateMetadataTask>("generateMetadata")
		task.run()
		println(task.outputDirectory.get().asFile.resolve("fabric.mod.json").readText(Charsets.UTF_8))
	}
}