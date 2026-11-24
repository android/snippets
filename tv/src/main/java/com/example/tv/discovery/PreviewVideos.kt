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
import android.content.ComponentName
import android.content.Context
import android.media.MediaPlayer
import android.media.tv.TvInputManager
import android.media.tv.TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING
import android.media.tv.TvInputService
import android.net.Uri
import android.util.Log
import android.view.Surface
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.TvContractCompat.PreviewPrograms
import java.io.IOException

// tvprovider's program builder setters are inherited from a restricted base class, so lint flags them.
@SuppressLint("RestrictedApi")
private fun setPreviewVideoUri(channelId: Long) {
    // [START android_tv_preview_video_uri]
    val previewVideoUrl = Uri.parse("https://www.example.com/preview.mp4")
    val builder = PreviewProgram.Builder()
    builder.setChannelId(channelId)
        // ...
        .setPreviewVideoUri(previewVideoUrl)
    // [END android_tv_preview_video_uri]
}

private fun buildPreviewProgramVideoUri(context: Context) {
    // [START android_tv_preview_program_video_uri]
    val id: Long = 1L // content identifier.
    val componentName = ComponentName(context, PreviewVideoInputService::class.java)
    val previewProgramVideoUri = TvContractCompat.buildPreviewProgramUri(id)
        .buildUpon()
        .appendQueryParameter("input", TvContractCompat.buildInputId(componentName))
        .build()
    // [END android_tv_preview_program_video_uri]
}

private fun buildPreviewProgramVideoUriFromString(componentName: ComponentName) {
    // [START android_tv_preview_program_video_uri_string]
    val previewProgramVideoUri = Uri.withAppendedPath(PreviewPrograms.CONTENT_URI, "content-identifier")
        .buildUpon()
        .appendQueryParameter("input", TvContractCompat.buildInputId(componentName))
        .build()
    // [END android_tv_preview_program_video_uri_string]
}

// [START android_tv_preview_video_input_service]
// import android.content.Context
// import android.media.MediaPlayer
// import android.media.tv.TvInputService
// import android.net.Uri
// import android.util.Log
// import android.view.Surface
// import java.io.IOException

class PreviewVideoInputService : TvInputService() {

    override fun onCreateSession(inputId: String): TvInputService.Session? {
        return PreviewSession(this)
    }

    private inner class PreviewSession(
        internal var context: Context
    ) : TvInputService.Session(context) {

        internal var mediaPlayer: MediaPlayer? = MediaPlayer()

        override fun onRelease() {
            mediaPlayer?.release()
            mediaPlayer = null
        }

        override fun onTune(uri: Uri): Boolean {
            // Let the TvInputService know that the video is being loaded.
            notifyVideoUnavailable(VIDEO_UNAVAILABLE_REASON_TUNING)
            // Fetch the stream url from the TV Provider database
            // for content://android.media.tv/preview_program/<id>.
            val id = uri.lastPathSegment
            // Load your video in the background.
            retrieveYourVideoPreviewUrl(id) { videoUri ->
                if (videoUri == null) {
                    Log.d(TAG, "Could not find video $id")
                    notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN)
                }

                try {
                    mediaPlayer?.setDataSource(getApplicationContext(), videoUri)
                    mediaPlayer?.prepare()
                    mediaPlayer?.start()
                    notifyVideoAvailable()
                } catch (e: IOException) {
                    Log.e(TAG, "Could not prepare media player", e)
                    notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN)
                }
            }
            return true
        }

        override fun onSetSurface(surface: Surface?): Boolean {
            mediaPlayer?.setSurface(surface)
            return true
        }

        override fun onSetStreamVolume(volume: Float) {
            // The home screen may fade in and out the video's volume.
            // Your player should be updated accordingly.
            mediaPlayer?.setVolume(volume, volume)
        }

        override fun onSetCaptionEnabled(b: Boolean) {
            // enable/disable captions here.
        }
    }

    companion object {
        private const val TAG = "PreviewInputService"
    }
}
// [END android_tv_preview_video_input_service]

private fun retrieveYourVideoPreviewUrl(id: String?, callback: (Uri) -> Unit) {
    callback(Uri.parse("https://www.example.com/$id.mp4"))
}
