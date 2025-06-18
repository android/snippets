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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.xr.runtime.Session
import androidx.xr.scenecore.GltfModel
import androidx.xr.scenecore.GltfModelEntity
import androidx.xr.scenecore.SpatialCapabilities
import androidx.xr.scenecore.scene
import kotlinx.coroutines.guava.await
import java.nio.file.Paths

private suspend fun loadGltfFile(session: Session) {
    // [START androidxr_scenecore_gltfmodel_create]
    val gltfModel = GltfModel.createAsync(session, Paths.get("models", "saturn_rings.glb")).await()
    // [END androidxr_scenecore_gltfmodel_create]
}

@Suppress("RestrictedApi") // b/416066566
private fun createModelEntity(session: Session, gltfModel: GltfModel) {
    // [START androidxr_scenecore_gltfmodelentity_create]
    if (session.scene.spatialCapabilities
        .hasCapability(SpatialCapabilities.SPATIAL_CAPABILITY_3D_CONTENT)
    ) {
        val gltfEntity = GltfModelEntity.create(session, gltfModel)
    }
    // [END androidxr_scenecore_gltfmodelentity_create]
}

@Suppress("RestrictedApi") // b/416066566
private fun animateEntity(gltfEntity: GltfModelEntity) {
    // [START androidxr_scenecore_gltfmodelentity_animation]
    gltfEntity.startAnimation(loop = true, animationName = "Walk")
    // [END androidxr_scenecore_gltfmodelentity_animation]
}

private fun ComponentActivity.startSceneViewer() {
    // [START androidxr_scenecore_sceneviewer]
    val url =
        "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf"
    val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
    val intentUri =
        Uri.parse("https://arvr.google.com/scene-viewer/1.2")
            .buildUpon()
            .appendQueryParameter("file", url)
            .build()
    sceneViewerIntent.setData(intentUri)
    try {
        startActivity(sceneViewerIntent)
    } catch (e: ActivityNotFoundException) {
        // There is no activity that could handle the intent.
    }
    // [END androidxr_scenecore_sceneviewer]
}
