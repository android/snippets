/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.inject.Inject
import org.gradle.process.ExecOperations

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.example.compose.snippets"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    lint {
        // Borrowed compose/snippets layouts reference views from compose/snippets code and its
        // dependencies (CoordinatorLayout, Material AppBarLayout, EmojiTextView, ...). Those
        // layouts are already linted in :compose:snippets.
        disable += "MissingClass"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            // Recording animated GIFs keeps every frame in memory until it's encoded.
            all { it.maxHeapSize = "4g" }
        }
    }

    sourceSets {
        getByName("main") {
            res.srcDirs("../../compose/snippets/src/main/res")
        }
        getByName("test") {
            kotlin.directories.add("../wasm/src/wasmJsMain/kotlin/com/example/compose/preview/wasm/registry")
        }
    }
}

androidComponents {
    // This module only exists to run the Roborazzi screenshot test in the debug variant.
    // Its main source set borrows compose/snippets resources (see res.srcDirs above), which
    // reference themes/attrs from libraries this module doesn't depend on (AppCompat, Material
    // Components, Glance, ...). Release resource verification would fail on those, so the
    // unused release variant is disabled.
    beforeVariants(selector().withBuildType("release")) { variantBuilder ->
        variantBuilder.enable = false
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    testImplementation(composeBom)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)

    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(project(":compose:snippets"))
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.rule)
}

tasks.register("generateScreenshots") {
    group = "verification"
    description = "Generates preview screenshots for all Material Component snippets using Roborazzi/Robolectric"
    dependsOn("recordRoborazziDebug")
}

// The screenshot test writes PNGs and GIFs into the wasm resources (see PreviewScreenshotTest), so
// declare them as outputs: Gradle then restores them from the build cache instead of skipping the
// test and leaving the gitignored directory empty.
tasks.withType<Test>().configureEach {
    outputs.dir(rootProject.layout.projectDirectory.dir("preview/wasm/src/wasmJsMain/resources/screenshots"))
}

/**
 * Converts the GIF recordings from `PreviewScreenshotTest.record()` into small H.264 MP4s, the
 * format DAC shows for animated previews, and deletes the GIFs. Needs `ffmpeg` on the PATH
 * (preinstalled on GitHub's Ubuntu runners).
 */
abstract class EncodePreviewVideos @Inject constructor(private val execOps: ExecOperations) : DefaultTask() {
    @get:Internal
    abstract val screenshotsDir: DirectoryProperty

    @TaskAction
    fun encode() {
        val gifs = screenshotsDir.get().asFile.listFiles { file -> file.extension == "gif" }.orEmpty().sorted()
        gifs.forEach { gif ->
            val mp4 = File(gif.parentFile, "${gif.nameWithoutExtension}.mp4")
            execOps.exec {
                commandLine(
                    "ffmpeg", "-loglevel", "error", "-y", "-i", gif.absolutePath,
                    // Even dimensions and yuv420p for H.264 playback in every browser.
                    "-vf", "scale=trunc(iw/2)*2:trunc(ih/2)*2", "-pix_fmt", "yuv420p",
                    "-c:v", "libx264", "-crf", "23", "-movflags", "+faststart",
                    // Reproducible output for the same recording.
                    "-map_metadata", "-1", "-fflags", "+bitexact", "-flags:v", "+bitexact",
                    mp4.absolutePath,
                )
            }
            gif.delete()
        }
        logger.lifecycle("Encoded ${gifs.size} preview recording(s) to MP4.")
    }
}

val encodePreviewVideos by tasks.registering(EncodePreviewVideos::class) {
    description = "Converts the recorded preview GIFs to MP4 for DAC."
    screenshotsDir.set(rootProject.layout.projectDirectory.dir("preview/wasm/src/wasmJsMain/resources/screenshots"))
}

tasks.matching { it.name == "recordRoborazziDebug" }.configureEach {
    finalizedBy(encodePreviewVideos)
}
