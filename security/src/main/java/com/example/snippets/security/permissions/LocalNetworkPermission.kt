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
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
// [START android_security_local_network_permission_request]
fun checkAndRequestLocalNetworkPermission(activity: Activity) {
    val permission = Manifest.permission.ACCESS_LOCAL_NETWORK

    when {
        // 1. Check if permission is already granted (or auto-granted via NEARBY_DEVICES)
        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED -> {
            // Permission is granted. Safely run your local network operations.
            startLocalNetworkOperations()
        }

        // 2. Provide UI rationale if necessary
        ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
            showPermissionRationaleDialog(activity) {
                requestLocalNetworkPermission(activity)
            }
        }

        // 3. Request the permission
        else -> {
            requestLocalNetworkPermission(activity)
        }
    }
}

// [START_EXCLUDE silent]
@RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
// [END_EXCLUDE]
private fun requestLocalNetworkPermission(activity: Activity) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.ACCESS_LOCAL_NETWORK),
        LOCAL_NETWORK_REQUEST_CODE
    )
}
// [END android_security_local_network_permission_request]

private const val LOCAL_NETWORK_REQUEST_CODE = 1001

private fun startLocalNetworkOperations() {
    // Perform local network operations after permission is granted.
}

private fun showPermissionRationaleDialog(activity: Activity, onConfirm: () -> Unit) {
    onConfirm()
}
