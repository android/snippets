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

package com.example.media

import android.media.MediaCodec
// [START android_media_platform_xhe_aac_check_support]
import android.media.MediaCodecInfo.CodecProfileLevel
import android.media.MediaCodecList
import android.media.MediaFormat
import android.os.Build

/**
 * Checks if the current device supports xHE-AAC audio encoding.
 */
fun isXheAacEncodingSupported(): Boolean {
    // Verify API Level >= 37.1 (CINNAMON_BUN_1 / SDK 3700001).
    if (Build.VERSION.SDK_INT_FULL < Build.VERSION_CODES_FULL.CINNAMON_BUN_1) {
        return false
    }

    val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
    for (codecInfo in codecList.codecInfos) {
        if (!codecInfo.isEncoder) continue

        if (MediaFormat.MIMETYPE_AUDIO_AAC in codecInfo.supportedTypes) {
            val capabilities =
                codecInfo.getCapabilitiesForType(MediaFormat.MIMETYPE_AUDIO_AAC)
            // Check if AACObjectXHE (42) is present in profileLevels.
            val supportsXhe = capabilities.profileLevels.any { profileLevel ->
                profileLevel.profile == CodecProfileLevel.AACObjectXHE
            }
            if (supportsXhe) {
                return true
            }
        }
    }
    return false
}
// [END android_media_platform_xhe_aac_check_support]

// [START android_media_platform_xhe_aac_setup_encoder]
fun setupXheAacEncoder(): MediaCodec? {
    if (!isXheAacEncodingSupported()) {
        return null
    }

    val mimeType = MediaFormat.MIMETYPE_AUDIO_AAC
    val sampleRate = 48000
    val channelCount = 1 // Mono voice recording.
    val bitRate = 20000  // 20 kbps delivers superior speech clarity.

    val format = MediaFormat.createAudioFormat(mimeType, sampleRate, channelCount).apply {
        setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        setInteger(MediaFormat.KEY_AAC_PROFILE, CodecProfileLevel.AACObjectXHE)
    }

    // Find the hardware or software encoder supporting this format and profile.
    val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
    val encoderName = codecList.findEncoderForFormat(format) ?: return null
    val encoder = MediaCodec.createByCodecName(encoderName)
    encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    return encoder
}
// [END android_media_platform_xhe_aac_setup_encoder]
