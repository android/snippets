package com.example.xr.scenecore

import androidx.xr.scenecore.Session
import androidx.xr.scenecore.SpatialCapabilities
import androidx.xr.scenecore.getSpatialCapabilities

fun checkMultipleCapabilities(xrSession: Session) {
    // [START androidxr_compose_checkMultipleCapabilities]
    // Example 1: check if enabling passthrough mode is allowed
    if (xrSession.getSpatialCapabilities().hasCapability(
            SpatialCapabilities.SPATIAL_CAPABILITY_PASSTHROUGH_CONTROL)) {
        xrSession.spatialEnvironment.setPassthroughOpacityPreference(0f)
    }
    // Example 2: multiple capability flags can be checked simultaneously:
    if (xrSession.getSpatialCapabilities().hasCapability(
            SpatialCapabilities.SPATIAL_CAPABILITY_PASSTHROUGH_CONTROL and
                    SpatialCapabilities.SPATIAL_CAPABILITY_3D_CONTENT)) {
        // ...
    }
    // [END androidxr_compose_checkMultipleCapabilities]
}