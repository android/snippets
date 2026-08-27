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
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaCrypto
import android.media.MediaFormat
import android.view.Display
import android.view.Surface
import android.view.SurfaceView
import java.nio.ByteBuffer
import java.util.ArrayDeque
import java.util.Queue

// Constant for HDR_TYPE_HLG (Display.HdrCapabilities.HDR_TYPE_HLG)
private const val HDR_TYPE_HLG = 2

private fun checkHdrSupport(display: Display?) {
    // [START android_media_hdr_playback_check_support]
    // Check if display supports the HDR type
    val capabilities = display?.hdrCapabilities?.supportedHdrTypes ?: intArrayOf()
    if (!capabilities.contains(HDR_TYPE_HLG)) {
      throw RuntimeException("Display does not support desired HDR type")
    }
    // [END android_media_hdr_playback_check_support]
}

private fun mediaCodecHdrFlow(
    surfaceView: SurfaceView,
    queue: Queue<Int>,
    timestamp: Long,
    crypto: MediaCrypto?,
    offset: Int,
    size: Int,
    flags: Int,
    isStreaming: Boolean
) {
    // [START android_media_hdr_playback_mediacodec]
    // Check if there's a codec that supports the specific HDR profile
    val list = MediaCodecList(MediaCodecList.REGULAR_CODECS)
    var format = MediaFormat() /* media format from the container */
    format.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AV1ProfileMain10)
    val codecName = list.findDecoderForFormat(format) ?: throw RuntimeException("No codec supports the format")

    // Here is a standard MediaCodec playback flow
    val codec: MediaCodec = MediaCodec.createByCodecName(codecName)
    val surface: Surface = surfaceView.holder.surface
    val callback: MediaCodec.Callback = (object : MediaCodec.Callback() {
       override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
          queue.offer(index)
       }

       override fun onOutputBufferAvailable(
          codec: MediaCodec,
          index: Int,
          info: MediaCodec.BufferInfo
       ) {
          codec.releaseOutputBuffer(index, timestamp)
       }

       override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
          // handle error
       }

       override fun onOutputFormatChanged(
          codec: MediaCodec, format: MediaFormat
       ) {
          // handle format change
       }
    })

    codec.setCallback(callback)
    codec.configure(format, surface, crypto, 0 /* flags */)
    codec.start()
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    while (/* until EOS */) {
    // [START_EXCLUDE silent]
    */
    while (isStreaming) {
    // [END_EXCLUDE]
       val index = queue.poll()
       val buffer = codec.getInputBuffer(index)
       // [START_EXCLUDE silent]
       /*
       // [END_EXCLUDE]
       buffer?.put(/* write bitstream */)
       // [START_EXCLUDE silent]
       */
       buffer?.put(byteArrayOf())
       // [END_EXCLUDE]
       codec.queueInputBuffer(index, offset, size, timestamp, flags)
    }
    codec.stop()
    codec.release()
    // [END android_media_hdr_playback_mediacodec]
}
