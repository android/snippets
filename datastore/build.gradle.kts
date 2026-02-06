import org.jetbrains.kotlin.gradle.dsl.JvmTarget
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)

    // [START android_datastore_proto_plugin]
    alias(libs.plugins.google.protobuf)
    // [END android_datastore_proto_plugin]

    // [START android_datastore_serialization_plugin]
    alias(libs.plugins.kotlin.serialization)
    // [END android_datastore_serialization_plugin]
}

android {
    namespace = "com.example.datastore.snippets"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.datastore.snippets"
        minSdk = 23
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
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget("11")
        }
    }
    buildFeatures {
        compose = true
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

    // [START android_datastore_dependency]
    // Typed DataStore (Typed API surface, such as Proto)
    implementation(libs.androidx.datastore)

    // Alternatively - without an Android dependency.
    implementation(libs.androidx.datastore.core)
    // [END android_datastore_dependency]

    // [START android_datastore_preferences_dependency]
    // Preferences DataStore (SharedPreferences like APIs)
    implementation(libs.androidx.datastore.preferences)

    // Alternatively - without an Android dependency.
    implementation(libs.androidx.datastore.preferences.core)
    // [END android_datastore_preferences_dependency]

    // [START android_datastore_preferences_dependency_rxjava]
    // optional - RxJava2 support
    implementation(libs.androidx.datastore.preferences.rxjava2)

    // optional - RxJava3 support
    implementation(libs.androidx.datastore.preferences.rxjava3)
    // [END android_datastore_preferences_dependency_rxjava]

    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)

    // [START android_datastore_proto_dependency]
    implementation(libs.google.protobuf.kotlin.lite)
    // [END android_datastore_proto_dependency]

    // [START android_datastore_json_dependency]
    implementation(libs.kotlinx.serialization.json)
    // [END android_datastore_json_dependency]

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// [START android_datastore_proto_task]
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.32.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin")
            }
        }
    }
}
// [END android_datastore_proto_task]