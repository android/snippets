package com.example.media.snippets.mediaplayer

import android.media.MediaPlayer
import java.util.UUID

class DRMSnippets {
    private var mediaPlayer: MediaPlayer? = null

    fun syncDRM() {
        // [START android_mediaplayer_drm_sync]
        mediaPlayer?.apply {
            setDataSource("https://example.com/video.mp4")
            setOnDrmConfigHelper { _ -> /* configuration */ } // optional, for custom configuration
            prepare()
            drmInfo?.also {
                prepareDrm(UUID.randomUUID())
                getKeyRequest(byteArrayOf(), byteArrayOf(), "", 0, null)
                provideKeyResponse(byteArrayOf(), byteArrayOf())
            }

            // MediaPlayer is now ready to use
            start()
            // ...play/pause/resume... 
            stop()
            releaseDrm()
        }
        // [END android_mediaplayer_drm_sync]
    }

    // [START android_mediaplayer_drm_async]
    inner class MyDrmActivity : MediaPlayer.OnDrmInfoListener, MediaPlayer.OnPreparedListener {
        fun setupAsync(mediaPlayer: MediaPlayer) {
            mediaPlayer.apply {
                setOnPreparedListener(this@MyDrmActivity)
                setOnDrmInfoListener(this@MyDrmActivity)
                setDataSource("https://example.com/video.mp4")
                prepareAsync()
            }
        }

        // If the data source content is protected you receive a call to the onDrmInfo() callback.
        override fun onDrmInfo(mediaPlayer: MediaPlayer, drmInfo: MediaPlayer.DrmInfo) {
            mediaPlayer.apply {
                prepareDrm(UUID.randomUUID())
                getKeyRequest(byteArrayOf(), byteArrayOf(), "", 0, null)
                provideKeyResponse(byteArrayOf(), byteArrayOf())
            }
        }

        // When prepareAsync() finishes, you receive a call to the onPrepared() callback.
        // If there is a DRM, onDrmInfo() sets it up before executing this callback,
        // so you can start the player.
        override fun onPrepared(mediaPlayer: MediaPlayer) {
            mediaPlayer.start()
        }
    }
    // [END android_mediaplayer_drm_async]
}
