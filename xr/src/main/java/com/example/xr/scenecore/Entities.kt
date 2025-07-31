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

package com.example.xr.scenecore

import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Quaternion
import androidx.xr.runtime.math.Vector3
import androidx.xr.scenecore.Entity

private fun setPoseExample(entity: Entity) {
    // [START androidxr_scenecore_entity_setPoseExample]
    // Place the entity forward 2 meters
    val newPosition = Vector3(0f, 0f, -2f)
    // Rotate the entity by 180 degrees on the up axis (upside-down)
    val newOrientation = Quaternion.fromEulerAngles(0f, 0f, 180f)
    // Update the position and rotation on the entity
    entity.setPose(Pose(newPosition, newOrientation))
    // [END androidxr_scenecore_entity_setPoseExample]
}

private fun disableEntity(entity: Entity) {
    // [START androidxr_scenecore_entity_setEnabled]
    // Disable the entity.
    entity.setEnabled(false)
    // [END androidxr_scenecore_entity_setEnabled]
}

private fun entitySetScale(entity: Entity) {
    // [START androidxr_scenecore_entity_entitySetScale]
    // Double the size of the entity
    entity.setScale(2f)
    // [END androidxr_scenecore_entity_entitySetScale]
}
