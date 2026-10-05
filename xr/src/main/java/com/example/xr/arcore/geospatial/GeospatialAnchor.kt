/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.xr.arcore.geospatial

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.xr.arcore.AnchorCreateSuccess
import androidx.xr.arcore.AnchorResult
import androidx.xr.arcore.Geospatial
import androidx.xr.arcore.GeospatialSurface
import androidx.xr.compose.spatial.ExperimentalFollowingSubspaceApi
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.animation.follow.FollowTarget
import androidx.xr.runtime.Session
import androidx.xr.runtime.math.IntSize2d
import androidx.xr.runtime.math.Quaternion
import androidx.xr.scenecore.AnchorSpace
import androidx.xr.scenecore.PanelEntity

private fun createAnchor(
    geospatial: Geospatial,
    latitude: Double,
    longitude: Double,
    altitude: Double
) {
    // [START androidxr_arcore_geospatial_createAnchor]
    val createAnchorResult = geospatial.createAnchor(
        latitude,
        longitude,
        altitude,
        Quaternion.Identity,
    )
    // [END androidxr_arcore_geospatial_createAnchor]
}

private suspend fun createAnchorOnSurface(
    geospatial: Geospatial,
    latitude: Double,
    longitude: Double,
    altitude: Double
) {
    // [START androidxr_arcore_geospatial_createAnchorOnSurface]
    val createAnchorResult = geospatial.createAnchorOnSurface(
        latitude,
        longitude,
        altitude,
        Quaternion.Identity,
        GeospatialSurface.TERRAIN,
    )
    // [END androidxr_arcore_geospatial_createAnchorOnSurface]
}

private fun checkAnchorCreateResult(createAnchorResult: AnchorResult) {
    // [START androidxr_arcore_geospatial_checkAnchorCreateResult]
    when (createAnchorResult) {
        is AnchorCreateSuccess -> {
            // Use anchorCreateResult.anchor to render content at the result.
        }

        else -> {
            // Anchor create failed.
            // See AnchorCreateResult for possible failure reasons.
        }
    }
    // [END androidxr_arcore_geospatial_checkAnchorCreateResult]
}

@OptIn(ExperimentalFollowingSubspaceApi::class)
@Composable
private fun UseGeospatialAnchorCompose(session: Session, anchorCreateResult: AnchorCreateSuccess) {
    // [START androidxr_arcore_geospatial_useAnchorCreateResult_compose]
    Subspace(FollowTarget.anchor(anchorCreateResult.anchor)) {
        SpatialPanel {
            GeospatialContent()
        }
    }
    // [END androidxr_arcore_geospatial_useAnchorCreateResult_compose]
}

private fun useGeospatialAnchorSceneCore(
    context: ComponentActivity,
    session: Session,
    anchorCreateResult: AnchorCreateSuccess
) {
    // [START androidxr_arcore_geospatial_useAnchorCreateResult_scenecore]
    val anchorSpace = AnchorSpace.create(session, anchorCreateResult.anchor)
    val contentEntity = createGeospatialContentEntity(context, session)
    anchorSpace.addChild(contentEntity)
    // [END androidxr_arcore_geospatial_useAnchorCreateResult_scenecore]
}

@Composable
private fun GeospatialContent() {
    Box(
        Modifier
            .background(Color.White)
            .fillMaxSize()
    )
}

private fun createGeospatialContentEntity(
    context: ComponentActivity,
    session: Session
): PanelEntity {
    val panelContent = ComposeView(context).apply {
        setViewTreeLifecycleOwner(context)
        setViewTreeSavedStateRegistryOwner(context)
    }
    return PanelEntity.create(
        session = session,
        view = panelContent,
        pixelDimensions = IntSize2d(500, 500),
        name = "panel entity"
    )
}