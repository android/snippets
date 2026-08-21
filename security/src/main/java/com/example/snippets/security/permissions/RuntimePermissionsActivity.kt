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

package com.example.snippets.security.permissions

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

// [START android_security_runtime_permission_request]
class CameraActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startCameraPreview()
            } else {
                showPermissionDeniedFeedback()
            }
        }

    fun checkAndLaunchCamera(permission: String) {
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                startCameraPreview()
            }
            // Handle the case where the user denied the permission before.
            shouldShowRequestPermissionRationale(permission) -> {
                showPermissionRationaleAndRetry(permission)
            }
            else -> {
                // Request the permission for the first time.
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showPermissionRationaleAndRetry(permission: String) {
        // UI logic to explain why the permission is needed, then re-trigger launch:
        // requestPermissionLauncher.launch(permission)
    }

    private fun startCameraPreview() { /* Camera preview initialization logic */ }
    private fun showPermissionDeniedFeedback() { /* UI warning indicating permission is required */ }
}
// [END android_security_runtime_permission_request]
