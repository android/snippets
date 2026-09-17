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

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.Composition.HDR_MODE_TONE_MAP_HDR_TO_SDR_USING_OPEN_GL
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Transformer
import com.google.common.collect.ImmutableList

@OptIn(UnstableApi::class)
private fun setupHdrToSdrTransformer(
    context: Context,
    videoSequence: EditedMediaItemSequence,
    listener: Transformer.Listener
) {
    // [START android_media_sharing_hdr_to_sdr]
    val transformer = Transformer.Builder(context)
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        .addListener(/* ... */)
        // [START_EXCLUDE silent]
         */
        .addListener(listener)
        // [END_EXCLUDE]
        .build()
    val composition = Composition.Builder(
        ImmutableList.of(videoSequence)
    )
        .setHdrMode(HDR_MODE_TONE_MAP_HDR_TO_SDR_USING_OPEN_GL)
        .build()
    // [END android_media_sharing_hdr_to_sdr]
}
