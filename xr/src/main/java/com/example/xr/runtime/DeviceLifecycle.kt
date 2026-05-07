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

package com.example.xr.runtime

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import androidx.xr.runtime.ExperimentalXrDeviceLifecycleApi
import androidx.xr.runtime.XrDevice
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@OptIn(ExperimentalXrDeviceLifecycleApi::class, ExperimentalProjectedApi::class,
    ExperimentalCoroutinesApi::class,

)
suspend fun collectDeviceLifecycle(context: Context) {
    // [START androidxr_device_lifecycle_collect]
    // In your phone activity or service, check for projected device connection state before
    // attempting to create a projected device context and get the device lifecycle.
    ProjectedContext.isProjectedDeviceConnected(context, currentCoroutineContext())
        .flatMapLatest { isConnected ->
            if (isConnected) {
                try {
                    // Create the projected device context on connection
                    val projectedContext = ProjectedContext.createProjectedDeviceContext(context)
                    val xrDevice = XrDevice.getCurrentDevice(projectedContext)

                    // Get the device lifecycle
                    xrDevice.getLifecycle().currentStateFlow
                } catch (e: Exception) {
                    flowOf(Lifecycle.State.DESTROYED)
                }
            } else {
                flowOf(Lifecycle.State.DESTROYED)
            }
        }
        .collect { state ->
            when (state) {
                Lifecycle.State.STARTED -> { /* Device is available (worn) */ }
                Lifecycle.State.CREATED -> { /* Device is unavailable (not worn) */ }
                Lifecycle.State.DESTROYED -> { /* Device is disconnected from host phone */ }
                else -> { /* Handle other states */ }
            }
        }
}
// [END androidxr_device_lifecycle_collect]
