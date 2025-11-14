plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.compose.compiler)

}

android {
    compileSdk = 36

    namespace = "com.example.sft"

    defaultConfig {
        applicationId = "com.example.sft.presentation.MainActivity"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    val composeBom = platform(libs.androidx.compose.bom)

    // General compose dependencies
    implementation(composeBom)
    implementation(libs.androidx.activity.compose)

    // Compose for Wear OS Dependencies
    implementation(libs.wear.compose.material3)

    // Foundation is additive, so you can use the mobile version in your Wear OS app.
    implementation(libs.compose.foundation)
    implementation(libs.androidx.material.icons.core)

    //implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("play-services-health-tracking-eap-0.0.1-eap.aar"))))

    implementation("com.google.android.gms:play-services-health-tracking-eap:0.0.4-eap")
    // Preview Tooling
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.androidx.compose.ui.tooling)

    implementation(libs.horologist.compose.layout)
    implementation(libs.horologist.compose.material)
    implementation(libs.androidx.lifecycle.service)

    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.tooling)

    // implementation("com.google.android.gms:play-services-health-tracking:0.0.2")
}
