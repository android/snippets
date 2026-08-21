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

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.util.Size
import android.view.Surface
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlin.math.roundToInt

// [START_EXCLUDE]
interface CameraViewfinder {
    fun requestSurfaceAsync(request: ViewfinderSurfaceRequest): ListenableFuture<Surface>
}

class ViewfinderSurfaceRequest(
    val resolution: Size,
    val characteristics: CameraCharacteristics
)
// [END_EXCLUDE]

private class CameraPreviewHelper(
    private val width: Int,
    private val height: Int,
    private val characteristics: CameraCharacteristics,
    private val cameraViewfinder: CameraViewfinder,
    private val context: Context
) {

    // [START android_camera2_camera_preview_viewfinder_request_surface]
    fun startCamera() {
        val previewResolution = Size(width, height)
        val viewfinderSurfaceRequest =
            ViewfinderSurfaceRequest(previewResolution, characteristics)
        val surfaceListenableFuture =
            cameraViewfinder.requestSurfaceAsync(viewfinderSurfaceRequest)

        Futures.addCallback(
            surfaceListenableFuture,
            object : FutureCallback<Surface> {
                override fun onSuccess(surface: Surface) {
                    /* create a CaptureSession using this surface as usual */
                }
                override fun onFailure(t: Throwable) { /* something went wrong */ }
            },
            ContextCompat.getMainExecutor(context)
        )
    }
    // [END android_camera2_camera_preview_viewfinder_request_surface]
}

private class AutoFitSurfaceView(context: Context) : SurfaceView(context) {
    // [START android_camera2_camera_preview_surfaceview_onmeasure]
    private var aspectRatio = 4f / 3f

    fun setAspectRatio(width: Int, height: Int) {
        aspectRatio = width.toFloat() / height.toFloat()
        holder.setFixedSize(width, height)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (width == 0 || height == 0) return

        if (width > height * aspectRatio) {
            setMeasuredDimension(width, (width / aspectRatio).roundToInt())
        } else {
            setMeasuredDimension((height * aspectRatio).roundToInt(), height)
        }
    }
    // [END android_camera2_camera_preview_surfaceview_onmeasure]
}

private object CameraPreviewSnippets {
    // [START android_camera2_camera_preview_compute_relative_rotation]
    fun computeRelativeRotation(
        characteristics: CameraCharacteristics,
        surfaceRotationDegrees: Int
    ): Int {
        val sensorOrientationDegrees =
            characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!

        val sign = if (characteristics.get(CameraCharacteristics.LENS_FACING) ==
            CameraCharacteristics.LENS_FACING_FRONT
        ) 1 else -1

        // Reverse device orientation for front-facing cameras
        return (sensorOrientationDegrees - (surfaceRotationDegrees * sign) + 360) % 360
    }
    // [END android_camera2_camera_preview_compute_relative_rotation]
}
