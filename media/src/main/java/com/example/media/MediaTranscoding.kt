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

import android.content.ContentResolver
import android.media.ApplicationMediaCapabilities
import android.media.MediaFeature
import android.media.MediaFormat
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.provider.MediaStore

class MediaTranscoding(
    private val contentResolver: ContentResolver,
    private val mediaUri: Uri,
    private val mediaMimeType: String
) {

    fun declareCapabilities() {
        // [START android_media_platform_transcoding_declare_capabilities_code]
        val mediaCapabilities = ApplicationMediaCapabilities.Builder()
            .addSupportedVideoMimeType(MediaFormat.MIMETYPE_VIDEO_HEVC)
            .addUnsupportedHdrType(MediaFeature.HdrType.HDR10)
            .addUnsupportedHdrType(MediaFeature.HdrType.HDR10_PLUS)
            .build()
        // [END android_media_platform_transcoding_declare_capabilities_code]

        // [START android_media_platform_transcoding_open_file_descriptor]
        val providerOptions = Bundle().apply {
            putParcelable(MediaStore.EXTRA_MEDIA_CAPABILITIES, mediaCapabilities)
        }
        contentResolver.openTypedAssetFileDescriptor(mediaUri, mediaMimeType, providerOptions)
            ?.use { fileDescriptor ->
                // Content will be transcoded based on values defined in the
                // ApplicationMediaCapabilities provided.
            }
        // [END android_media_platform_transcoding_open_file_descriptor]
    }

    fun openWithCallingUid() {
        // [START android_media_platform_transcoding_calling_uid]
        val providerOptions = Bundle().apply {
            putInt(MediaStore.EXTRA_MEDIA_CAPABILITIES_UID, Binder.getCallingUid())
        }
        contentResolver.openTypedAssetFileDescriptor(mediaUri, mediaMimeType, providerOptions)
            ?.use { fileDescriptor ->
                // Content will be transcoded based on the media capabilities of the
                // calling app.
            }
        // [END android_media_platform_transcoding_calling_uid]
    }
}
