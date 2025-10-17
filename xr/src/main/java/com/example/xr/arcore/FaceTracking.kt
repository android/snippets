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

import androidx.xr.arcore.Face
import androidx.xr.arcore.FaceBlendShapeType
import androidx.xr.arcore.FaceConfidenceRegion
import androidx.xr.runtime.Config
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionConfigureConfigurationNotSupported
import androidx.xr.runtime.SessionConfigureSuccess
import androidx.xr.runtime.TrackingState

private fun configureFaceTracking(session: Session) {
    // [START androidxr_arcore_faceTracking_configure]
    val newConfig = session.config.copy(
        faceTracking = Config.FaceTrackingMode.USER,
    )
    when (val result = session.configure(newConfig)) {
        is SessionConfigureSuccess -> TODO(/* Success! */)
        is SessionConfigureConfigurationNotSupported ->
            TODO(/* Some combinations of configurations are not valid. Handle this failure case. */)
        else ->
            TODO(/* The session could not be configured. See SessionConfigureResult for possible causes. */)
    }
    // [END androidxr_arcore_faceTracking_configure]
}

@Suppress("UnusedVariable")
private suspend fun getUserFace(session: Session) {
    // [START androidxr_arcore_faceTracking_getFace]
    val face = Face.getUserFace(session) ?: return
    face.state.collect { state ->
        if (state.trackingState != TrackingState.TRACKING) return@collect

        val confidence = state.getConfidence(FaceConfidenceRegion.FACE_CONFIDENCE_REGION_LOWER)
        val blendShapeValue = state.blendShapes[FaceBlendShapeType.FACE_BLEND_SHAPE_TYPE_LIPS_TOWARD]
    }
    // [END androidxr_arcore_faceTracking_getFace]
}
