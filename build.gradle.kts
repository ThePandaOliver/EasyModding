plugins {
	`kotlin-dsl`
	alias(libs.plugins.kotlin.serialization)
}

group = "dev.pandasystems"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
	gradlePluginPortal()
	maven("https://maven.fabricmc.net/")
}

dependencies {
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.kotlinx.serialization.toml.core)
	implementation(libs.kotlinx.serialization.toml.file)
	testImplementation(kotlin("test"))
	testImplementation(gradleTestKit())

	fun implementPlugin(id: String, version: String) = implementation("$id:$id.gradle.plugin:$version")

	implementPlugin("net.fabricmc.fabric-loom", "1.17-SNAPSHOT")
	implementPlugin("net.neoforged.moddev", "2.0.+")
}

kotlin {
	jvmToolchain(21)
}

tasks.test {
	useJUnitPlatform()
}

gradlePlugin {
	plugins {
		register("easy-modding") {
			id = "dev.pandasystems.easymodding"
			implementationClass = "dev.pandasystems.easymodding.EasyModdingPlugin"
		}
		register("easy-modding-fabric") {
			id = "dev.pandasystems.easymodding.fabric"
			implementationClass = "dev.pandasystems.easymodding.loader.fabric.EasyModdingFabricPlugin"
		}
	}
}