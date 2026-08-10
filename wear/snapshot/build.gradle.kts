plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

android {
    namespace = "com.example.wear.snippet.snapshot"
    compileSdk = 37

    defaultConfig {
        minSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.wear.tooling.preview)

    // Snapshot dependencies required for OneHandedGestures; expected to go away in the first 1.7.0 beta release.
    implementation("androidx.wear.compose:compose-material3:1.7.0-SNAPSHOT")
    implementation("androidx.compose.foundation:foundation:1.7.0-SNAPSHOT")
    implementation(libs.wear.compose.material)
    implementation(libs.compose.ui.tooling)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.horologist.compose.layout)
    implementation(libs.horologist.compose.material)
}
