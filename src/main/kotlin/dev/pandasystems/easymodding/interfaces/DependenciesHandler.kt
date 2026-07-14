package dev.pandasystems.easymodding.interfaces

interface DependenciesHandler {
	fun addModImplementation()
	fun addModApi()
	fun addModCompileOnly()
	fun addModLocalRuntime()
}