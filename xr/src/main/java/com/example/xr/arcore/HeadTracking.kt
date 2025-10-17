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

package com.example.xr.arcore

import androidx.xr.arcore.RenderViewpoint
import androidx.xr.runtime.Config
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionConfigureConfigurationNotSupported
import androidx.xr.runtime.SessionConfigureSuccess

private fun configureSession(session: Session) {
    // [START androidxr_arcore_headtracking_configure]
    val newConfig = session.config.copy(
        headTracking = Config.HeadTrackingMode.LAST_KNOWN,
    )
    when (val result = session.configure(newConfig)) {
        is SessionConfigureSuccess -> TODO(/* Success! */)
        is SessionConfigureConfigurationNotSupported ->
            TODO(/* Some combinations of configurations are not valid. Handle this failure case. */)
        else ->
            TODO(/* The session could not be configured. See SessionConfigureResult for possible causes. */)
    }
    // [END androidxr_arcore_headtracking_configure]
}

private suspend fun getMonoViewpoint(session: Session) {
    // [START androidxr_arcore_headtracking_mono]
    val mono = RenderViewpoint.mono(session) ?: return
    mono.state.collect { state ->
        val fov = state.fieldOfView
        val viewpointPose = state.pose
    }
    // [END androidxr_arcore_headtracking_mono]
}
