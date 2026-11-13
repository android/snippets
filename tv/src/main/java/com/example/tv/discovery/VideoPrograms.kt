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

package com.example.tv.discovery

import android.annotation.SuppressLint
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat

// tvprovider's program builder setters are inherited from a restricted base class, so lint flags them.
@SuppressLint("RestrictedApi")
private fun buildVideoProgram(channelId: Long, clip: Clip) {
    // [START android_tv_video_program_builder]
    val program = PreviewProgram.Builder()
        .setChannelId(channelId)
        .setTitle(clip.getTitle())
        .setDescription(clip.getDescription())
        .setType(TvContractCompat.PreviewPrograms.TYPE_MOVIE)
        // Set required attributes.
        .build()
    // [END android_tv_video_program_builder]
}

private class Clip(private val title: String, private val description: String) {
    fun getTitle(): String = title
    fun getDescription(): String = description
}
