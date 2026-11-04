plugins {
    alias(libs.plugins.android.application)
}

kotlin {
    jvmToolchain(17)
}

// Platform connectivity snippets, with no Compose dependencies.
android {
    namespace = "com.example.snippets.connectivity"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.snippets.connectivity"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
}
