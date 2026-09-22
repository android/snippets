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

package com.example.snippets.backgroundwork

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager

// [START android_background_custom_configuration_on_demand]
class MyApplication() : Application(), Configuration.Provider {
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
// [END android_background_custom_configuration_on_demand]

private fun manualInitialization(context: Context) {
    // [START android_background_custom_configuration_manual_init]
    // provide custom configuration
    val myConfig = Configuration.Builder()
        .setMinimumLoggingLevel(android.util.Log.INFO)
        .build()

    // initialize WorkManager
    WorkManager.initialize(context, myConfig)
    // [END android_background_custom_configuration_manual_init]
}
