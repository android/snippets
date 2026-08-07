plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.aiglasses.camera"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.aiglasses.camera"
        minSdk = 36
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.xr.projected.experimental.ExperimentalProjectedApi"
        )
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ProGuard minification extension library required for Jetpack XR alpha05+
    compileOnly("com.android.extensions.xr:extensions-xr:1.1.0")

    // Android XR & AI Glasses libraries
    implementation("androidx.xr.runtime:runtime:1.0.0-alpha10")
    implementation("androidx.xr.glimmer:glimmer:1.0.0-alpha07")
    implementation("androidx.xr.projected:projected:1.0.0-alpha04")
    implementation("androidx.xr.arcore:arcore:1.0.0-alpha10")

    // Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Core utilities
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
}

