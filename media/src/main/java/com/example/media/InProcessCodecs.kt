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
import android.media.MediaFormat
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.mediacodec.MediaCodecInfo
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.mediacodec.MediaCodecUtil

// [START android_media_platform_in_process_codecs_create_by_name]
val INPROC_AAC_CODEC = "c2.android.inproc.aac.decoder"

fun createAudioDecoder(): MediaCodec {
    return try {
        // Attempt to opt in to the low-latency in-process AAC decoder.
        MediaCodec.createByCodecName(INPROC_AAC_CODEC)
    } catch (e: IllegalArgumentException) {
        // Fall back to the default platform AAC decoder.
        MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
    }
}
// [END android_media_platform_in_process_codecs_create_by_name]

@OptIn(UnstableApi::class)
// [START android_media_platform_in_process_codecs_media3_selector]
class InprocPreferredMediaCodecSelector : MediaCodecSelector {
    override fun getDecoderInfos(
        mimeType: String,
        requiresSecureDecoder: Boolean,
        requiresTunnelingDecoder: Boolean
    ): List<MediaCodecInfo> {
        val defaultInfos = MediaCodecUtil.getDecoderInfos(
            mimeType,
            requiresSecureDecoder,
            requiresTunnelingDecoder
        )
        val inprocNames = setOf(
            "c2.android.inproc.opus.decoder",
            "c2.android.inproc.aac.decoder"
        )
        // Sort in-process decoders to the top of the selection list.
        return defaultInfos.sortedByDescending { it.name in inprocNames }
    }
}
// [END android_media_platform_in_process_codecs_media3_selector]
