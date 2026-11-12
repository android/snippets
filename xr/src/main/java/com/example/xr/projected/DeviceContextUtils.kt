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

package com.example.xr.projected

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val TAG = "ProjectedDeviceContextUtils"

/**
 * Demonstrates how to safely monitor projected device connectivity and obtain a projected
 * context to access the glasses' hardware (like the camera) and system services.
 */
// [START androidxr_get_projected_device_context]
@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@OptIn(ExperimentalProjectedApi::class, ExperimentalCoroutinesApi::class)
fun monitorProjectedDeviceConnectivity(
    activity: ComponentActivity,
    onConnected: suspend (projectedContext: Context) -> (() -> Unit)?
) {
    activity.lifecycleScope.launch {
        var cleanupAction: (() -> Unit)? = null

        try {
            // Before creating a projected context, verify that the projected device
            // is connected to the host device (phone).
            // While this flow collects true, the active projected context remains valid.
            ProjectedContext.isProjectedDeviceConnected(activity, coroutineContext)
                .distinctUntilChanged()
                .collectLatest { isConnected ->
                    // Clean up any previous session resources prior to state transitions.
                    // Any previously acquired projected context is now invalid.
                    cleanupAction?.invoke()
                    cleanupAction = null

                    if (!isConnected) {
                        Log.i(TAG, "Projected device disconnected. Cleaning up hardware resources...")
                        return@collectLatest
                    }

                    // Inside a phone Activity, explicitly create a projected context
                    // to access the glasses' hardware and system services (e.g., CameraManager).
                    // Note: If you are already inside a projected Activity, the default context
                    // is already a projected context.
                    val projectedContext = try {
                        ProjectedContext.createProjectedDeviceContext(activity)
                    } catch (e: IllegalStateException) {
                        Log.e(TAG, "Failed to create projected context", e)
                        return@collectLatest
                    }

                    // Use the new, valid `projectedContext` to initialize system services (e.g., CameraManager).
                    Log.i(TAG, "Projected device connected. Initializing hardware...")
                    cleanupAction = onConnected(projectedContext)
                }
        } finally {
            // Guarantee final resource cleanup on Activity destruction or scope cancellation.
            cleanupAction?.invoke()
            cleanupAction = null
        }
    }
}
// [END androidxr_get_projected_device_context]

/**
 * Demonstrates how to obtain a context for the host device (the phone) to access
 * phone-specific hardware and system services (such as the phone's camera
 * or the phone's AudioManager).
 *
 * You must use this method when running inside a projected Activity or Service,
 * because their default [Context] is bound to the projected device (the glasses),
 * restricting direct access to the host phone's hardware.
 */
// [START androidxr_get_host_device_context]
@OptIn(ExperimentalProjectedApi::class)
fun getHostDeviceContext(context: Context): Context {
    // Obtain the host device (phone) context.
    // The `context` argument passed in can be any context (projected, host, Service, or App).
    return ProjectedContext.createHostDeviceContext(context)
}
// [END androidxr_get_host_device_context]
