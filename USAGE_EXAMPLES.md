# EasyModding Dependency Management Usage Examples

This document demonstrates how to use the unified dependency management system in EasyModding for both Fabric and NeoForge.

## Basic Setup

First, apply the EasyModding plugin and set your platform in `gradle.properties`:

```properties
# gradle.properties
easy_modding.platform=fabric
# or
easy_modding.platform=neoforge
```

Then in your `build.gradle.kts`:

```kotlin
plugins {
    id("dev.pandasystems.easymodding") version "1.0.0"
}

easyModding {
    minecraftVersion.set("1.21.1")
    
    // Enable Fabric
    fabric()
    
    // Or enable NeoForge
    // neoForge {
    //     neoForgeVersion.set("21.1.0")
    // }
    
    dependencies {
        // Your dependencies here - they work the same for both platforms!
    }
}
```

## Unified Dependency Methods

### 1. Mod Dependencies

#### `modImplementation(notation)`
Adds a mod dependency available at both compile time and runtime.

```kotlin
easyModding {
    dependencies {
        // Works the same for Fabric and NeoForge!
        modImplementation("com.example:cool-mod:1.0.0")
        
        // With Fabric: uses modImplementation configuration
        // With NeoForge: uses implementation configuration
    }
}
```

#### `modApi(notation)`
Adds a mod dependency that exposes its API to other mods (transitive).

```kotlin
easyModding {
    dependencies {
        modApi("com.example:api-mod:1.0.0")
        
        // With Fabric: uses modApi configuration
        // With NeoForge: uses api configuration (auto-applies java-library plugin)
    }
}
```

#### `modCompileOnly(notation)`
Adds a mod dependency only for compilation (not included at runtime).

```kotlin
easyModding {
    dependencies {
        modCompileOnly("com.example:compile-only-mod:1.0.0")
        
        // Useful for optional dependencies or provided mods
    }
}
```

#### `modLocalRuntime(notation)`
Adds a mod dependency only for the development runtime environment.

```kotlin
easyModding {
    dependencies {
        modLocalRuntime("com.example:dev-helper-mod:1.0.0")
        
        // Great for development utilities that shouldn't be required in production
    }
}
```

### 2. Library Dependencies (Non-Mod Libraries)

#### `library(notation)`
Adds a regular Java/Kotlin library dependency.

```kotlin
easyModding {
    dependencies {
        library("com.google.guava:guava:31.1-jre")
        library("org.slf4j:slf4j-api:2.0.9")
    }
}
```

#### `libraryCompileOnly(notation)` and `libraryRuntimeOnly(notation)`
Add library dependencies for specific scopes.

```kotlin
easyModding {
    dependencies {
        libraryCompileOnly("org.jetbrains:annotations:24.0.0")
        libraryRuntimeOnly("org.postgresql:postgresql:42.6.0")
    }
}
```

### 3. Jar-in-Jar (Bundling Dependencies)

#### `includeLibrary(notation)`
Bundles a non-mod library with your mod (jar-in-jar).

```kotlin
easyModding {
    dependencies {
        // This library will be bundled inside your mod jar
        includeLibrary("com.example:my-library:1.0.0")
        
        // With Fabric: uses include configuration
        // With NeoForge: uses jarJar configuration
    }
}
```

#### `includeMod(notation)`
Bundles a mod dependency with your mod (jar-in-jar).

```kotlin
easyModding {
    dependencies {
        // This mod will be bundled inside your mod jar
        includeMod("com.example:required-mod:1.0.0")
        
        // Useful for hard dependencies that should be shipped together
    }
}
```

## Complete Example

Here's a complete example showing various dependency types:

```kotlin
// build.gradle.kts
plugins {
    id("dev.pandasystems.easymodding") version "1.0.0"
}

easyModding {
    minecraftVersion.set("1.21.1")
    
    fabric()
    
    dependencies {
        // Required mod dependencies
        modImplementation("net.fabricmc.fabric-api:fabric-api:0.92.0+1.21.1")
        modImplementation("com.example:core-mod:2.0.0")
        
        // API mod that other mods can use
        modApi("com.example:public-api:1.5.0")
        
        // Optional mod (compile-only)
        modCompileOnly("com.example:optional-integration:1.0.0")
        
        // Development-only mod
        modLocalRuntime("dev.example:debug-tools:1.0.0")
        
        // Regular library dependencies
        library("com.google.code.gson:gson:2.10.1")
        library("org.apache.commons:commons-lang3:3.14.0")
        
        // Bundled library (included in your mod jar)
        includeLibrary("com.example:utility-lib:1.0.0")
        
        // Bundled mod (included in your mod jar)
        includeMod("com.example:bundled-mod:1.0.0")
        
        // Compile-only library
        libraryCompileOnly("org.jetbrains:annotations:24.0.0")
    }
}
```

## Platform-Specific Behavior

### Fabric (with Fabric Loom)
- `modImplementation` → `modImplementation` configuration
- `modApi` → `modApi` configuration
- `modCompileOnly` → `modCompileOnly` configuration
- `modLocalRuntime` → `modLocalRuntime` configuration
- `includeLibrary` / `includeMod` → `include` configuration

### NeoForge (with ModDev)
- `modImplementation` → `implementation` configuration
- `modApi` → `api` configuration (auto-applies `java-library` plugin)
- `modCompileOnly` → `compileOnly` configuration
- `modLocalRuntime` → `runtimeOnly` configuration
- `includeLibrary` / `includeMod` → `jarJar` configuration

## Benefits

1. **Write Once, Run Everywhere**: Write your dependencies once, and they work for both Fabric and NeoForge
2. **Clear Intent**: Method names clearly express what type of dependency you're adding
3. **Type Safety**: All methods have proper types and documentation
4. **Error Handling**: Clear error messages if platform is not detected
5. **Jar-in-Jar Support**: Easy bundling of dependencies with unified API

## Migration Guide

### From Standard Gradle Dependencies

**Before:**
```kotlin
dependencies {
    modImplementation("com.example:mod:1.0.0")
    include("com.example:lib:1.0.0")
}
```

**After:**
```kotlin
easyModding {
    dependencies {
        modImplementation("com.example:mod:1.0.0")
        includeLibrary("com.example:lib:1.0.0")
    }
}
```

### Multi-Platform Projects

If you're building for both Fabric and NeoForge, you can use the same dependency declarations:

```kotlin
// Common dependencies that work for both platforms
easyModding {
    dependencies {
        modImplementation("com.example:cross-platform-mod:1.0.0")
        includeLibrary("com.example:shared-lib:1.0.0")
    }
}
```

Just change the `easy_modding.platform` property to switch platforms!
