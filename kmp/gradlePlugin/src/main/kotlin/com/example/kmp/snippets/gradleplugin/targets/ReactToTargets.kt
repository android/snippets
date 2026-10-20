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

package com.example.kmp.snippets.gradleplugin.targets

// [START android_kmp_gradleplugin_react_targets]
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
            kmpExtension.targets.configureEach { target ->
                // 'target' is an instance of KotlinTarget.
                val targetName = target.name // for example, "android", "iosX64", "jvm".
                val platformType = target.platformType // for example, androidJvm, jvm, native, js.
            }
        }
    }
}
// [END android_kmp_gradleplugin_react_targets]
