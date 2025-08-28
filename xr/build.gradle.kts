plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.xr"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.xr"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
    buildFeatures {
        compose = true
    }
    lint {
        disable += "RestrictedApi"
    }
}

dependencies {
    implementation(libs.androidx.xr.arcore)
    implementation(libs.androidx.xr.scenecore)
    implementation(libs.androidx.xr.compose)

    implementation(libs.androidx.activity.ktx)

    implementation(libs.androidx.media3.exoplayer)

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
}