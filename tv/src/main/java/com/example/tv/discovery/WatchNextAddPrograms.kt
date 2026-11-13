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
import android.content.Context
import android.net.Uri
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.WatchNextProgram

// tvprovider's program builder setters are inherited from a restricted base class, so lint flags them.
@SuppressLint("RestrictedApi")
private fun insertWatchNextProgram(context: Context, time: Long, uri: Uri, appProgramId: String) {
    // [START android_tv_watch_next_program_insert]
    val builder = WatchNextProgram.Builder()
    builder.setType(TvContractCompat.WatchNextPrograms.TYPE_MOVIE)
        .setWatchNextType(TvContractCompat.WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE)
        .setLastEngagementTimeUtcMillis(time)
        .setTitle("Title")
        .setDescription("Program description")
        .setPosterArtUri(uri)
        .setIntentUri(uri)
        .setInternalProviderId(appProgramId)

    val watchNextProgramUri = context.contentResolver
        .insert(
            TvContractCompat.WatchNextPrograms.CONTENT_URI,
            builder.build().toContentValues()
        )
    // [END android_tv_watch_next_program_insert]
}
