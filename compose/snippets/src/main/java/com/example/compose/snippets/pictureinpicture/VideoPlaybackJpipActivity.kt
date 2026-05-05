package com.example.compose.snippets.pictureinpicture

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.core.pip.PictureInPictureDelegate
import androidx.core.pip.VideoPlaybackPictureInPicture

// [START android_jpip_video_playback_impl]
class VideoPlaybackJpipActivity : ComponentActivity(), PictureInPictureDelegate.OnPictureInPictureEventListener {
    private lateinit var pictureInPictureImpl: VideoPlaybackPictureInPicture
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // BasicPictureInPicture is ideal for Navigation and Video call use cases.
        pictureInPictureImpl.addOnPictureInPictureEventListener(
            ContextCompat.getMainExecutor(this),
            this
        )
        setContent {
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

    override fun onDestroy() {
        super.onDestroy()
        pictureInPictureImpl.close()
    }

}
// [END android_jpip_video_playback_impl]