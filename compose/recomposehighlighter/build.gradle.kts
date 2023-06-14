plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}
android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.example.recompose"

    defaultConfig {
        applicationId = "com.example.recompose"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro")
        }
    }
}
dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.material)

    implementation(libs.androidx.lifecycle.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

/*
plugins {
    id 'com.android.application'
    id 'kotlin-android'
}

android {
    compileSdkVersion 33
    buildToolsVersion "30.0.2"

    defaultConfig {
        applicationId "com.example.android.compose.recomposehighlighter"
        minSdkVersion 21
        targetSdkVersion 33
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt')
        }
    }

    buildFeatures {
        compose true
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
    composeOptions {
        kotlinCompilerExtensionVersion compose_compiler_version
    }

}

dependencies {
    def composeBom = platform('androidx.compose:compose-bom:2022.11.00')
    implementation(composeBom)

    implementation 'androidx.core:core-ktx:1.3.2'
    implementation 'androidx.activity:activity-compose:1.4.0'
    implementation 'com.google.android.material:material:1.3.0'
    implementation "androidx.compose.ui:ui"
    implementation "androidx.compose.ui:ui-util"
    implementation "androidx.compose.material:material"
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.3.0'
    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
}*/
