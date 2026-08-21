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
import android.hardware.camera2.TotalCaptureResult
import android.hardware.camera2.params.DynamicRangeProfiles
import android.hardware.camera2.params.OutputConfiguration
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Handler
import android.view.Surface
import androidx.annotation.RequiresApi

private class HdrVideoCaptureSnippets(
    private val cameraManager: CameraManager
) {

    // [START android_camera2_hdr_video_capture_is_ten_bit_profile_supported]
    private fun isTenBitProfileSupported(cameraId: String): Boolean {
        val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
        val availableCapabilities = cameraCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
        for (capability in availableCapabilities!!) {
            if (capability == CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT) {
                return true
            }
        }
        return false
    }
    // [END android_camera2_hdr_video_capture_is_ten_bit_profile_supported]

    // [START android_camera2_hdr_video_capture_is_hlg_supported]
    @RequiresApi(api = 33)
    private fun isHLGSupported(cameraId: String): Boolean {
        if (isTenBitProfileSupported(cameraId)) {
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
            val availableProfiles = cameraCharacteristics
                .get(CameraCharacteristics.REQUEST_AVAILABLE_DYNAMIC_RANGE_PROFILES)!!
                .getSupportedProfiles()

            // Checks for the desired profile, in this case HLG10
            return availableProfiles.contains(DynamicRangeProfiles.HLG10)
        }
        return false
    }
    // [END android_camera2_hdr_video_capture_is_hlg_supported]

    // [START android_camera2_hdr_video_capture_setup_session]
    /**
     * Creates a [CameraCaptureSession] with a dynamic range profile.
     */
    private fun setupSessionWithDynamicRangeProfile(
        dynamicRange: Long,
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler? = null,
        stateCallback: CameraCaptureSession.StateCallback
    ): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val outputConfigs = mutableListOf<OutputConfiguration>()
            for (target in targets) {
                val outputConfig = OutputConfiguration(target)
                // sets the dynamic range profile, for example DynamicRangeProfiles.HLG10
                outputConfig.setDynamicRangeProfile(dynamicRange)
                outputConfigs.add(outputConfig)
            }
            device.createCaptureSessionByOutputConfigurations(
                outputConfigs, stateCallback, handler
            )
            return true
        } else {
            device.createCaptureSession(targets, stateCallback, handler)
            return false
        }
    }
    // [END android_camera2_hdr_video_capture_setup_session]

    fun previewRequestSample(
        session: CameraCaptureSession,
        previewRequest: CaptureRequest,
        cameraHandler: Handler
    ) {
        // [START android_camera2_hdr_video_capture_preview_request]
        session.setRepeatingRequest(previewRequest, null, cameraHandler)
        // [END android_camera2_hdr_video_capture_preview_request]
    }

    interface VideoEncoder {
        fun frameAvailable()
    }

    fun recordRequestSample(
        session: CameraCaptureSession,
        recordRequest: CaptureRequest,
        cameraHandler: Handler,
        currentlyRecording: Boolean,
        encoder: VideoEncoder
    ) {
        // [START android_camera2_hdr_video_capture_record_request]
        // Start recording repeating requests, which stops the ongoing preview
        //  repeating requests without having to explicitly call
        //  `session.stopRepeating`
        session.setRepeatingRequest(
            recordRequest,
            object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    if (currentlyRecording) {
                        encoder.frameAvailable()
                    }
                }
            },
            cameraHandler
        )
        // [END android_camera2_hdr_video_capture_record_request]
    }

    @RequiresApi(33)
    fun encodeHdrStream(
        dynamicRange: Long,
        width: Int,
        height: Int,
        bitRate: Int,
        frameRate: Int,
        IFRAME_INTERVAL: Int,
        mediaCodec: MediaCodec
    ) {
        // [START android_camera2_hdr_video_capture_encode_hdr_stream]
        val mimeType = when {
            dynamicRange == DynamicRangeProfiles.STANDARD -> MediaFormat.MIMETYPE_VIDEO_AVC
            dynamicRange < DynamicRangeProfiles.PUBLIC_MAX ->
                MediaFormat.MIMETYPE_VIDEO_HEVC
            else -> throw IllegalArgumentException("Unknown dynamic range format")
        }

        val codecProfile = when {
            dynamicRange == DynamicRangeProfiles.HLG10 ->
                MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10
            dynamicRange == DynamicRangeProfiles.HDR10 ->
                MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10
            dynamicRange == DynamicRangeProfiles.HDR10_PLUS ->
                MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10Plus
            else -> -1
        }
        // Failing to correctly set color transfer causes quality issues
        // for example, washout and color clipping
        val transferFunction = when (codecProfile) {
            MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10 ->
                MediaFormat.COLOR_TRANSFER_HLG
            MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10 ->
                MediaFormat.COLOR_TRANSFER_ST2084
            MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10Plus ->
                MediaFormat.COLOR_TRANSFER_ST2084
            else -> MediaFormat.COLOR_TRANSFER_SDR_VIDEO
        }

        val format = MediaFormat.createVideoFormat(mimeType, width, height)

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an exception.
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)

        if (codecProfile != -1) {
            format.setInteger(MediaFormat.KEY_PROFILE, codecProfile)
            format.setInteger(
                MediaFormat.KEY_COLOR_STANDARD,
                MediaFormat.COLOR_STANDARD_BT2020
            )
            format.setInteger(MediaFormat.KEY_COLOR_RANGE, MediaFormat.COLOR_RANGE_LIMITED)
            format.setInteger(MediaFormat.KEY_COLOR_TRANSFER, transferFunction)
            format.setFeatureEnabled(
                MediaCodecInfo.CodecCapabilities.FEATURE_HdrEditing,
                true
            )
        }

        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        // [END android_camera2_hdr_video_capture_encode_hdr_stream]
    }
}
