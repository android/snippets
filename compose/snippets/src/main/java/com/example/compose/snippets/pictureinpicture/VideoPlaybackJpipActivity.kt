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

package com.example.compose.snippets.pictureinpicture

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.content.ContextCompat
import androidx.core.pip.PictureInPictureDelegate
import androidx.core.pip.VideoPlaybackPictureInPicture

// [START android_jpip_video_playback_impl]
class VideoPlaybackJpipActivity : ComponentActivity(), PictureInPictureDelegate.OnPictureInPictureEventListener {
    private lateinit var pictureInPictureImpl: VideoPlaybackPictureInPicture
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pictureInPictureImpl = VideoPlaybackPictureInPicture(this)
        pictureInPictureImpl.addOnPictureInPictureEventListener(
            ContextCompat.getMainExecutor(this),
            this
        )
        setContent {
            ContentScreen(pictureInPictureImpl)
        }
    }
    override fun onPictureInPictureEvent(
        event: PictureInPictureDelegate.Event,
        config: Configuration?
    ) {
        when (event) {
            PictureInPictureDelegate.Event.ENTER_ANIMATION_START -> { /* Hide overlays */ }
            PictureInPictureDelegate.Event.ENTER_ANIMATION_END -> { /* Animation finished */ }
            PictureInPictureDelegate.Event.ENTERED -> { /* Switch to PiP layout */ }
            PictureInPictureDelegate.Event.STASHED -> { /* PiP stashed */ }
            PictureInPictureDelegate.Event.UNSTASHED -> { /* PiP unstashed */ }
            PictureInPictureDelegate.Event.EXITED -> { /* Return to full-screen */ }
        }
    }

    @Composable
    fun ContentScreen(pipController: VideoPlaybackPictureInPicture) {
        DisposableEffect(pipController) {
            onDispose {
                pipController.close()
            }
        }
    }
}
// [END android_jpip_video_playback_impl]
