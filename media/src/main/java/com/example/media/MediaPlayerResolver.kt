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

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri

class MediaPlayerResolver : Activity() {

    private var mediaPlayer: MediaPlayer? = null

    fun queryMedia() {
        // [START android_media_platform_mediaplayer_resolver_query]
        val resolver: ContentResolver = contentResolver
        val uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = resolver.query(uri, null, null, null, null)
        when {
            cursor == null -> {
                // query failed, handle error.
            }
            !cursor.moveToFirst() -> {
                // no media on the device
            }
            else -> {
                val titleColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
                val idColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
                do {
                    val thisId = cursor.getLong(idColumn)
                    val thisTitle = cursor.getString(titleColumn)
                    // ...process entry...
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        // [END android_media_platform_mediaplayer_resolver_query]
    }

    fun playContentUri(id: Long) {
        // [START android_media_platform_mediaplayer_resolver_play_uri]
        val contentUri: Uri =
            ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(applicationContext, contentUri)
        }

        // ...prepare and start...
        // [START_EXCLUDE silent]
        mediaPlayer?.prepare()
        mediaPlayer?.start()
        // [END_EXCLUDE]
        // [END android_media_platform_mediaplayer_resolver_play_uri]
    }
}
