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

package com.example.kmp.snippets.gradleplugin.extension

// [START android_kmp_gradleplugin_android_extension]
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId("com.android.kotlin.multiplatform.library") {
            val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

            // Access the Android target, which also serves as the Android-specific DSL extension.
            kmpExtension.targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach { androidTarget ->

                // You can now access properties and methods from both
                // KotlinMultiplatformAndroidLibraryTarget and KotlinMultiplatformAndroidLibraryExtension.
                androidTarget.compileSdk = 34
                androidTarget.namespace = "com.example.myplugin.library"
                androidTarget.withJava() // enable Java sources.
            }
        }
    }
}
// [END android_kmp_gradleplugin_android_extension]
