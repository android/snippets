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

import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.media.ImageReader
import android.util.Size
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.max
import kotlin.math.min

private object MultipleCameraStreamsSnippets {

    fun combinedRequest(
        session: CameraCaptureSession,
        previewSurface: Surface,
        imReaderSurface: Surface
    ) {
        // [START android_camera2_multiple_camera_streams_combined_request]
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        val session: CameraCaptureSession = ...  // from CameraCaptureSession.StateCallback
        // [START_EXCLUDE silent]
         */
        // [END_EXCLUDE]

        // You will use the preview capture template for the combined streams
        // because it is optimized for low latency; for high-quality images, use
        // TEMPLATE_STILL_CAPTURE, and for a steady frame rate use TEMPLATE_RECORD.
        val requestTemplate = CameraDevice.TEMPLATE_PREVIEW
        val combinedRequest = session.device.createCaptureRequest(requestTemplate)

        // Link the Surface targets with the combined request.
        combinedRequest.addTarget(previewSurface)
        combinedRequest.addTarget(imReaderSurface)

        // In this simple case, the SurfaceView gets updated automatically. ImageReader
        // has its own callback that you have to listen to in order to retrieve the
        // frames so there is no need to set up a callback for the capture request.
        session.setRepeatingRequest(combinedRequest.build(), null, null)
        // [END android_camera2_multiple_camera_streams_combined_request]
    }

    fun supportedFormats(characteristics: CameraCharacteristics) {
        // [START android_camera2_multiple_camera_streams_supported_formats]
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        val characteristics: CameraCharacteristics = ...
        // [START_EXCLUDE silent]
         */
        // [END_EXCLUDE]
        val supportedFormats = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )?.outputFormats
        // [END android_camera2_multiple_camera_streams_supported_formats]
    }

    fun outputSizesByFormat(characteristics: CameraCharacteristics, outputFormat: Int) {
        // [START android_camera2_multiple_camera_streams_output_sizes_format]
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        val characteristics: CameraCharacteristics = ...
        val outputFormat: Int = ...  // such as ImageFormat.JPEG
        // [START_EXCLUDE silent]
         */
        // [END_EXCLUDE]
        val sizes = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )?.getOutputSizes(outputFormat)
        // [END android_camera2_multiple_camera_streams_output_sizes_format]
    }

    fun <T> outputSizesByClass(characteristics: CameraCharacteristics, targetClass: Class<T>) {
        // [START android_camera2_multiple_camera_streams_output_sizes_class]
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        val characteristics: CameraCharacteristics = ...
        val targetClass: Class <T> = ...  // such as SurfaceView::class.java
        // [START_EXCLUDE silent]
         */
        // [END_EXCLUDE]
        val sizes = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )?.getOutputSizes(targetClass)
        // [END android_camera2_multiple_camera_streams_output_sizes_class]
    }

    // [START android_camera2_multiple_camera_streams_maximum_output_size]
    fun <T> getMaximumOutputSize(
        characteristics: CameraCharacteristics,
        targetClass: Class<T>,
        format: Int? = null
    ): Size {
        val config = checkNotNull(
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        )

        // If image format is provided, use it to determine supported sizes; or else use target class.
        val allSizes = if (format == null)
            config.getOutputSizes(targetClass) else config.getOutputSizes(format)
        return allSizes.maxBy { it.height * it.width }
    }
    // [END android_camera2_multiple_camera_streams_maximum_output_size]

    /**
     * Returns the largest available output size that fits the screen, capped at 1080p.
     * Supports the get_preview_output_sizes snippet. The page's own getPreviewOutputSize()
     * sample stays on the guide page because it uses the deprecated Display.getRealSize().
     */
    fun <T> getPreviewOutputSize(
        context: Context,
        characteristics: CameraCharacteristics,
        targetClass: Class<T>,
        format: Int? = null
    ): Size {
        val metrics = context.resources.displayMetrics
        val screenLong = max(metrics.widthPixels, metrics.heightPixels)
        val screenShort = min(metrics.widthPixels, metrics.heightPixels)
        val hdScreen = screenLong >= 1920 || screenShort >= 1080
        val maxLong = if (hdScreen) 1920 else screenLong
        val maxShort = if (hdScreen) 1080 else screenShort

        val config = checkNotNull(
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        )
        val allSizes = if (format == null)
            config.getOutputSizes(targetClass) else config.getOutputSizes(format)
        return allSizes
            .sortedByDescending { it.height * it.width }
            .first { max(it.width, it.height) <= maxLong && min(it.width, it.height) <= maxShort }
    }

    fun checkHardwareLevel(characteristics: CameraCharacteristics) {
        // [START android_camera2_multiple_camera_streams_hardware_level]
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        val characteristics: CameraCharacteristics = ...
        // [START_EXCLUDE silent]
         */
        // [END_EXCLUDE]

        // Hardware level will be one of:
        // - CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
        // - CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL,
        // - CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
        // - CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
        // - CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
        val hardwareLevel = characteristics.get(
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL
        )
        // [END android_camera2_multiple_camera_streams_hardware_level]
    }
}

private class MultipleStreamsActivity : Activity() {

    private fun <T> getPreviewOutputSize(
        context: Context,
        characteristics: CameraCharacteristics,
        targetClass: Class<T>,
        format: Int? = null
    ): Size =
        MultipleCameraStreamsSnippets.getPreviewOutputSize(
            context,
            characteristics,
            targetClass,
            format
        )

    fun setupPreviewSizes(characteristics: CameraCharacteristics) {
        // [START android_camera2_multiple_camera_streams_get_preview_output_sizes]
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        val characteristics: CameraCharacteristics = ...
        // [START_EXCLUDE silent]
         */
        // [END_EXCLUDE]
        val context = this as Context // assuming you are inside of an activity

        val surfaceViewSize = getPreviewOutputSize(
            context, characteristics, SurfaceView::class.java
        )
        val imageReaderSize = getPreviewOutputSize(
            context, characteristics, ImageReader::class.java, format = ImageFormat.YUV_420_888
        )
        // [END android_camera2_multiple_camera_streams_get_preview_output_sizes]

        setupSurfaceView(surfaceViewSize)
        setupImageReader(imageReaderSize)
    }

    private fun setupSurfaceView(surfaceViewSize: Size) {
        // [START android_camera2_multiple_camera_streams_surfaceview_callback]
        // [START_EXCLUDE silent]
        val surfaceView = SurfaceView(this)
        /*
        // [END_EXCLUDE]
        val surfaceView = findViewById <SurfaceView>(...)
        // [START_EXCLUDE silent]
         */
        // [END_EXCLUDE]
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // You do not need to specify image format, and it will be considered of type PRIV.
                // Surface is now ready and you could use it as an output target for CameraSession.
            }
            // [START_EXCLUDE]
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {}
            // [END_EXCLUDE]
        })
        // [END android_camera2_multiple_camera_streams_surfaceview_callback]
    }

    private fun setupImageReader(imageReaderSize: Size) {
        // [START android_camera2_multiple_camera_streams_image_reader_instance]
        val frameBufferCount = 3 // just an example, depends on your usage of ImageReader
        val imageReader = ImageReader.newInstance(
            imageReaderSize.width, imageReaderSize.height, ImageFormat.YUV_420_888,
            frameBufferCount
        )
        // [END android_camera2_multiple_camera_streams_image_reader_instance]

        // [START android_camera2_multiple_camera_streams_image_reader_listener]
        imageReader.setOnImageAvailableListener({
            val frame = it.acquireNextImage()
            // Do something with "frame" here.
            it.close()
        }, null)
        // [END android_camera2_multiple_camera_streams_image_reader_listener]
    }
}
