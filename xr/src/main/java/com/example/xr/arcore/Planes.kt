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

import androidx.xr.arcore.Plane
import androidx.xr.runtime.Session
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Ray

private suspend fun subscribePlanes(session: Session) {
    // [START androidxr_arcore_planes_subscribe]
    Plane.subscribe(session).collect { planes ->
        // Planes have changed; update plane rendering
    }
    // [END androidxr_arcore_planes_subscribe]
}

private fun hitTestTable(session: Session) {
    val scenecoreSession: androidx.xr.scenecore.Session = null!!
    val pose = scenecoreSession.spatialUser.head?.transformPoseTo(Pose(), scenecoreSession.perceptionSpace) ?: return
    val ray = Ray(pose.translation, pose.forward)
    // [START androidxr_arcore_hitTest]
    val results = androidx.xr.arcore.hitTest(session, ray)
    // When interested in the first Table hit:
    val tableHit = results.firstOrNull {
        val trackable = it.trackable
        trackable is Plane && trackable.state.value.label == Plane.Label.Table
    }
    // [END androidxr_arcore_hitTest]
}
