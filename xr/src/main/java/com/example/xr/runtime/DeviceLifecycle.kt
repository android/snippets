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
import androidx.lifecycle.Lifecycle
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import androidx.xr.runtime.ExperimentalXrDeviceLifecycleApi
import androidx.xr.runtime.XrDevice
import kotlinx.coroutines.flow.takeWhile

@OptIn(ExperimentalXrDeviceLifecycleApi::class, ExperimentalProjectedApi::class)
suspend fun collectDeviceLifecycle(context: Context) {

    val projectedContext = ProjectedContext.createProjectedDeviceContext(context)

    // [START androidxr_device_lifecycle_collect]
    val xrDevice = XrDevice.getCurrentDevice(projectedContext)

    xrDevice.getLifecycle().currentStateFlow
        .takeWhile { it != Lifecycle.State.DESTROYED }
        .collect { state ->
            when (state) {
                Lifecycle.State.STARTED -> { /* Device is ACTIVE (worn) */ }
                Lifecycle.State.CREATED -> { /* Device is INACTIVE (not worn) */ }
                else -> { /* Handle other states */ }
            }
        }
}
// [END androidxr_device_lifecycle_collect]
