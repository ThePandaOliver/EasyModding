package dev.pandasystems.easymodding.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class FabricEntryPoint(val value: String = "main")