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

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

// [START android_security_runtime_permission_request]
class MainActivity : ComponentActivity() {

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCameraPreview()
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    // User selected 'Don't ask again' or permanently denied.
                    // Direct user to Application Details Settings.
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                } else {
                    showSnackbar("Camera permission is required to preview camera feed.")
                }
            }
        }

    fun requestCameraPermissionSafely() {
        val permission = Manifest.permission.CAMERA
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                startCameraPreview()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                showSnackbar("Camera permission is needed to preview camera feed.")
                cameraLauncher.launch(permission)
            }
            else -> {
                cameraLauncher.launch(permission)
            }
        }
    }

    private fun startCameraPreview() {
        Log.d("MainActivity", "Camera preview started")
    }

    private fun showSnackbar(msg: String) {
        Log.i("MainActivity", msg)
    }
}
// [END android_security_runtime_permission_request]

typealias CameraActivity = MainActivity
