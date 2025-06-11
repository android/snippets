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
import androidx.xr.runtime.math.FloatSize3d
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Quaternion
import androidx.xr.runtime.math.Vector3
import androidx.xr.scenecore.AnchorPlacement
import androidx.xr.scenecore.Entity
import androidx.xr.scenecore.InputEvent
import androidx.xr.scenecore.InteractableComponent
import androidx.xr.scenecore.MovableComponent
import androidx.xr.scenecore.PlaneOrientation
import androidx.xr.scenecore.PlaneSemanticType
import androidx.xr.scenecore.ResizableComponent
import androidx.xr.scenecore.ResizeListener
import androidx.xr.scenecore.SurfaceEntity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

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

@Suppress("RestrictedApi") // b/416066566
private fun moveableComponentExample(session: Session, entity: Entity) {
    // [START androidxr_scenecore_moveableComponentExample]
    val anchorPlacement = AnchorPlacement.createForPlanes(
        planeTypeFilter = setOf(PlaneOrientation.VERTICAL),
        planeSemanticFilter = setOf(PlaneSemanticType.FLOOR, PlaneSemanticType.TABLE)
    )

    val movableComponent = MovableComponent.create(
        session = session,
        systemMovable = false,
        scaleInZ = false,
        anchorPlacement = setOf(anchorPlacement)
    )
    entity.addComponent(movableComponent)
    // [END androidxr_scenecore_moveableComponentExample]
}

@Suppress("RestrictedApi") // b/416066566
private fun resizableComponentExample(session: Session, entity: Entity, executor: Executor) {
    // [START androidxr_scenecore_resizableComponentExample]
    val resizableComponent = ResizableComponent.create(session)
    resizableComponent.minimumSize = FloatSize3d(177f, 100f, 1f)
    resizableComponent.fixedAspectRatio = 16f / 9f // Specify a 16:9 aspect ratio

    resizableComponent.addResizeListener(
        executor,
        object : ResizeListener {
            override fun onResizeEnd(entity: Entity, finalSize: FloatSize3d) {

                // update the size in the component
                resizableComponent.size = finalSize

                // update the Entity to reflect the new size
                (entity as SurfaceEntity).canvasShape = SurfaceEntity.CanvasShape.Quad(finalSize.width, finalSize.height)
            }
        },
    )

    entity.addComponent(resizableComponent)
    // [END androidxr_scenecore_resizableComponentExample]
}

@Suppress("RestrictedApi") // b/416066566
private fun interactableComponentExample(session: Session, entity: Entity) {
    // [START androidxr_scenecore_interactableComponentExample]
    val executor = Executors.newSingleThreadExecutor()
    val interactableComponent = InteractableComponent.create(session, executor) {
        // when the user disengages with the entity with their hands
        if (it.source == InputEvent.SOURCE_HANDS && it.action == InputEvent.ACTION_UP) {
            // increase size with right hand and decrease with left
            if (it.pointerType == InputEvent.POINTER_TYPE_RIGHT) {
                entity.setScale(1.5f)
            } else if (it.pointerType == InputEvent.POINTER_TYPE_LEFT) {
                entity.setScale(0.5f)
            }
        }
    }
    entity.addComponent(interactableComponent)
    // [END androidxr_scenecore_interactableComponentExample]
}
