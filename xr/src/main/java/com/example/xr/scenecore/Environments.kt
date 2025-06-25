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

import android.content.Context
import androidx.xr.runtime.Session
import androidx.xr.scenecore.ExrImage
import androidx.xr.scenecore.GltfModel
import androidx.xr.scenecore.SpatialEnvironment
import androidx.xr.scenecore.scene
import kotlinx.coroutines.guava.await
import java.nio.file.Paths

private class Environments(val session: Session) {
    suspend fun loadEnvironmentGeometry(context: Context) {
        // [START androidxr_scenecore_environment_loadEnvironmentGeometry]
        val environmentGeometry = GltfModel.create(session, Paths.get("DayGeometry.glb"))
        // [END androidxr_scenecore_environment_loadEnvironmentGeometry]
    }

    suspend fun loadEnvironmentSkybox() {
        // [START androidxr_scenecore_environment_loadEnvironmentSkybox]
        val lightingForSkybox = ExrImage.createFromZipAsync(session, Paths.get("BlueSkyboxLighting.zip")).await()
        // [END androidxr_scenecore_environment_loadEnvironmentSkybox]
    }

    @Suppress("RestrictedApi") // b/416066566
    fun setEnvironmentPreference(environmentGeometry: GltfModel, lightingForSkybox: ExrImage) {
        // [START androidxr_scenecore_environment_setEnvironmentPreference]
        val spatialEnvironmentPreference =
            SpatialEnvironment.SpatialEnvironmentPreference(lightingForSkybox, environmentGeometry)
        session.scene.spatialEnvironment.preferredSpatialEnvironment = spatialEnvironmentPreference
        if (session.scene.spatialEnvironment.isPreferredSpatialEnvironmentActive) {
            // The environment was successfully updated and is now visible, and any listeners
            // specified using addOnSpatialEnvironmentChangedListener will be notified.
        } else {
            // The passthrough opacity preference was successfully set, but not
            // immediately visible. The passthrough opacity change will be applied
            // when the activity has the SPATIAL_CAPABILITY_APP_ENVIRONMENT capability.
            // Then, any listeners specified using addOnSpatialEnvironmentChangedListener
            // will be notified.
         }
        // [END androidxr_scenecore_environment_setEnvironmentPreference]
    }

    @Suppress("RestrictedApi") // b/416066566
    fun setPassthroughOpacityPreference() {
        // [START androidxr_scenecore_environment_setPassthroughOpacityPreference]
        session.scene.spatialEnvironment.preferredPassthroughOpacity = 1.0f
        if (session.scene.spatialEnvironment.currentPassthroughOpacity == 1.0f) {
            // The passthrough opacity request succeeded and should be visible now, and any listeners
            // specified using addOnPassthroughOpacityChangedListener will be notified.
        } else {
            // The passthrough opacity preference was successfully set, but not
            // immediately visible. The passthrough opacity change will be applied
            // when the activity has the
            // SpatialCapabilities.SPATIAL_CAPABILITY_PASSTHROUGH_CONTROL capability.
            // Then, any listeners specified using addOnPassthroughOpacityChangedListener
            // will be notified.
        }
        // [END androidxr_scenecore_environment_setPassthroughOpacityPreference]
    }

    @Suppress("RestrictedApi") // b/416066566
    fun getCurrentPassthroughOpacity() {
        // [START androidxr_scenecore_environment_getCurrentPassthroughOpacity]
        val currentPassthroughOpacity = session.scene.spatialEnvironment.currentPassthroughOpacity
        // [END androidxr_scenecore_environment_getCurrentPassthroughOpacity]
    }
}
