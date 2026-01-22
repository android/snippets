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

package com.example.xr.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SceneCoreEntity
import androidx.xr.compose.subspace.SceneCoreEntitySizeAdapter
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.offset
import androidx.xr.runtime.math.IntSize2d
import androidx.xr.runtime.math.Pose
import androidx.xr.scenecore.SurfaceEntity

@Composable
fun SceneCoreEntityExample() {
    val session = LocalSession.current
    if (session !== null) {
        // [START androidxr_compose_SceneCoreEntity]
        Subspace {
            SceneCoreEntity(
                modifier = SubspaceModifier.offset(x = 50.dp),
                factory = {
                    SurfaceEntity.create(
                        session = session,
                        pose = Pose.Identity,
                        stereoMode = SurfaceEntity.StereoMode.MONO
                    )
                },
                update = { entity ->
                    // compose state changes may be applied to the
                    // SceneCore entity here.
                    entity.stereoMode = SurfaceEntity.StereoMode.SIDE_BY_SIDE
                },
                sizeAdapter =
                    SceneCoreEntitySizeAdapter({
                        IntSize2d(it.width, it.height)
                    }),
            ) {
                // Content here will be children of the SceneCoreEntity
                // in the scene graph.
            }
        }
        // [END androidxr_compose_SceneCoreEntity]
    }
}