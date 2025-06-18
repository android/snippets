val snapshotVersion : String? = "13663278"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
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
