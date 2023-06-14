plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.example.slice"

    defaultConfig {
        applicationId = "com.example.slice"
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
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        // Disable unused AGP features
        buildConfig = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging.resources {
        // Multiple dependency bring these files in. Exclude them to enable
        // our test APK to build (has no effect on our AARs)
        excludes += "/META-INF/AL2.0"
        excludes += "/META-INF/LGPL2.1"
    }
}
dependencies {
    implementation(project(":shared"))
 // TODO   implementation(fileTree(dir: 'libs', include: ['*.jar']))
    implementation(libs.kotlin.stdlib)
    implementation("androidx.slice:slice-builders-ktx:1.0.0-alpha4")
    implementation("androidx.appcompat:appcompat:1.0.0-beta01")
    implementation("androidx.cardview:cardview:1.0.0-beta01")
    testImplementation("junit:junit:4.12")
}

/*
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'

android {
    compileSdkVersion 28

    defaultConfig {
        applicationId "com.example.android.snippets"
        minSdkVersion 19
        targetSdkVersion 31
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

    lintOptions {
        disable 'GoogleAppIndexingWarning'
    }

}

dependencies {
    implementation project(':shared')
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    implementation "androidx.slice:slice-builders-ktx:1.0.0-alpha4"
    implementation "androidx.appcompat:appcompat:1.0.0-beta01"
    implementation "androidx.cardview:cardview:1.0.0-beta01"
    testImplementation 'junit:junit:4.12'
}
*/
