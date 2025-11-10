plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.kotlin_samples"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.kotlin_samples"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // Core Android KTX for quality-of-life Kotlin extensions
    implementation("androidx.core:core-ktx:1.13.1")

    // Activity library with Compose integration for `setContent` and `enableEdgeToEdge`
    implementation("androidx.activity:activity-compose:1.9.0")

    // Jetpack Compose Bill of Materials (BOM)
    // The BOM ensures that all of your Compose libraries use the same, compatible version.
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose UI for core components like Modifier and layout primitives
    implementation("androidx.compose.ui:ui")

    // Compose Material 3 for Material Design components like Scaffold and Text
    implementation("androidx.compose.material3:material3")

    // Compose Foundation for building blocks like scrolling
    implementation("androidx.compose.foundation:foundation")

    // Tooling for displaying @Preview composables in Android Studio
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Other standard dependencies for testing and lifecycle management
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}