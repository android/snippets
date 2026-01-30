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

import androidx.xr.arcore.DepthMap
import androidx.xr.runtime.Config
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionConfigureSuccess

private fun configureDepthEstimation(session: Session) {
    // [START androidxr_arcore_depthmaps_configure]
    val newConfig = session.config.copy(
        depthEstimation = Config.DepthEstimationMode.SMOOTH_ONLY,
    )
    when (val result = session.configure(newConfig)) {
        is SessionConfigureSuccess -> TODO(/* Success! */)
        else ->
            TODO(/* The session could not be configured. See SessionConfigureResult for possible causes. */)
    }
    // [END androidxr_arcore_depthmaps_configure]
}

@Suppress("UnusedVariable")
private suspend fun getDepthMap(session: Session) {
    // [START androidxr_arcore_depthmaps_obtain_depth_map]
    val depthMap = DepthMap.left(session) ?: return
    // [END androidxr_arcore_depthmaps_obtain_depth_map]

    // [START androidxr_arcore_depthmaps_calculate_results]
    depthMap.state.collect { depthMap ->
        // example coordinates
        val x = 200
        val y = 350
        val index = x + y * depthMap.width
        val result = depthMap.smoothDepthMap?.get(index)
        val confidence = depthMap.smoothConfidenceMap?.get(index)
    }
    // [END androidxr_arcore_depthmaps_calculate_results]
}
