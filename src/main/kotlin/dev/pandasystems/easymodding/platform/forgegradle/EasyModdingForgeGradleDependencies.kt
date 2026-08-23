package dev.pandasystems.easymodding.platform.forgegradle

import dev.pandasystems.easymodding.platform.base.EasyModdingDependencies
import org.gradle.api.Project
import javax.inject.Inject

abstract class EasyModdingForgeGradleDependencies @Inject constructor(
	private val project: Project
): EasyModdingDependencies {
	override fun modImplementation(notation: Any) {
		project.dependencies.add("implementation", notation)
	}


	override fun modApi(notation: Any) {
		project.dependencies.add("api", notation)
	}


	override fun modCompileOnly(notation: Any) {
		project.dependencies.add("compileOnly", notation)
	}


	override fun modLocalRuntime(notation: Any) {
		project.dependencies.add("runtimeOnly", notation)
	}


	override fun library(notation: Any) {
		project.dependencies.add("implementation", notation)
	}


	override fun includeLibrary(notation: Any) {
		// First add as implementation
		library(notation)

		// Then add to jar-in-jar configuration
		project.dependencies.add("jarJar", notation)
	}


	override fun includeMod(notation: Any) {
		// First add as mod dependency
		modImplementation(notation)

		// Then add to jar-in-jar configuration
		project.dependencies.add("jarJar", notation)
	}


	override fun libraryCompileOnly(notation: Any) {
		project.dependencies.add("compileOnly", notation)
	}


	override fun libraryRuntimeOnly(notation: Any) {
		project.dependencies.add("runtimeOnly", notation)
	}
}
