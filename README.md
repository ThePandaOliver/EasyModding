# EasyModding

A Gradle plugin that makes **Minecraft multiloader modding** easier by unifying the build scripts
into a single plugin. It sets up your project based on the selected loader (Fabric, NeoForge, or
Forge) and generates the required loader metadata files (`fabric.mod.json`, `neoforge.mods.toml`,
`mods.toml`, `pack.mcmeta`) from **one shared config file**.

Instead of maintaining separate build scripts and metadata files per loader, you declare your mod
once and switch platforms with a single Gradle property.

> **Note:** This project was developed with assistance from Agentic AI as an experiment to accelerate
> development. All code has been reviewed by the developer.

## Features

- **Unified build setup** — one plugin applies and configures the correct loader toolchain (Fabric
  Loom with auto-detection, Fabric Loom No-Remap, Fabric Loom Remap, NeoForged ModDev, or
  ForgeGradle) based on the selected platform.
- **Single source of truth for metadata** — describe your mod once in `easymodding.mod.json`, and
  EasyModding generates the loader-native files at build time.
- **Unified mod dependencies** — declare mod dependencies (`depends`/`recommends`/etc.) once in
  `easymodding.mod.json`, and they're translated into `fabric.mod.json`, `neoforge.mods.toml`, and
  `mods.toml`'s native dependency schemas automatically.
- **Unified dependency API** — declare Gradle dependencies once with methods like
  `modImplementation`, `library`, `includeMod`, etc., and they map to the correct loader-specific
  configurations.
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
  "dependencies": [
    { "modId": "fabric", "versionRange": ">=0.92.0" },
    { "modId": "some-api-mod", "type": "optional", "versionRange": ">=1.0.0" },
    { "modId": "known-incompatible-mod", "type": "incompatible", "reason": "Crashes on load together" }
  ],
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
(`fabric`, `neoforge`, `pack`) can override or extend those values. The shared `dependencies` list
works the same way for mod dependencies — see [Unified mod dependencies](#unified-mod-dependencies)
below.

### 3. Build

`fabric.mod.json` / `neoforge.mods.toml` / `mods.toml` / `pack.mcmeta` are generated automatically
as part of `processResources`. You can also run the per-loader lifecycle tasks directly, each of
which generates every metadata file that loader needs in one go:

```bash
./gradlew generateFabricResources     # when Fabric is enabled   -> fabric.mod.json
./gradlew generateNeoForgeResources   # when NeoForge is enabled -> neoforge.mods.toml, pack.mcmeta
./gradlew generateForgeResources      # when Forge is enabled    -> mods.toml, pack.mcmeta
```

Under the hood, each of those is a lifecycle task with no action of its own; it just depends on
the single-purpose leaf task(s) that actually write each metadata file, which you can also run
individually if you only want to (re)generate one file:

```bash
./gradlew generateFabricModJson       # writes fabric.mod.json
./gradlew generateNeoForgeModsToml    # writes META-INF/neoforge.mods.toml
./gradlew generateForgeModsToml       # writes META-INF/mods.toml
./gradlew generatePackResources       # writes pack.mcmeta (shared by NeoForge and Forge)
```

```
generateFabricResources     -> generateFabricModJson
generateNeoForgeResources   -> generateNeoForgeModsToml, generatePackResources
generateForgeResources      -> generateForgeModsToml,    generatePackResources
```

`pack.mcmeta` is loader-agnostic (its meaning is identical on NeoForge and Forge) and lives at the
root of the resources directory rather than under `META-INF`, so it's written once by
`generatePackResources` and depended on by both `generateNeoForgeResources` and
`generateForgeResources` — but **not** by `generateFabricResources`, since Fabric has no use for
it. Generating it once, shared, also matters if you enable more than one loader in the same module
(e.g. a shared/common module preparing metadata for multiple platforms): generating it separately
per loader would make `processResources` see the same destination path contributed more than once
and fail with a duplicate-entry error.

## Unified mod dependencies

The `dependencies` array in `easymodding.mod.json` (**not** the Gradle `dependencies { }` block
described below — see [Unified dependency API](#unified-dependency-api) for that) lets you declare
a mod's dependency relationships once and have them generated into every enabled loader's native
metadata format:

```json
{
  "modId": "fabric-api",
  "type": "required",
  "versionRange": ">=0.92.0",
  "reason": "Needed for networking APIs",
  "ordering": "after",
  "side": "both",
  "referralUrl": "https://modrinth.com/mod/fabric-api"
}
```

| Field          | Required | Description                                                             |
| -------------- | -------- | ------------------------------------------------------------------------ |
| `modId`        | Yes      | The mod (or loader API, e.g. `fabricloader`, `minecraft`) depended on.  |
| `type`         | No       | `required` (default), `optional`, `incompatible`, or `discouraged`.     |
| `versionRange` | No       | Accepted version range, in the target loader's own syntax.             |
| `reason`       | No       | Shown to the user when the dependency isn't satisfied.                 |
| `ordering`     | No       | `before`, `after`, or `none` (default; let the loader decide).         |
| `side`         | No       | `client`, `server`, or `both` (default).                               |
| `referralUrl`  | No       | A URL with more info about (or to obtain) the dependency.               |

Each entry is translated per-loader when the corresponding metadata is generated:

| `type`         | Fabric (`fabric.mod.json`) | NeoForge / Forge (`[[dependencies]]`)              |
| -------------- | --------------------------- | --------------------------------------------------- |
| `required`     | `depends`                   | `type = "required"` / `mandatory = true`             |
| `optional`     | `recommends`                | `type = "optional"` / `mandatory = false`            |
| `discouraged`  | `conflicts`                 | `type = "discouraged"` / `mandatory = false`         |
| `incompatible` | `breaks`                    | `type = "incompatible"` / `mandatory = false`        |

NeoForge's dependency schema matches the unified one almost exactly, so `ordering`, `side`, and
`referralUrl` carry over untouched. Legacy Forge only has a boolean `mandatory` flag, so anything
other than `required` maps to `mandatory = false`. Fabric has no version-range-aware equivalent of
`type` beyond the four buckets above; a missing `versionRange` is written as `*` (any version).

You can still declare extra, platform-only dependencies directly under the `fabric`, `neoforge`,
or `forge` sections — they're merged alongside (Fabric, keyed by mod ID with the platform-specific
entry winning on a clash) or appended after (NeoForge/Forge) the ones generated from the shared
list.

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
│   ├── EasyModdingDependency.kt  Unified mod dependency model, shared across all loaders
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
└── tasks/                        Metadata generation tasks (one leaf task per generated file)
    ├── GenerateFabricModJsonTask.kt      Writes fabric.mod.json
    ├── GenerateNeoForgeModsTomlTask.kt   Writes META-INF/neoforge.mods.toml
    ├── GenerateForgeModsTomlTask.kt      Writes META-INF/mods.toml
    └── GeneratePackMcmetaTask.kt         Writes pack.mcmeta, shared by NeoForge and Forge
```

`generateFabricResources`/`generateNeoForgeResources`/`generateForgeResources` are lifecycle tasks
registered directly in `EasyModdingPlugin.kt` (no dedicated task class) that just `dependsOn` the
leaf tasks above.

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
