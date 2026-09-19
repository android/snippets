val snapshotVersion: String? by settings

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        snapshotVersion?.let {
            println("https://androidx.dev/snapshots/builds/$it/artifacts/repository/")
            maven { url = uri("https://androidx.dev/snapshots/builds/$it/artifacts/repository/") }
        }
        maven {
            url = uri("https://jitpack.io")
            content {
                includeGroup("com.github.xgouchet")
            }
        }
        google()
        mavenCentral()
        ivy {
            name = "Node.js"
            url = uri("https://nodejs.org/dist")
            patternLayout {
                artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]")
            }
            metadataSources {
                artifact()
            }
            content {
                includeGroup("org.nodejs")
            }
        }
        ivy {
            name = "Yarn"
            url = uri("https://github.com/yarnpkg/yarn/releases/download")
            patternLayout {
                artifact("v[revision]/[artifact](-v[revision]).[ext]")
            }
            metadataSources {
                artifact()
            }
            content {
                includeGroup("com.yarnpkg")
            }
        }
        ivy {
            name = "Binaryen"
            url = uri("https://github.com/WebAssembly/binaryen/releases/download")
            patternLayout {
                artifact("version_[revision]/[artifact]-version_[revision]-[classifier].[ext]")
            }
            metadataSources {
                artifact()
            }
            content {
                includeGroup("com.github.webassembly")
            }
        }
    }
    versionCatalogs {
        create("xrLibs") {
            from(files("xr/libs.versions.toml"))
        }
    }
}
rootProject.name = "snippets"
include(
    ":ai",
    ":bluetoothle",
    ":compose:recomposehighlighter",
    ":kotlin",
    ":compose:snippets",
    ":datastore",
    ":camerax",
    ":watchface",
    ":wear",
    ":wearcompanion",
    ":views",
    ":media",
    ":misc",
    ":security",
    ":identity:credentialmanager",
    ":xr",
    ":watchfacepush:validator",
    ":kmp:androidApp",
    ":kmp:shared",
    ":playbilling",
    ":tv",
    ":contacts",
    ":healthconnect",
    ":cars",
    ":installprompt",
    ":telecom",
    ":room",
    ":performance",
    ":preview:wasm",
    ":preview:generator"
)