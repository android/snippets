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
    buildFeatures {
        compose = true
    }
    lint {

    }
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        allWarningsAsErrors = true
    }
}

val snapshotVersion: String? by project
val isUsingSnapshot = snapshotVersion != null

dependencies {
    implementation(xrLibs.androidx.xr.arcore.asProvider() orSnapshot "1.1.0")
    implementation(xrLibs.androidx.xr.arcore.play.services orSnapshot "1.1.0")
    implementation(xrLibs.androidx.xr.glimmer.asProvider() orSnapshot "1.0.0")
    implementation(xrLibs.androidx.xr.glimmer.googlefonts orSnapshot "1.0.0")
    // Don't update the Projected dependency until the emulator supports the latest APIs: b/544045508
    // implementation(xrLibs.androidx.xr.projected.testing orSnapshot "1.0.0")
    implementation(xrLibs.androidx.xr.projected.asProvider())
    implementation(xrLibs.androidx.xr.scenecore.asProvider() orSnapshot "1.1.0")
    implementation(xrLibs.androidx.xr.compose orSnapshot "1.0.0")

    // Don't update the Projected dependency until the emulator supports the latest APIs: b/544045508
    // testImplementation(xrLibs.androidx.xr.projected.testing orSnapshot "1.0.0")
    testImplementation(xrLibs.androidx.xr.projected.testing)
    testImplementation(xrLibs.androidx.xr.arcore.testing orSnapshot "1.1.0")
    testImplementation(xrLibs.androidx.xr.scenecore.testing orSnapshot "1.1.0")

    if (isUsingSnapshot) {
        // Snapshot versions will reference a non-public impress version.
        constraints {
            implementation("com.google.ar:impress") {
                version {
                    strictly("0.0.13")
                }
            }

            testImplementation("com.google.ar:impress") {
                version {
                    strictly("0.0.13")
                }
            }
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
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.test)
}

infix fun Provider<MinimalExternalModuleDependency>.orSnapshot(version: String): Provider<MinimalExternalModuleDependency> =
    this.map { dependency ->
        if (isUsingSnapshot) {
            dependency.copy().apply {
                version {
                    strictly("$version-SNAPSHOT")
                }
            }
        } else {
            dependency
        }
    }