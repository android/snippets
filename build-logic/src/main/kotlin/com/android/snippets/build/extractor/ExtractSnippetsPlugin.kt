/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.snippets.build.extractor

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * Gradle Settings plugin that registers documentation snippet extraction tasks across all
 * non-root projects in an Isolated-Projects-compatible manner.
 *
 * This plugin scans source files across standard Android, JVM, iOS, and Kotlin Multiplatform
 * source sets (including `.kt`, `.java`, `.xml`, `.json`, `.proto`, `.swift`, `.kts`, `.gradle`,
 * `.pro`, and extensionless snippet files), extracts regions bounded by `[START <tag>]` and
 * `[END <tag>]`, and applies transformations (exclusion handling and indentation normalization)
 * matching documentation site rendering.
 *
 * ### Output Destination
 * Extracted snippets are written to `<subproject>/build/extracted-snippets/<tag>.<extension>`.
 *
 * ### Usage Examples
 *
 * **1. Extract all snippets across the entire repository:**
 * ```bash
 * ./gradlew extractSnippets
 * ```
 * Output: Saved to each submodule's `build/extracted-snippets/` directory (e.g., `compose/snippets/build/extracted-snippets/`, `views/build/extracted-snippets/`, etc.).
 *
 * **2. Extract snippets for a specific submodule (e.g. Kotlin Multiplatform):**
 * ```bash
 * ./gradlew :kmp:shared:extractSnippets
 * ```
 * Output: Saved to `kmp/shared/build/extracted-snippets/` (e.g., `kmp/shared/build/extracted-snippets/android_kmp_viewmodel_class.kt`).
 *
 * **3. Filter by a specific region tag in a module (e.g. Compose snippets):**
 * ```bash
 * ./gradlew :compose:snippets:extractSnippets --tag=android_compose_navigation3_basic_2
 * # Or using property flag:
 * ./gradlew :compose:snippets:extractSnippets -Ptag=android_compose_navigation3_basic_2
 * ```
 * Output: Saved to `compose/snippets/build/extracted-snippets/android_compose_navigation3_basic_2.kt`.
 *
 * **4. Filter by package or directory substring in a module (e.g. Room database snippets):**
 * ```bash
 * ./gradlew :room:extractSnippets --package=migration
 * # Or using property flag:
 * ./gradlew :room:extractSnippets -Ppackage=migration
 * ```
 * Output: Saved to `room/build/extracted-snippets/` for matching snippets under `room/.../migration/`.
 */
class ExtractSnippetsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.gradle.lifecycle.beforeProject {
            if (path == ":") return@beforeProject

            val projectPath = path
            val projectProviders = providers
            val projectLayout = layout

            tasks.register("extractSnippets", ExtractSnippetsTask::class.java) {
                group = "documentation"
                description =
                    "Extracts documentation snippets for $projectPath matching documentation site transformations."

                targetTag.convention(projectProviders.gradleProperty("tag"))
                targetPackage.convention(projectProviders.gradleProperty("package"))
                projectRootDir.set(projectLayout.projectDirectory)

                sourceFiles.setFrom(
                    projectLayout.projectDirectory.asFileTree.matching {
                        include(
                            "src/**",
                            "iosApp/**",
                            "*.gradle.kts",
                            "*.gradle",
                            "*.pro",
                        )
                        exclude(
                            "**/build/**",
                            "**/spotless/**",
                            "**/.gradle/**",
                            "**/.idea/**",
                            "**/.DS_Store",
                            "**/*.png",
                            "**/*.jpg",
                            "**/*.jpeg",
                            "**/*.webp",
                            "**/*.gif",
                            "**/*.bmp",
                            "**/*.ico",
                            "**/*.heic",
                            "**/*.mp4",
                            "**/*.mkv",
                            "**/*.webm",
                            "**/*.mp3",
                            "**/*.wav",
                            "**/*.ogg",
                            "**/*.ttf",
                            "**/*.otf",
                            "**/*.woff",
                            "**/*.woff2",
                            "**/*.apk",
                            "**/*.aab",
                            "**/*.jar",
                            "**/*.aar",
                            "**/*.dex",
                            "**/*.class",
                            "**/*.so",
                            "**/*.dylib",
                            "**/*.a",
                            "**/*.tflite",
                            "**/*.pb",
                            "**/*.bin",
                            "**/*.pdf",
                            "**/*.zip",
                            "**/*.gz",
                            "**/*.tar",
                            "**/*.keystore",
                            "**/*.jks",
                        )
                    }
                )
                outputDir.set(projectLayout.buildDirectory.dir("extracted-snippets"))
            }
        }
    }
}
