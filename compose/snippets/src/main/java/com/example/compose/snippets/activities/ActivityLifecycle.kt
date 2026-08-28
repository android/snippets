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

package com.example.compose.snippets.activities

import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

private object ResumeExample {
    // [START android_activities_lifecycle_camera_component_resume]
    class CameraComponent : LifecycleObserver {
        // ...
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun initializeCamera() {
            if (camera == null) {
                getCamera()
            }
        }
        // ...
        // [START_EXCLUDE silent]
        private var camera: Camera? = null
        private fun getCamera() {
            camera = Camera.open()
        }
        // [END_EXCLUDE]
    }
    // [END android_activities_lifecycle_camera_component_resume]
}

private object PauseExample {
    // [START android_activities_lifecycle_camera_component_pause]
    class CameraComponent : LifecycleObserver {
        // ...
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun releaseCamera() {
            camera?.release()
            camera = null
        }
        // ...
        // [START_EXCLUDE silent]
        private var camera: Camera? = null
        // [END_EXCLUDE]
    }
    // [END android_activities_lifecycle_camera_component_pause]
}

private class NoteActivity : ComponentActivity() {
    private val noteViewModel = NoteViewModel()

    // [START android_activities_lifecycle_on_stop_save]
    override fun onStop() {
        super.onStop()

        // Delegate the save operation to the ViewModel, which handles the
        // background thread operations (e.g., using Kotlin Coroutines and Room).
        noteViewModel.saveDraft()
    }
    // [END android_activities_lifecycle_on_stop_save]
}

@Composable
private fun StartActivityButtonSnippet() {
    // [START android_activities_lifecycle_start_activity_button]
    val context = LocalContext.current

    Button(onClick = {
        val intent = Intent(context, SignInActivity::class.java)
        context.startActivity(intent)
    }) {
        Text("Sign In")
    }
    // [END android_activities_lifecycle_start_activity_button]
}

private fun startExternalActivitySnippet(recipientArray: Array<String>, context: Context) {
    // [START android_activities_lifecycle_start_external_activity]
    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_EMAIL, recipientArray)
    }
    context.startActivity(intent)
    // [END android_activities_lifecycle_start_external_activity]
}

private class NoteViewModel {
    fun saveDraft() {}
}

private class SignInActivity : ComponentActivity()
