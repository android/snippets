/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.compose.snippets.performance

import android.app.Application
import androidx.compose.runtime.Composer
import androidx.compose.runtime.tooling.ComposeStackTraceMode
import com.google.maps.android.ktx.BuildConfig

// [START android_compose_stacktraces_enable]
class SampleStackTracesEnabledApp : Application() {

    override fun onCreate() {
        // If the App is a debug variant, include the source information,
        // otherwise do the less accurate GroupKeys option through Auto.
        Composer.setDiagnosticStackTraceMode(if (BuildConfig.DEBUG) {
            ComposeStackTraceMode.SourceInformation
        } else {
            ComposeStackTraceMode.Auto
        })
    }
}
// [END android_compose_stacktraces_enable]
