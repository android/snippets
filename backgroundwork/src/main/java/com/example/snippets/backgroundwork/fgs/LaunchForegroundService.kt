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

package com.example.snippets.backgroundwork.fgs

import android.Manifest
import android.app.ForegroundServiceStartNotAllowedException
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.PermissionChecker

// [START android_backgroundwork_fgs_launch_promote]
class MyCameraService : Service() {

    private fun startForeground() {
        // Before starting the service as foreground check that the app has the
        // appropriate runtime permissions. In this case, verify that the user has
        // granted the CAMERA permission.
        val cameraPermission =
            PermissionChecker.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (cameraPermission != PermissionChecker.PERMISSION_GRANTED) {
            // Without camera permissions the service cannot run in the foreground.
            // Consider informing user or updating your app UI if visible.
            stopSelf()
            return
        }

        try {
            val notification = NotificationCompat.Builder(this, "CHANNEL_ID")
                // Create the notification to display while the service is running.
                .build()
            ServiceCompat.startForeground(
                /* service = */ this,
                /* id = */ 100, // Cannot be 0.
                /* notification = */ notification,
                /* foregroundServiceType = */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
                } else {
                    0
                },
            )
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                e is ForegroundServiceStartNotAllowedException
            ) {
                // App not in a valid state to start foreground service
                // (e.g. started from bg).
            }
            // ...
        }
    }
    // [START_EXCLUDE silent]
    override fun onBind(intent: Intent): IBinder? = null
    // [END_EXCLUDE]
}
// [END android_backgroundwork_fgs_launch_promote]
