package dev.pandasystems.easymodding.platform.base

interface EasyModdingDependencies {
	fun modImplementation(notation: Any)

	fun modApi(notation: Any)

	fun modCompileOnly(notation: Any)

	fun modLocalRuntime(notation: Any)

	fun library(notation: Any)

	fun includeLibrary(notation: Any)

	fun includeMod(notation: Any)

	fun libraryCompileOnly(notation: Any)

	fun libraryRuntimeOnly(notation: Any)
}