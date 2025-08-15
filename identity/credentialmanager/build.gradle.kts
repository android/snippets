plugins {
    alias(libs.plugins.android.application)
    // [START android_identity_fido2_migration_dependency]
    alias(libs.plugins.kotlin.android)
    // [END android_identity_fido2_migration_dependency]
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.identity.credentialmanager"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.identity.credentialmanager"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
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
    sourceSets {
        named("main") {
            java {
                srcDir("src/main/java")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // [START android_identity_credman_dependency]
    implementation(libs.androidx.credentials)
    // [END android_identity_credman_dependency]

    // [START android_identity_gradle_dependencies]
    implementation(libs.androidx.credentials)

    // optional - needed for credentials support from play services, for devices running
    // Android 13 and below.
    implementation(libs.androidx.credentials.play.services.auth)
    // [END android_identity_gradle_dependencies]
    // [START android_identity_siwg_gradle_dependencies]
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.android.identity.googleid)
    // [END android_identity_siwg_gradle_dependencies]
    implementation(libs.okhttp)
    implementation(libs.kotlin.coroutines.okhttp)
    implementation(libs.androidx.webkit)
    implementation(libs.appcompat)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
