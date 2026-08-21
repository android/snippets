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

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.os.AsyncTask
import android.view.Surface
import java.util.concurrent.Executor

private object MultiCameraSnippets {

    // [START android_camera2_multi_camera_find_dual_cameras]
    /**
     * Helper class used to encapsulate a logical camera and two underlying
     * physical cameras
     */
    data class DualCamera(val logicalId: String, val physicalId1: String, val physicalId2: String)

    fun findDualCameras(manager: CameraManager, facing: Int? = null): List<DualCamera> {
        val dualCameras = mutableListOf<DualCamera>()

        // Iterate over all the available camera characteristics
        manager.cameraIdList.map {
            Pair(manager.getCameraCharacteristics(it), it)
        }.filter {
            // Filter by cameras facing the requested direction
            facing == null || it.first.get(CameraCharacteristics.LENS_FACING) == facing
        }.filter {
            // Filter by logical cameras
            // CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA requires API >= 28
            it.first.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)!!.contains(
                CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA
            )
        }.forEach {
            // All possible pairs from the list of physical cameras are valid results
            // NOTE: There could be N physical cameras as part of a logical camera grouping
            // getPhysicalCameraIds() requires API >= 28
            val physicalCameras = it.first.physicalCameraIds.toTypedArray()
            for (idx1 in 0 until physicalCameras.size) {
                for (idx2 in (idx1 + 1) until physicalCameras.size) {
                    dualCameras.add(
                        DualCamera(
                            it.second, physicalCameras[idx1], physicalCameras[idx2]
                        )
                    )
                }
            }
        }

        return dualCameras
    }
    // [END android_camera2_multi_camera_find_dual_cameras]

    // [START android_camera2_multi_camera_open_dual_camera]
    fun openDualCamera(
        cameraManager: CameraManager,
        dualCamera: DualCamera,
        // AsyncTask is deprecated beginning API 30
        executor: Executor = AsyncTask.SERIAL_EXECUTOR,
        callback: (CameraDevice) -> Unit
    ) {

        // openCamera() requires API >= 28
        cameraManager.openCamera(
            dualCamera.logicalId, executor,
            object : CameraDevice.StateCallback() {
                override fun onOpened(device: CameraDevice) = callback(device)
                // Omitting for brevity...
                override fun onError(device: CameraDevice, error: Int) = onDisconnected(device)
                override fun onDisconnected(device: CameraDevice) = device.close()
            }
        )
    }
    // [END android_camera2_multi_camera_open_dual_camera]

    // [START android_camera2_multi_camera_create_dual_camera_session]
    /**
     * Helper type definition that encapsulates 3 sets of output targets:
     *
     *   1. Logical camera
     *   2. First physical camera
     *   3. Second physical camera
     */
    typealias DualCameraOutputs =
        Triple<MutableList<Surface>?, MutableList<Surface>?, MutableList<Surface>?>

    fun createDualCameraSession(
        cameraManager: CameraManager,
        dualCamera: DualCamera,
        targets: DualCameraOutputs,
        // AsyncTask is deprecated beginning API 30
        executor: Executor = AsyncTask.SERIAL_EXECUTOR,
        callback: (CameraCaptureSession) -> Unit
    ) {

        // Create 3 sets of output configurations: one for the logical camera, and
        // one for each of the physical cameras.
        val outputConfigsLogical = targets.first?.map { OutputConfiguration(it) }
        val outputConfigsPhysical1 = targets.second?.map {
            OutputConfiguration(it).apply { setPhysicalCameraId(dualCamera.physicalId1) }
        }
        val outputConfigsPhysical2 = targets.third?.map {
            OutputConfiguration(it).apply { setPhysicalCameraId(dualCamera.physicalId2) }
        }

        // Put all the output configurations into a single flat array
        val outputConfigsAll = arrayOf(
            outputConfigsLogical, outputConfigsPhysical1, outputConfigsPhysical2
        )
            .filterNotNull().flatMap { it }

        // Instantiate a session configuration that can be used to create a session
        val sessionConfiguration = SessionConfiguration(
            SessionConfiguration.SESSION_REGULAR,
            outputConfigsAll, executor,
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) = callback(session)
                // Omitting for brevity...
                override fun onConfigureFailed(session: CameraCaptureSession) = session.device.close()
            }
        )

        // Open the logical camera using the previously defined function
        openDualCamera(cameraManager, dualCamera, executor = executor) {

            // Finally create the session and return via callback
            it.createCaptureSession(sessionConfiguration)
        }
    }
    // [END android_camera2_multi_camera_create_dual_camera_session]

    // [START android_camera2_multi_camera_find_short_long_camera_pair]
    fun findShortLongCameraPair(manager: CameraManager, facing: Int? = null): DualCamera? {

        return findDualCameras(manager, facing).map {
            val characteristics1 = manager.getCameraCharacteristics(it.physicalId1)
            val characteristics2 = manager.getCameraCharacteristics(it.physicalId2)

            // Query the focal lengths advertised by each physical camera
            val focalLengths1 = characteristics1.get(
                CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS
            ) ?: floatArrayOf(0F)
            val focalLengths2 = characteristics2.get(
                CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS
            ) ?: floatArrayOf(0F)

            // Compute the largest difference between min and max focal lengths between cameras
            val focalLengthsDiff1 = focalLengths2.maxOrNull()!! - focalLengths1.minOrNull()!!
            val focalLengthsDiff2 = focalLengths1.maxOrNull()!! - focalLengths2.minOrNull()!!

            // Return the pair of camera IDs and the difference between min and max focal lengths
            if (focalLengthsDiff1 < focalLengthsDiff2) {
                Pair(DualCamera(it.logicalId, it.physicalId1, it.physicalId2), focalLengthsDiff1)
            } else {
                Pair(DualCamera(it.logicalId, it.physicalId2, it.physicalId1), focalLengthsDiff2)
            }

            // Return only the pair with the largest difference, or null if no pairs are found
        }.maxByOrNull { it.second }?.first
    }
    // [END android_camera2_multi_camera_find_short_long_camera_pair]

    fun zoomExample(
        manager: CameraManager,
        surface1: Surface,
        surface2: Surface
    ) {
        // [START android_camera2_multi_camera_zoom_example]
        // [START_EXCLUDE]
        /*
        // [END_EXCLUDE]
        val cameraManager: CameraManager = ...

        // Get the two output targets from the activity / fragment
        val surface1 = ...  // from SurfaceView
        val surface2 = ...  // from SurfaceView
        // [START_EXCLUDE]
         */
        // [END_EXCLUDE]

        val dualCamera = findShortLongCameraPair(manager)!!
        val outputTargets = DualCameraOutputs(
            null, mutableListOf(surface1), mutableListOf(surface2)
        )

        // Here you open the logical camera, configure the outputs and create a session
        createDualCameraSession(manager, dualCamera, targets = outputTargets) { session ->

            // Create a single request which has one target for each physical camera
            // NOTE: Each target receive frames from only its associated physical camera
            val requestTemplate = CameraDevice.TEMPLATE_PREVIEW
            val captureRequest = session.device.createCaptureRequest(requestTemplate).apply {
                arrayOf(surface1, surface2).forEach { addTarget(it) }
            }.build()

            // Set the sticky request for the session and you are done
            session.setRepeatingRequest(captureRequest, null, null)
        }
        // [END android_camera2_multi_camera_zoom_example]
    }

    fun distortionCorrection(
        cameraSession: CameraCaptureSession,
        characteristics: CameraCharacteristics,
        captureCallback: CameraCaptureSession.CaptureCallback? = null
    ) {
        // [START android_camera2_multi_camera_distortion_correction]
        // [START_EXCLUDE]
        /*
        // [END_EXCLUDE]
        val cameraSession: CameraCaptureSession = ...
        // [START_EXCLUDE]
         */
        // [END_EXCLUDE]

        // Use still capture template to build the capture request
        val captureRequest = cameraSession.device.createCaptureRequest(
            CameraDevice.TEMPLATE_STILL_CAPTURE
        )

        // Determine if this device supports distortion correction
        // [START_EXCLUDE]
        /*
        // [END_EXCLUDE]
        val characteristics: CameraCharacteristics = ...
        // [START_EXCLUDE]
         */
        // [END_EXCLUDE]
        val supportsDistortionCorrection = characteristics.get(
            CameraCharacteristics.DISTORTION_CORRECTION_AVAILABLE_MODES
        )?.contains(
            CameraMetadata.DISTORTION_CORRECTION_MODE_HIGH_QUALITY
        ) ?: false

        if (supportsDistortionCorrection) {
            captureRequest.set(
                CaptureRequest.DISTORTION_CORRECTION_MODE,
                CameraMetadata.DISTORTION_CORRECTION_MODE_HIGH_QUALITY
            )
        }

        // Add output target, set other capture request parameters...

        // Dispatch the capture request
        // [START_EXCLUDE]
        cameraSession.capture(captureRequest.build(), captureCallback, null)
        /*
        // [END_EXCLUDE]
        cameraSession.capture(captureRequest.build(), ...)
        // [START_EXCLUDE]
         */
        // [END_EXCLUDE]
        // [END android_camera2_multi_camera_distortion_correction]
    }
}
