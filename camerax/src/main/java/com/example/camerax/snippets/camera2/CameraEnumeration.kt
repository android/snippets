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

package com.example.camerax.snippets.camera2

import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata

private object CameraEnumerationSnippets {

    fun iterateCameras(activity: Activity) {
        // [START android_camera2_camera_enumeration_iterate_cameras]
        val cameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIdList = cameraManager.cameraIdList
        for (cameraId in cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (cameraDirection != null &&
                cameraDirection == CameraCharacteristics.LENS_FACING_BACK
            ) {
                continue
            }
            val cameraCapabilities = characteristics.get(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
            )
            val isBackwardCompatible = cameraCapabilities?.contains(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
            ) ?: false
            if (isBackwardCompatible) {
                // ...
            }
        }
        // [END android_camera2_camera_enumeration_iterate_cameras]
    }

    // [START android_camera2_camera_enumeration_get_first_camera_id_facing]
    fun getFirstCameraIdFacing(
        cameraManager: CameraManager,
        facing: Int = CameraMetadata.LENS_FACING_BACK
    ): String? {
        val cameraIds = cameraManager.cameraIdList
        cameraIds.forEach { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val hasCapability = characteristics.get(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
            )?.contains(
                CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
            ) ?: false
            val cameraFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (hasCapability && (cameraFacing == facing)) {
                return id
            }
        }
        return null
    }
    // [END android_camera2_camera_enumeration_get_first_camera_id_facing]

    // [START android_camera2_camera_enumeration_filter_cameras]
    fun filterCompatibleCameras(
        cameraManager: CameraManager,
        cameraIds: Array<String>
    ): List<String> {
        return cameraIds.filter { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            characteristics.get(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES
            )?.contains(
                CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
            ) ?: false
        }
    }

    fun filterCameraIdsFacing(
        cameraManager: CameraManager,
        cameraIds: List<String>,
        facing: Int
    ): List<String> {
        return cameraIds.filter { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            characteristics.get(CameraCharacteristics.LENS_FACING) == facing
        }
    }

    fun getNextCameraId(
        cameraManager: CameraManager,
        currCameraId: String? = null,
        facing: Int? = null
    ): String? {
        // Return all compatible cameras
        val cameraIds = filterCompatibleCameras(cameraManager, cameraManager.cameraIdList)

        // Return cameras that face the same direction, if specified
        val cameraFacingIds = facing?.let {
            filterCameraIdsFacing(cameraManager, cameraIds, it)
        } ?: cameraIds

        // Return the next camera in the list
        if (cameraFacingIds.isEmpty()) return null
        return currCameraId?.let { id ->
            val currIndex = cameraFacingIds.indexOf(id)
            if (currIndex >= 0) {
                cameraFacingIds[(currIndex + 1) % cameraFacingIds.size]
            } else {
                cameraFacingIds[0]
            }
        } ?: cameraFacingIds[0]
    }
    // [END android_camera2_camera_enumeration_filter_cameras]
}
