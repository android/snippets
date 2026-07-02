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

package com.example.wear.snippets.alwayson

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.wear.ambient.AmbientLifecycleObserver

// [START android_wear_ongoing_activity_ambientlifecycleobserver]
val ambientCallback = object : AmbientLifecycleObserver.AmbientLifecycleCallback {
    override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {
        // ... Called when moving from interactive mode into ambient mode.
        // Adjust UI for low-power state: dim colors, hide non-essential elements.
    }

    override fun onExitAmbient() {
        // ... Called when leaving ambient mode, back into interactive mode.
        // Restore full UI.
    }

    override fun onUpdateAmbient() {
        // ... Called by the system periodically (typically once per minute)
        // to allow the app to update its display while in ambient mode.
    }
}
// [END android_wear_ongoing_activity_ambientlifecycleobserver]

class AmbientLifecycleActivity : ComponentActivity() {

    private val activity = this // rename so the snippet reads better
    // [START android_wear_ongoing_activity_ambientlifecycleobserver_oncreate]
    private val ambientObserver = AmbientLifecycleObserver(activity, ambientCallback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(ambientObserver)

        // ...
    }
    // [END android_wear_ongoing_activity_ambientlifecycleobserver_oncreate]

    // [START android_wear_ongoing_activity_ambientlifecycleobserver_ondestroy]
    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(ambientObserver)

        // ...
    }
    // [END android_wear_ongoing_activity_ambientlifecycleobserver_ondestroy]
}
