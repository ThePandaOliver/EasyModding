package dev.pandasystems.easymodding.loader.neoforge

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.domainObjectContainer
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

// Reference: https://docs.neoforged.net/docs/gettingstarted/modfiles/

abstract class NeoForgeMetadataSpec @Inject constructor(
	private val objects: ObjectFactory,
) {
	val modloader = objects.property<String>()
	val loaderVersion = objects.property<String>()
	val license = objects.property<String>()
	val showAsResourcePack = objects.property<Boolean>()
	val showAsDataPack = objects.property<Boolean>()
	val services = objects.listProperty<String>()
	val properties = objects.mapProperty<String, String>()
	val issueTrackerURL = objects.property<String>()

	val mods = objects.domainObjectContainer(NeoForgeModSpec::class.java)
	val accessTransformers = objects.listProperty<String>()
	val mixins = objects.domainObjectContainer(NeoForgeMixinConfigSpec::class.java)

	fun service(service: String) = services.add(service)

	fun property(key: String, value: String) = properties.put(key, value)

	fun mod(modId: String, action: Action<NeoForgeModSpec> = Action {}) {
		val mod = mods.create(modId)
		mod.modId.set(modId)
		action.execute(mod)
	}

	fun accessTransformer(file: String) = accessTransformers.add(file)

	fun mixin(config: String, action: Action<NeoForgeMixinConfigSpec> = Action {}) {
		val mixin = mixins.create(config)
		mixin.config.set(config)
		action.execute(mixin)
	}
}

abstract class NeoForgeModSpec @Inject constructor(
	objects: ObjectFactory,
) {
	val modId = objects.property(String::class.java)
	val namespace = objects.property(String::class.java)
	val version = objects.property(String::class.java)
	val displayName = objects.property(String::class.java)
	val description = objects.property(String::class.java)
	val logoFile = objects.property(String::class.java)
	val logoBlur = objects.property(Boolean::class.java)
	val updateJSONURL = objects.property(String::class.java)
	val modUrl = objects.property(String::class.java)
	val credits = objects.property(String::class.java)
	val authors = objects.property(String::class.java)
	val displayURL = objects.property(String::class.java)
	val enumExtension = objects.property(String::class.java)
	val featureFlags = objects.property(String::class.java)

	val features = objects.mapProperty<String, String>()
	val properties = objects.mapProperty<String, String>()
	val dependencies = objects.domainObjectContainer(NeoForgeDependencySpec::class.java)

	fun feature(feature: String, value: String) = features.put(feature, value)
	fun property(key: String, value: String) = properties.put(key, value)
	fun dependency(modId: String, action: Action<NeoForgeDependencySpec> = Action {}) {
		val dependency = dependencies.create(modId)
		dependency.modId.set(modId)
		action.execute(dependency)
	}
}

abstract class NeoForgeAccessTransformerSpec @Inject constructor(
	objects: ObjectFactory,
) {
	val file = objects.property(String::class.java)
}

abstract class NeoForgeMixinConfigSpec @Inject constructor(
	objects: ObjectFactory,
) {
	val config = objects.property(String::class.java)
	val requiredMods = objects.listProperty(String::class.java)
	val behaviorVersion = objects.property(String::class.java)

	fun requiredMod(modId: String) = requiredMods.add(modId)
}

abstract class NeoForgeDependencySpec @Inject constructor(
	objects: ObjectFactory,
) {
	val modId = objects.property(String::class.java)
	val type = objects.property(NeoForgeDependencyType::class.java)
	val reason = objects.property(String::class.java)
	val versionRange = objects.property(String::class.java)
	val ordering = objects.property(NeoForgeDependencyOrdering::class.java)
	val side = objects.property(NeoForgeDependencySide::class.java)
	val referralUrl = objects.property(String::class.java)
}