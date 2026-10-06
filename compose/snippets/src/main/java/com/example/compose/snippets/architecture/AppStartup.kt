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

package com.example.compose.snippets.architecture

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.AppInitializer
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager

@SuppressLint("EnsureInitializerMetadata")
private object AppStartupSnippets {
    class ExampleLogger(val workManager: WorkManager)

    // [START android_architecture_app_startup_workmanager_initializer]
    // Initializes WorkManager.
    class WorkManagerInitializer : Initializer<WorkManager> {
        override fun create(context: Context): WorkManager {
            val configuration = Configuration.Builder().build()
            WorkManager.initialize(context, configuration)
            return WorkManager.getInstance(context)
        }
        override fun dependencies(): List<Class<out Initializer<*>>> {
            // No dependencies on other libraries.
            return emptyList()
        }
    }
    // [END android_architecture_app_startup_workmanager_initializer]

    // [START android_architecture_app_startup_logger_initializer]
    // Initializes ExampleLogger.
    class ExampleLoggerInitializer : Initializer<ExampleLogger> {
        override fun create(context: Context): ExampleLogger {
            // WorkManager.getInstance() is non-null only after
            // WorkManager is initialized.
            return ExampleLogger(WorkManager.getInstance(context))
        }

        override fun dependencies(): List<Class<out Initializer<*>>> {
            // Defines a dependency on WorkManagerInitializer so it can be
            // initialized after WorkManager is initialized.
            return listOf(WorkManagerInitializer::class.java)
        }
    }
    // [END android_architecture_app_startup_logger_initializer]

    fun manualInitializeExample(context: Context) {
        // [START android_architecture_app_startup_manual_init]
        AppInitializer.getInstance(context)
            .initializeComponent(ExampleLoggerInitializer::class.java)
        // [END android_architecture_app_startup_manual_init]
    }
}
