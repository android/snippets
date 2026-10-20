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

package com.example.kmp.snippets.gradleplugin.compilations

// [START android_kmp_gradleplugin_compilations]
import com.android.build.api.dsl.KotlinMultiplatformAndroidCompilation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId("com.android.kotlin.multiplatform.library") {
            val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
            kmpExtension.targets.configureEach { target ->
                target.compilations.configureEach { compilation ->
                    // standard compilations are usually 'main' and 'test'.
                    // android target has 'main', 'hostTest', 'deviceTest'.
                    val compilationName = compilation.name

                    // Access the default source set for this compilation.
                    val defaultSourceSet = compilation.defaultSourceSet

                    // Access the Android-specific compilation DSL.
                    if (compilation is KotlinMultiplatformAndroidCompilation) {
                    }

                    // Access and configure the Kotlin compilation task.
                    compilation.compileTaskProvider.configure { compileTask ->
                    }
                }
            }
        }
    }
}
// [END android_kmp_gradleplugin_compilations]
