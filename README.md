# EasyModding

A Gradle plugin that makes **Minecraft multiloader modding** easier by unifying the build scripts
into a single plugin. It sets up your project based on the selected loader (Fabric, NeoForge, or
Forge) and generates the required loader metadata files (`fabric.mod.json`, `neoforge.mods.toml`,
`mods.toml`, `pack.mcmeta`) from **one shared config file**.

Instead of maintaining separate build scripts and metadata files per loader, you declare your mod
once and switch platforms with a single Gradle property.

## Features

- **Unified build setup** — one plugin applies and configures the correct loader toolchain (Fabric
  Loom with auto-detection, Fabric Loom No-Remap, Fabric Loom Remap, NeoForged ModDev, or
  ForgeGradle) based on the selected platform.
- **Single source of truth for metadata** — describe your mod once in `easymodding.mod.json`, and
  EasyModding generates the loader-native files at build time.
- **Unified dependency API** — declare dependencies once with methods like `modImplementation`,
  `library`, `includeMod`, etc., and they map to the correct loader-specific configurations.
- **Switch platforms with one property** — change `easy_modding.platform` to build for a different
  loader without touching your dependency or metadata declarations.

## Requirements

- JDK 21+
- Gradle 9.x (a wrapper for Gradle 9.6.0 is included)

## Installation

Apply the plugin in your `build.gradle.kts`:

```kotlin
plugins {
    id("dev.pandasystems.easymodding") version "1.0-SNAPSHOT"
}
```

Select the target loader in `gradle.properties`:

```properties
# Choose one:
easy_modding.platform=loom            # Auto-detects: loom-remap for MC <= 1.21.11, loom-noremap for MC > 1.21.11
# easy_modding.platform=loom-noremap  # Explicit: Fabric Loom no-remap variant (for MC > 1.21.11)
# easy_modding.platform=loom-remap    # Explicit: Fabric Loom remap variant with Mojang mappings (for MC <= 1.21.11)
# easy_modding.platform=moddev        # NeoForge ModDev
# easy_modding.platform=forgegradle   # Legacy Forge with ForgeGradle
```

**Recommended:** Use `loom` for Fabric projects — it automatically selects the appropriate Loom
variant based on your Minecraft version.

When no platform is set, EasyModding skips loader-specific wiring — useful for a shared/common
subproject in a split multiloader setup.

## Usage

### 1. Configure the build

```kotlin
easyModding {
    minecraftVersion.set("1.21.1")

    // Enable Fabric...
    fabric()

    // ...or NeoForge:
    // neoForge {
    //     neoForgeVersion.set("21.1.0")
    // }

    // ...or legacy Forge:
    // forge {
    //     forgeVersion.set("47.2.0")
    // }

    dependencies {
        // Cross-platform dependency declarations (see below)
        modImplementation("net.fabricmc.fabric-api:fabric-api:0.92.0+1.21.1")
    }
}
```

### 2. Describe your mod once

Create `easymodding.mod.json` in your project directory (the default path; configurable via
`configPath`). This single file is the source of truth for all loader metadata:

```json
{
  "schemaVersion": 1,
  "metadata": {
    "id": "examplemod",
    "version": "1.0.0",
    "name": "Example Mod",
    "description": "An example multiloader mod",
    "license": "MIT",
    "icon": "icon.png",
    "authors": [
      "Alice",
      { "name": "Bob", "contact": { "homepage": "https://example.com" } }
    ],
    "contact": { "homepage": "https://example.com" }
  },
  "mixins": ["examplemod.mixins.json"],
  "fabric": {
    "entrypoints": {
      "main": ["com.example.mod.CommonMain"],
      "client": ["com.example.mod.ClientMain"]
    },
    "accessWidener": "examplemod.accesswidener"
  }
}
```

The shared `metadata` block is used as the fallback for every loader. Loader-specific sections
(`fabric`, `neoforge`, `pack`) can override or extend those values.

### 3. Build

`fabric.mod.json` / `neoforge.mods.toml` / `mods.toml` / `pack.mcmeta` are generated automatically
as part of `processResources`. You can also run the generation tasks directly:

```bash
./gradlew generateFabricResources     # when Fabric is enabled
./gradlew generateNeoForgeResources   # when NeoForge is enabled
./gradlew generateForgeResources      # when Forge is enabled
```

## Unified dependency API

Inside `easyModding { dependencies { } }`, use the loader-agnostic methods below. EasyModding maps
each to the correct configuration for the active platform.

| Method               | Description                                        | Fabric           | NeoForge / Forge                    |
| -------------------- | -------------------------------------------------- | ---------------- | ----------------------------------- |
| `modImplementation`  | Mod dep, compile + runtime                         | `modImplementation` | `implementation`                 |
| `modApi`             | Mod dep, exposed as API to dependents              | `modApi`         | `api` (auto-applies `java-library`) |
| `modCompileOnly`     | Mod dep, compile only                              | `modCompileOnly` | `compileOnly`                       |
| `modLocalRuntime`    | Mod dep, dev runtime only                          | `modLocalRuntime`| `runtimeOnly`                       |
| `library`            | Plain library, compile + runtime                   | `implementation` | `implementation`                    |
| `libraryCompileOnly` | Plain library, compile only                        | `compileOnly`    | `compileOnly`                       |
| `libraryRuntimeOnly` | Plain library, runtime only                        | `runtimeOnly`    | `runtimeOnly`                       |
| `includeLibrary`     | Bundle a library inside the jar (jar-in-jar)       | `include`        | `jarJar`                            |
| `includeMod`         | Bundle a mod inside the jar (jar-in-jar)           | `include`        | `jarJar`                            |

### Examples

```kotlin
easyModding {
    dependencies {
        // A required mod available at compile time and runtime
        modImplementation("net.fabricmc.fabric-api:fabric-api:0.92.0+1.21.1")

        // A mod whose API your mod exposes to other mods
        modApi("com.example:some-api-mod:1.0.0")

        // A mod only needed at compile time (e.g. an optional integration)
        modCompileOnly("com.example:optional-mod:1.0.0")

        // A mod only loaded in the dev environment
        modLocalRuntime("com.example:debug-tools:1.0.0")

        // A regular Java library
        library("com.google.code.gson:gson:2.10.1")

        // A library bundled inside your mod jar
        includeLibrary("com.example:utility-lib:1.0.0")

        // A mod bundled inside your mod jar
        includeMod("com.example:required-sub-mod:1.0.0")
    }
}
```

## How it works

EasyModding is a thin orchestrator plugin backed by loader-specific sub-plugins:

```
dev.pandasystems.easymodding                   (main entry point)
├── dev.pandasystems.easymodding.loom             -> Auto-detecting Loom (platform = "loom")
│                                                    ├─> loom-remap (MC <= 1.21.11)
│                                                    └─> loom-noremap (MC > 1.21.11)
├── dev.pandasystems.easymodding.loom-noremap     -> Fabric Loom No-Remap (platform = "loom-noremap")
├── dev.pandasystems.easymodding.loom-remap       -> Fabric Loom Remap    (platform = "loom-remap")
├── dev.pandasystems.easymodding.moddev           -> NeoForged ModDev     (platform = "moddev")
└── dev.pandasystems.easymodding.forgegradle      -> ForgeGradle          (platform = "forgegradle")
```

1. The main plugin reads `easy_modding.platform` (`loom`, `loom-noremap`, `loom-remap`, `moddev`,
   or `forgegradle`) and applies the matching sub-plugin.
2. For `loom`, the sub-plugin detects the Minecraft version and automatically applies either
   `loom-remap` (for versions 1.21.11 and below) or `loom-noremap` (for versions above 1.21.11).
3. The sub-plugin applies the underlying loader plugin and wires up the Minecraft / NeoForge /
   Forge version from the `easyModding` extension.
4. Resource-generation tasks read `easymodding.mod.json` and write the loader-native metadata
   files, which are folded into `processResources`.

## Project structure

```
src/main/kotlin/dev/pandasystems/easymodding/
├── EasyModdingPlugin.kt          Main plugin: extension + platform selection + task wiring
├── data/                         Metadata models and generation logic
│   ├── EasyModdingConfig.kt      Unified config model (easymodding.mod.json)
│   ├── FabricModJson.kt          fabric.mod.json model + population/serialization
│   ├── NeoForgeModToml.kt        neoforge.mods.toml model + population/serialization
│   ├── ForgeModsToml.kt          mods.toml model + population/serialization (Forge's own schema)
│   └── PackMcmeta.kt             pack.mcmeta model + population/serialization
├── extensions/                   The easyModding { } DSL
│   ├── EasyModdingExtension.kt   Root DSL (minecraftVersion, configPath, loaders, deps)
│   ├── EasyModdingDependencies.kt Unified cross-platform dependency API
│   ├── LoaderExtension.kt        Common loader interface (enabled flag)
│   ├── FabricExtension.kt        Fabric loader config
│   ├── NeoForgeExtension.kt      NeoForge loader config (+ neoForgeVersion)
│   └── ForgeExtension.kt         Forge loader config (+ forgeVersion)
├── platform/                     Loader-specific sub-plugins
│   ├── BaseEasyModdingPlatformPlugin.kt
│   ├── loom/EasyModdingLoomPlugin.kt                Fabric (auto-detects Loom variant)
│   ├── loom/EasyModdingLoomNoremapPlugin.kt         Fabric (Loom no-remap, MC > 1.21.11)
│   ├── loom/EasyModdingLoomRemapPlugin.kt           Fabric (Loom remap, MC <= 1.21.11)
│   ├── moddev/EasyModdingModdevPlugin.kt            NeoForge (ModDev)
│   └── forgegradle/EasyModdingForgeGradlePlugin.kt  Forge (ForgeGradle)
└── tasks/                        Metadata generation tasks
    ├── GenerateFabricResourcesTask.kt
    ├── GenerateNeoForgeResourcesTask.kt
    └── GenerateForgeResourcesTask.kt
```

## Building the plugin

```bash
./gradlew build            # compile and test
./gradlew test             # run tests only
./gradlew publish          # publish to the configured Maven repo
```

## Status & roadmap

- **Fabric (`loom`)** — supported with automatic version detection. The plugin automatically selects
  the appropriate Loom variant based on your Minecraft version:
  - **Minecraft <= 1.21.11**: Uses `loom-remap` with Mojang mappings
  - **Minecraft > 1.21.11**: Uses `loom-noremap` (standard Fabric Loom)
  
  This is the **recommended** option for Fabric projects as it handles version changes automatically.

- **Fabric Loom No-Remap (`loom-noremap`)** — explicit support for the standard Fabric Loom variant
  without remapping. Use this if you want to explicitly target Minecraft versions above 1.21.11
  without auto-detection.

- **Fabric Loom Remap (`loom-remap`)** — explicit support for the remap variant. Uses
  `net.fabricmc.fabric-loom-remap` (same version as `fabric-loom`) and automatically wires in the
  official Mojang mappings via `loom.officialMojangMappings()`. Use this if you want to explicitly
  target Minecraft 1.21.11 or older versions without auto-detection.

- **NeoForge (`moddev`)** — supported.

- **Forge (`forgegradle`)** — supported, backed by ForgeGradle 7 (`[7.0.17,8)`), which requires
  Gradle 9+ and uses the newer `minecraft.dependency(...)` API instead of the legacy `minecraft`
  dependency configuration used by ForgeGradle 6.

## License

Group `dev.pandasystems`, version `1.0-SNAPSHOT`. No license file is present in the repository yet;
add one before distributing.
