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

package com.example.camera.snippets.camera2

import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.util.Log

private const val TAG = "CameraEnumeration"

private object CameraEnumerationSnippets {

    fun iterateCameras(cameraManager: CameraManager) {
        // [START android_camera2_camera_enumeration_iterate_cameras]
        try {
            val cameraIdList = cameraManager.cameraIdList // may be empty

            // iterate over available camera devices
            for (cameraId in cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val cameraLensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
                val cameraCapabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)

                // check if the selected camera device supports basic features
                // ensures backward compatibility with the original Camera API
                val isBackwardCompatible = cameraCapabilities?.contains(
                    CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
                ) ?: false
                // ...
            }
        } catch (e: CameraAccessException) {
            e.message?.let { Log.e(TAG, it) }
            // ...
        }
        // [END android_camera2_camera_enumeration_iterate_cameras]
    }

    // [START android_camera2_camera_enumeration_get_first_camera_id_facing]
    fun getFirstCameraIdFacing(
        cameraManager: CameraManager,
        facing: Int = CameraMetadata.LENS_FACING_BACK
    ): String? {
        try {
            // Get list of all compatible cameras
            val cameraIds = cameraManager.cameraIdList.filter {
                val characteristics = cameraManager.getCameraCharacteristics(it)
                val capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
                capabilities?.contains(
                    CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
                ) ?: false
            }

            // Iterate over the list of cameras and return the first one matching desired
            // lens-facing configuration
            cameraIds.forEach {
                val characteristics = cameraManager.getCameraCharacteristics(it)
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == facing) {
                    return it
                }
            }

            // If no camera matched desired orientation, return the first one from the list
            return cameraIds.firstOrNull()
        } catch (e: CameraAccessException) {
            e.message?.let { Log.e(TAG, it) }
            return null
        }
    }
    // [END android_camera2_camera_enumeration_get_first_camera_id_facing]

    // [START android_camera2_camera_enumeration_filter_cameras]
    fun filterCompatibleCameras(
        cameraIds: Array<String>,
        cameraManager: CameraManager
    ): List<String> {
        return cameraIds.filter {
            val characteristics = cameraManager.getCameraCharacteristics(it)
            characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)?.contains(
                CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
            ) ?: false
        }
    }

    fun filterCameraIdsFacing(
        cameraIds: List<String>,
        cameraManager: CameraManager,
        facing: Int
    ): List<String> {
        return cameraIds.filter {
            val characteristics = cameraManager.getCameraCharacteristics(it)
            characteristics.get(CameraCharacteristics.LENS_FACING) == facing
        }
    }

    fun getNextCameraId(cameraManager: CameraManager, currCameraId: String? = null): String? {
        // Get all front, back and external cameras in 3 separate lists
        val cameraIds = filterCompatibleCameras(cameraManager.cameraIdList, cameraManager)
        val backCameras = filterCameraIdsFacing(
            cameraIds, cameraManager, CameraMetadata.LENS_FACING_BACK
        )
        val frontCameras = filterCameraIdsFacing(
            cameraIds, cameraManager, CameraMetadata.LENS_FACING_FRONT
        )
        val externalCameras = filterCameraIdsFacing(
            cameraIds, cameraManager, CameraMetadata.LENS_FACING_EXTERNAL
        )

        // The recommended order of iteration is: all external, first back, first front
        val allCameras = (
            externalCameras + listOf(
                backCameras.firstOrNull(), frontCameras.firstOrNull()
            )
            ).filterNotNull()

        // Get the index of the currently selected camera in the list
        val cameraIndex = allCameras.indexOf(currCameraId)

        // The selected camera may not be in the list, for example it could be an
        // external camera that has been removed by the user
        return if (cameraIndex == -1) {
            // Return the first camera from the list
            allCameras.getOrNull(0)
        } else {
            // Return the next camera from the list, wrap around if necessary
            allCameras.getOrNull((cameraIndex + 1) % allCameras.size)
        }
    }
    // [END android_camera2_camera_enumeration_filter_cameras]
}
