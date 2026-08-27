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
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri

class MediaPlayerBasics(private val context: Context) {

    fun playRawResource() {
        // [START android_media_platform_mediaplayer_basics_create_raw]
        var mediaPlayer = MediaPlayer.create(context, R.raw.sound_file_1)
        mediaPlayer.start() // no need to call prepare(); create() does that for you
        // [END android_media_platform_mediaplayer_basics_create_raw]
    }

    fun playLocalUri(applicationContext: Context, myUri: Uri) {
        // [START android_media_platform_mediaplayer_basics_local_uri]
        val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(applicationContext, myUri)
            prepare()
            start()
        }
        // [END android_media_platform_mediaplayer_basics_local_uri]
    }

    fun playRemoteUrl(url: String) {
        // [START android_media_platform_mediaplayer_basics_remote_url]
        val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            prepare() // might take long! (for buffering, etc)
            start()
        }
        // [END android_media_platform_mediaplayer_basics_remote_url]
    }
}
