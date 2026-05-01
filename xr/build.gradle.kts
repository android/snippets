plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.xr"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.xr"
        minSdk = 34
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        lint {
            warningsAsErrors = true
            disable += "UseKtx"
        }
    }
    kotlin {
        jvmToolchain(17)

        compilerOptions {
            allWarningsAsErrors = true
            // TODO: b/508214289
            freeCompilerArgs.add("-Xwarning-level=TYPEALIAS_EXPANSION_DEPRECATION:disabled")
        }
    }
    buildFeatures {
        compose = true
    }
    lint {
        disable += "RestrictedApi"
    }
}

val snapshotVersion: String? by project
val isUsingSnapshot = snapshotVersion != null


val implementationXrLibraries = listOf<Provider<MinimalExternalModuleDependency>>(
    libs.androidx.xr.arcore.asProvider(),
    libs.androidx.xr.arcore.play.services,
    libs.androidx.xr.glimmer,
    libs.androidx.xr.projected.asProvider(),
    libs.androidx.xr.scenecore,
    libs.androidx.xr.compose,
)
val testImplementationXrLibraries = listOf<Provider<MinimalExternalModuleDependency>>(
    libs.androidx.xr.projected.testing,
)

dependencies {
    if (isUsingSnapshot) {
        implementationXrLibraries.forEach {
            implementation(it.toSnapshotDependency())
        }
        testImplementationXrLibraries.forEach {
            testImplementation(it.toSnapshotDependency())
        }

        // Snapshot versions will reference a non-public impress version.
        constraints {
            implementation("com.google.ar:impress") {
                version {
                    strictly("0.0.13")
                }
            }
        }
    } else {
        implementationXrLibraries.forEach {
            implementation(it)
        }
        testImplementationXrLibraries.forEach {
            testImplementation(it)
        }
    }

    implementation(libs.google.ar.core)

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.graphics.shapes)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.viewbinding)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.compose.animation.graphics)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material)

    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material.ripple)
    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.truth)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.mockito.kotlin)
}

fun Provider<MinimalExternalModuleDependency>.toSnapshotDependency(): Provider<MinimalExternalModuleDependency> =
    this.map {
        it.copy().apply {
            version {
                strictly("1.0.0-SNAPSHOT")
            }
        }
    }