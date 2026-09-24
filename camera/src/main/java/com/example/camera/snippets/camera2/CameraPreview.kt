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

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.view.SurfaceView
import kotlin.math.min

private class AutoFitSurfaceView(
    context: Context,
    private val characteristics: CameraCharacteristics,
    private val surfaceRotationDegrees: Int,
    private val previewWidth: Float,
    private val previewHeight: Float
) : SurfaceView(context) {

    private fun computeRelativeRotation(
        characteristics: CameraCharacteristics,
        surfaceRotationDegrees: Int
    ): Int = CameraPreviewSnippets.computeRelativeRotation(characteristics, surfaceRotationDegrees)

    // [START android_camera2_camera_preview_surfaceview_onmeasure]
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val relativeRotation = computeRelativeRotation(characteristics, surfaceRotationDegrees)

        if (previewWidth > 0f && previewHeight > 0f) {
            /* Scale factor required to scale the preview to its original size on the x-axis. */
            val scaleX =
                if (relativeRotation % 180 == 0) {
                    width.toFloat() / previewWidth
                } else {
                    width.toFloat() / previewHeight
                }
            /* Scale factor required to scale the preview to its original size on the y-axis. */
            val scaleY =
                if (relativeRotation % 180 == 0) {
                    height.toFloat() / previewHeight
                } else {
                    height.toFloat() / previewWidth
                }

            /* Scale factor required to fit the preview to the SurfaceView size. */
            val finalScale = min(scaleX, scaleY)

            setScaleX(1 / scaleX * finalScale)
            setScaleY(1 / scaleY * finalScale)
        }
        setMeasuredDimension(width, height)
    }
    // [END android_camera2_camera_preview_surfaceview_onmeasure]
}

private object CameraPreviewSnippets {
    // [START android_camera2_camera_preview_compute_relative_rotation]
    /**
     * Computes rotation required to transform the camera sensor output orientation to the
     * device's current orientation in degrees.
     *
     * @param characteristics The CameraCharacteristics to query for the sensor orientation.
     * @param surfaceRotationDegrees The current device orientation as a Surface constant.
     * @return Relative rotation of the camera sensor output.
     */
    public fun computeRelativeRotation(
        characteristics: CameraCharacteristics,
        surfaceRotationDegrees: Int
    ): Int {
        val sensorOrientationDegrees =
            characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!

        // Reverse device orientation for back-facing cameras.
        val sign = if (characteristics.get(CameraCharacteristics.LENS_FACING) ==
            CameraCharacteristics.LENS_FACING_FRONT
        ) 1 else -1

        // Calculate desired orientation relative to camera orientation to make
        // the image upright relative to the device orientation.
        return (sensorOrientationDegrees - surfaceRotationDegrees * sign + 360) % 360
    }
    // [END android_camera2_camera_preview_compute_relative_rotation]
}
