plugins {
    `kotlin-dsl`
}

group = "dev.pandasystems"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.toml.core)
    implementation(libs.kotlinx.serialization.toml.file)
    testImplementation(kotlin("test"))

    fun implementPlugin(id: String, version: String) = "$id:$id.gradle.plugin:$version"

    implementPlugin("det.fabricmc.fabric-loom", "1.17-SNAPSHOT")
    implementPlugin("net.neoforged.moddev", "2.0.+")
}

kotlin {
    jvmToolchain(25)
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
            implementationClass = "dev.pandasystems.easymodding.fabric.EasyModdingFabricPlugin"
        }
        register("easy-modding-neoforge") {
            id = "dev.pandasystems.easymodding.neoforge"
            implementationClass = "dev.pandasystems.easymodding.neoforge.EasyModdingNeoForgePlugin"
        }
    }
}