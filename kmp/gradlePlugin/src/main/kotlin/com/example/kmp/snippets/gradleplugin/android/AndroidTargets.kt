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

package com.example.kmp.snippets.gradleplugin.android

// [START android_kmp_gradleplugin_android_targets]
import com.android.build.api.AndroidPluginVersion
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget

class MyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
            kmpExtension.targets.configureEach { target ->
                if (target is KotlinAndroidTarget) {
                    // Old kmp android integration using com.android.library or com.android.application.
                }
                if (target.platformType == KotlinPlatformType.androidJvm &&
                    AndroidPluginVersion.getCurrent() >= AndroidPluginVersion(8, 8) &&
                    target is KotlinMultiplatformAndroidLibraryTarget
                ) {
                    // New kmp android integration using com.android.kotlin.multiplatform.library.
                }
            }
        }
    }
}
// [END android_kmp_gradleplugin_android_targets]
