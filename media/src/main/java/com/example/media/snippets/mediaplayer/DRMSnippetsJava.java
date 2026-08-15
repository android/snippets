package com.example.media.snippets.mediaplayer;

import android.media.MediaPlayer;
import java.util.UUID;

public class DRMSnippetsJava {
    private MediaPlayer mediaPlayer;

    public void syncDRM() {
        // [START android_mediaplayer_drm_sync_java]
        try {
            mediaPlayer.setDataSource("https://example.com/video.mp4");
            mediaPlayer.setOnDrmConfigHelper(null); // optional, for custom configuration
            mediaPlayer.prepare();
            if (mediaPlayer.getDrmInfo() != null) {
                mediaPlayer.prepareDrm(UUID.randomUUID());
                mediaPlayer.getKeyRequest(null, null, null, 0, null);
                mediaPlayer.provideKeyResponse(null, null);
            }

            // MediaPlayer is now ready to use
            mediaPlayer.start();
            // ...play/pause/resume...
            mediaPlayer.stop();
            mediaPlayer.releaseDrm();
        } catch (Exception e) {
            // Handle exceptions
        }
        // [END android_mediaplayer_drm_sync_java]
    }

    // [START android_mediaplayer_drm_async_java]
    public class MyDrmActivity implements MediaPlayer.OnDrmInfoListener, MediaPlayer.OnPreparedListener {
        public void setupAsync(MediaPlayer mediaPlayer) {
            try {
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnDrmInfoListener(this);
                mediaPlayer.setDataSource("https://example.com/video.mp4");
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                // Handle exceptions
            }
        }

        // If the data source content is protected you receive a call to the onDrmInfo() callback.
        @Override
        public void onDrmInfo(MediaPlayer mediaPlayer, MediaPlayer.DrmInfo drmInfo) {
            try {
                mediaPlayer.prepareDrm(UUID.randomUUID());
                mediaPlayer.getKeyRequest(null, null, null, 0, null);
                mediaPlayer.provideKeyResponse(null, null);
            } catch (Exception e) {
                // Handle exceptions
            }
        }

        // When prepareAsync() finishes, you receive a call to the onPrepared() callback.
        // If there is a DRM, onDrmInfo() sets it up before executing this callback,
        // so you can start the player.
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
        }
    }
    // [END android_mediaplayer_drm_async_java]
}
