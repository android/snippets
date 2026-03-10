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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
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
    }
}
rootProject.name = "snippets"
include(
    ":bluetoothle",
    ":compose:recomposehighlighter",
    ":kotlin",
    ":compose:snippets",
    ":datastore",
    ":watchface",
    ":wear",
    ":wearcompanion",
    ":views",
    ":misc",
    ":identity:credentialmanager",
    ":xr",
    ":watchfacepush:validator",
    ":kmp:androidApp",
    ":kmp:shared",
    ":playbilling",
    ":tv"
)
