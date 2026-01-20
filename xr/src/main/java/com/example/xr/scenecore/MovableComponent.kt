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

import androidx.xr.runtime.Session
import androidx.xr.scenecore.AnchorPlacement
import androidx.xr.scenecore.Entity
import androidx.xr.scenecore.MovableComponent
import androidx.xr.scenecore.PlaneOrientation
import androidx.xr.scenecore.PlaneSemanticType

private fun createSystemMovable(session: Session, entity: Entity) {
    // [START androidxr_scenecore_movableComponent_createSystemMovable]
    val movableComponent = MovableComponent.createSystemMovable(session)
    entity.addComponent(movableComponent)
    // [END androidxr_scenecore_movableComponent_createSystemMovable]
}

private fun movableComponentAnchorExample(session: Session, entity: Entity) {
    // [START androidxr_scenecore_movableComponent_anchorable]
    val anchorPlacement = AnchorPlacement.createForPlanes(
        anchorablePlaneOrientations = setOf(PlaneOrientation.VERTICAL),
        anchorablePlaneSemanticTypes = setOf(PlaneSemanticType.FLOOR, PlaneSemanticType.TABLE)
    )

    val movableComponent = MovableComponent.createAnchorable(
        session = session,
        anchorPlacement = setOf(anchorPlacement)
    )
    entity.addComponent(movableComponent)
    // [END androidxr_scenecore_movableComponent_anchorable]
}
