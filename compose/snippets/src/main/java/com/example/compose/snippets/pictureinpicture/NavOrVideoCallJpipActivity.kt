package com.example.compose.snippets.pictureinpicture

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.core.pip.BasicPictureInPicture
import androidx.core.pip.PictureInPictureDelegate

// [START android_jpip_basic_impl]
class NavOrVideoCallJpipActivity : ComponentActivity(), PictureInPictureDelegate.OnPictureInPictureEventListener {
    private lateinit var pictureInPictureImpl: BasicPictureInPicture
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
            PictureInPictureDelegate.Event.ENTERED -> { /* Toggle to PiP layout */ }
            PictureInPictureDelegate.Event.EXITED -> { /* Toggle to Full-screen layout */ }
            PictureInPictureDelegate.Event.STASHED -> { /* Optional: PiP is stashed */ }
            PictureInPictureDelegate.Event.UNSTASHED -> { /* Optional: PiP is unstashed */ }
        }
    }
}
// [END android_jpip_basic_impl]