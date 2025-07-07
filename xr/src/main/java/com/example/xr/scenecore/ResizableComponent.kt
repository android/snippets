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
import androidx.xr.scenecore.Entity
import androidx.xr.scenecore.ResizableComponent
import androidx.xr.scenecore.ResizeListener
import androidx.xr.scenecore.SurfaceEntity
import java.util.concurrent.Executor

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
