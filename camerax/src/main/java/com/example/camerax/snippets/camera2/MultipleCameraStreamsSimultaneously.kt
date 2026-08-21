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
import android.graphics.ImageFormat
import android.graphics.Point
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.ImageReader
import android.util.Size
import android.view.Display
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
        // [START_EXCLUDE]
        /*
        // [END_EXCLUDE]
        val session: CameraCaptureSession = ...  // from CameraCaptureSession.StateCallback
        // [START_EXCLUDE]
         */
        // [END_EXCLUDE]

        // You will use the preview capture template for the combined streams
        // because it is optimized for low latency; for high-quality images, use
        // TEMPLATE_STILL_CAPTURE, and for a steady frame rate use TEMPLATE_RECORD
        val requestTemplate = CameraDevice.TEMPLATE_PREVIEW
        val combinedRequest = session.device.createCaptureRequest(requestTemplate)

        // Link the Surface targets with the combined request
        combinedRequest.addTarget(previewSurface)
        combinedRequest.addTarget(imReaderSurface)

        // In this simple case, the SurfaceView gets updated automatically. ImageReader
        // has its own callback that you have to listen to in order to retrieve the
        // frames so there is no need to set up a callback for the capture request
        session.setRepeatingRequest(combinedRequest.build(), null, null)
        // [END android_camera2_multiple_camera_streams_combined_request]
    }

    fun supportedFormats(characteristics: CameraCharacteristics) {
        // [START android_camera2_multiple_camera_streams_supported_formats]
        // [START_EXCLUDE]
        /*
        // [END_EXCLUDE]
        val characteristics: CameraCharacteristics = ...
        // [START_EXCLUDE]
         */
        // [END_EXCLUDE]
        val supportedFormats = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )?.outputFormats
        // [END android_camera2_multiple_camera_streams_supported_formats]
    }

    fun outputSizesByFormat(characteristics: CameraCharacteristics, outputFormat: Int) {
        // [START android_camera2_multiple_camera_streams_output_sizes_format]
        // [START_EXCLUDE]
        /*
        // [END_EXCLUDE]
        val characteristics: CameraCharacteristics = ...
        val outputFormat: Int = ...  // such as ImageFormat.JPEG
        // [START_EXCLUDE]
         */
        // [END_EXCLUDE]
        val sizes = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )?.getOutputSizes(outputFormat)
        // [END android_camera2_multiple_camera_streams_output_sizes_format]
    }

    fun <T> outputSizesByClass(characteristics: CameraCharacteristics, targetClass: Class<T>) {
        // [START android_camera2_multiple_camera_streams_output_sizes_class]
        // [START_EXCLUDE]
        /*
        // [END_EXCLUDE]
        val characteristics: CameraCharacteristics = ...
        val targetClass: Class <T> = ...  // such as SurfaceView::class.java
        // [START_EXCLUDE]
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
        val config = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )

        // If image format is provided, use it to determine supported sizes; or else use target class
        val allSizes = if (format == null)
            config?.getOutputSizes(targetClass) else config?.getOutputSizes(format)
        return allSizes?.maxByOrNull { it.height * it.width } ?: Size(0, 0)
    }
    // [END android_camera2_multiple_camera_streams_maximum_output_size]

    // [START android_camera2_multiple_camera_streams_preview_output_size]
    /** Helper class used to pre-compute shortest and longest sides of a [Size] */
    class SmartSize(width: Int, height: Int) {
        var size = Size(width, height)
        var long = max(size.width, size.height)
        var short = min(size.width, size.height)
        override fun toString() = "SmartSize(${long}x$short)"
    }

    /** Standard High Definition size for pictures and video */
    val SIZE_1080P: SmartSize = SmartSize(1920, 1080)

    /** Returns a [SmartSize] object for the given [Display] */
    fun getDisplaySmartSize(display: Display): SmartSize {
        val outPoint = Point()
        display.getRealSize(outPoint)
        return SmartSize(outPoint.x, outPoint.y)
    }

    /**
     * Returns the largest available PREVIEW size. For more information, see:
     * https://d.android.com/reference/android/hardware/camera2/CameraDevice
     */
    fun <T> getPreviewOutputSize(
        display: Display,
        characteristics: CameraCharacteristics,
        targetClass: Class<T>,
        format: Int? = null
    ): Size {

        // Find which is smaller: screen or 1080p
        val screenSize = getDisplaySmartSize(display)
        val hdScreen = screenSize.long >= SIZE_1080P.long || screenSize.short >= SIZE_1080P.short
        val maxSize = if (hdScreen) SIZE_1080P else screenSize

        // If image format is provided, use it to determine supported sizes; else use target class
        val config = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )!!
        if (format == null)
            assert(StreamConfigurationMap.isOutputSupportedFor(targetClass))
        else
            assert(config.isOutputSupportedFor(format))
        val allSizes = if (format == null)
            config.getOutputSizes(targetClass) else config.getOutputSizes(format)

        // Get available sizes and sort them by area from largest to smallest
        val validSizes = allSizes
            .sortedWith(compareBy { it.height * it.width })
            .map { SmartSize(it.width, it.height) }.reversed()

        // Then, get the largest output size that is smaller or equal than our max size
        return validSizes.first { it.long <= maxSize.long && it.short <= maxSize.short }.size
    }
    // [END android_camera2_multiple_camera_streams_preview_output_size]

    fun <T> getPreviewOutputSize(
        context: Context,
        characteristics: CameraCharacteristics,
        targetClass: Class<T>,
        format: Int? = null
    ): Size = getPreviewOutputSize(context.display ?: (context.getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager).defaultDisplay, characteristics, targetClass, format)

    fun checkHardwareLevel(characteristics: CameraCharacteristics) {
        // [START android_camera2_multiple_camera_streams_hardware_level]
        // [START_EXCLUDE]
        /*
        // [END_EXCLUDE]
        val characteristics: CameraCharacteristics = ...
        // [START_EXCLUDE]
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

    fun setupPreviewSizes(characteristics: CameraCharacteristics) {
        // [START android_camera2_multiple_camera_streams_get_preview_output_sizes]
        // [START_EXCLUDE]
        /*
        // [END_EXCLUDE]
        val characteristics: CameraCharacteristics = ...
        // [START_EXCLUDE]
         */
        // [END_EXCLUDE]
        val context = this as Context // assuming you are inside of an activity

        val surfaceViewSize = MultipleCameraStreamsSnippets.getPreviewOutputSize(
            context, characteristics, SurfaceView::class.java
        )
        val imageReaderSize = MultipleCameraStreamsSnippets.getPreviewOutputSize(
            context, characteristics, ImageReader::class.java, format = ImageFormat.YUV_420_888
        )
        // [END android_camera2_multiple_camera_streams_get_preview_output_sizes]

        setupSurfaceView(surfaceViewSize)
        setupImageReader(imageReaderSize)
    }

    private fun setupSurfaceView(surfaceViewSize: Size) {
        // [START android_camera2_multiple_camera_streams_surfaceview_callback]
        // [START_EXCLUDE]
        val surfaceView = SurfaceView(this)
        /*
        // [END_EXCLUDE]
        val surfaceView = findViewById <SurfaceView>(...)
        // [START_EXCLUDE]
         */
        // [END_EXCLUDE]
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // You do not need to specify image format, and it will be considered of type PRIV
                // Surface is now ready and you could use it as an output target for CameraSession
            }
            // [START_EXCLUDE]
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {}
          /*
          // [END_EXCLUDE]
          ...
          // [START_EXCLUDE]
           */
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
            // Do something with "frame" here
            it.close()
        }, null)
        // [END android_camera2_multiple_camera_streams_image_reader_listener]
    }
}
