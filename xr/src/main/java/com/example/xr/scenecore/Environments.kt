package com.example.xr.scenecore

import androidx.xr.scenecore.ExrImage
import androidx.xr.scenecore.GltfModel
import androidx.xr.scenecore.Session
import androidx.xr.scenecore.SpatialEnvironment
import kotlinx.coroutines.guava.await

private class Environments(val session: Session) {
    suspend fun loadEnvironmentGeometry() {
        // [START androidxr_scenecore_environment_loadEnvironmentGeometry]
        val environmentGeometryFuture = GltfModel.create(session, "DayGeometry.glb")
        val environmentGeometry = environmentGeometryFuture.await()
        // [END androidxr_scenecore_environment_loadEnvironmentGeometry]
    }

    fun loadEnvironmentSkybox() {
        // [START androidxr_scenecore_environment_loadEnvironmentSkybox]
        val skybox = ExrImage.create(session, "BlueSkybox.exr")
        // [END androidxr_scenecore_environment_loadEnvironmentSkybox]
    }

    fun setEnvironmentPreference(environmentGeometry: GltfModel, skybox: ExrImage) {
        // [START androidxr_scenecore_environment_setEnvironmentPreference]
        val spatialEnvironmentPreference =
            SpatialEnvironment.SpatialEnvironmentPreference(skybox, environmentGeometry)
        val preferenceResult =
            session.spatialEnvironment.setSpatialEnvironmentPreference(spatialEnvironmentPreference)
        if (preferenceResult == SpatialEnvironment.SetSpatialEnvironmentPreferenceChangeApplied()) {
            // The environment was successfully updated and is now visible, and any listeners
            // specified using addOnSpatialEnvironmentChangedListener will be notified.
        } else if (preferenceResult == SpatialEnvironment.SetSpatialEnvironmentPreferenceChangePending()) {
            // The environment is in the process of being updated. Once visible, any listeners
            // specified using addOnSpatialEnvironmentChangedListener will be notified.
        }
        // [END androidxr_scenecore_environment_setEnvironmentPreference]
    }

    fun setPassthroughOpacityPreference() {
        // [START androidxr_scenecore_environment_setPassthroughOpacityPreference]
        val preferenceResult = session.spatialEnvironment.setPassthroughOpacityPreference(1.0f)
        if (preferenceResult == SpatialEnvironment.SetPassthroughOpacityPreferenceChangeApplied()) {
            // The passthrough opacity request succeeded and should be visible now, and any listeners
            // specified using addOnPassthroughOpacityChangedListener will be notified
        } else if (preferenceResult == SpatialEnvironment.SetPassthroughOpacityPreferenceChangePending()) {
            // The passthrough opacity preference was successfully set, but not
            // immediately visible. The passthrough opacity change will be applied
            // when the activity has the
            // SpatialCapabilities.SPATIAL_CAPABILITY_PASSTHROUGH_CONTROL capability.
            // Then, any listeners specified using addOnPassthroughOpacityChangedListener
            // will be notified
        }
        // [END androidxr_scenecore_environment_setPassthroughOpacityPreference]
    }

    fun getCurrentPassthroughOpacity() {
        // [START androidxr_scenecore_environment_getCurrentPassthroughOpacity]
        val currentPassthroughOpacity = session.spatialEnvironment.getCurrentPassthroughOpacity()
        // [END androidxr_scenecore_environment_getCurrentPassthroughOpacity]
    }
}

