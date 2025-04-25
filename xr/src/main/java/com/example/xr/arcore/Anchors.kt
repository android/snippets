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

import androidx.xr.arcore.Anchor
import androidx.xr.arcore.AnchorCreateSuccess
import androidx.xr.arcore.Trackable
import androidx.xr.runtime.Session
import androidx.xr.runtime.math.Pose
import androidx.xr.scenecore.AnchorEntity
import androidx.xr.scenecore.Entity

private fun createAnchorAtPose(session: Session, pose: Pose) {
    val pose = Pose()
    // [START androidxr_arcore_anchor_create]
    when (val result = Anchor.create(session, pose)) {
        is AnchorCreateSuccess -> { /* anchor stored in `result.anchor`. */ }
        else -> { /* handle failure */ }
    }
    // [END androidxr_arcore_anchor_create]
}

private fun createAnchorAtTrackable(trackable: Trackable<*>) {
    val pose = Pose()
    // [START androidxr_arcore_anchor_create_trackable]
    when (val result = trackable.createAnchor(pose)) {
        is AnchorCreateSuccess -> { /* anchor stored in `result.anchor`. */ }
        else -> { /* handle failure */ }
    }
    // [END androidxr_arcore_anchor_create_trackable]
}

private fun attachEntityToAnchor(
    session: androidx.xr.scenecore.Session,
    entity: Entity,
    anchor: Anchor
) {
    // [START androidxr_arcore_entity_tracks_anchor]
    AnchorEntity.create(session, anchor).apply {
        setParent(session.activitySpace)
        addChild(entity)
    }
    // [END androidxr_arcore_entity_tracks_anchor]
}
