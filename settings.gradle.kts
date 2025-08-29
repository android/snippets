val snapshotVersion : String? = System.getenv("COMPOSE_SNAPSHOT_ID")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
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
        ":wear",
        ":views",
        ":misc",
        ":identity:credentialmanager",
        ":xr",
        ":watchfacepush:validator"
)
