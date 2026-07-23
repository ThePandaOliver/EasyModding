plugins {
	`kotlin-dsl`
	alias(libs.plugins.kotlin.serialization)
	`maven-publish`
}

group = "dev.pandasystems"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
	gradlePluginPortal()
	maven("https://maven.fabricmc.net/")
	maven("https://maven.minecraftforge.net/")
}

dependencies {
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.kotlinx.serialization.toml.core)
	implementation(libs.kotlinx.serialization.toml.file)
	testImplementation(kotlin("test"))
	testImplementation(gradleTestKit())

	fun implementPlugin(id: String, version: String) = implementation("$id:$id.gradle.plugin:$version")

	implementPlugin("net.fabricmc.fabric-loom", "1.17-SNAPSHOT")
	implementPlugin("net.fabricmc.fabric-loom-remap", "1.17-SNAPSHOT")
	implementPlugin("net.neoforged.moddev", "2.0.141")
	implementPlugin("net.minecraftforge.gradle", "[7.0.17,8)")
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
		register("easy-modding-loom") {
			id = "dev.pandasystems.easymodding.loom"
			implementationClass = "dev.pandasystems.easymodding.platform.loom.EasyModdingLoomPlugin"
		}
		register("easy-modding-loom-noremap") {
			id = "dev.pandasystems.easymodding.loom-noremap"
			implementationClass = "dev.pandasystems.easymodding.platform.loom.EasyModdingLoomNoremapPlugin"
		}
		register("easy-modding-loom-remap") {
			id = "dev.pandasystems.easymodding.loom-remap"
			implementationClass = "dev.pandasystems.easymodding.platform.loom.EasyModdingLoomRemapPlugin"
		}
		register("easy-modding-moddev") {
			id = "dev.pandasystems.easymodding.moddev"
			implementationClass = "dev.pandasystems.easymodding.platform.moddev.EasyModdingModdevPlugin"
		}
		register("easy-modding-forgegradle") {
			id = "dev.pandasystems.easymodding.forgegradle"
			implementationClass = "dev.pandasystems.easymodding.platform.forgegradle.EasyModdingForgeGradlePlugin"
		}
	}
}

publishing {
	repositories {
		maven {
			name = "LocalRepo"
			url = uri("E:\\MavenRepo")
		}
	}
}