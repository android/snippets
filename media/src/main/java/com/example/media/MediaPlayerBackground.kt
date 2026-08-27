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

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.IBinder
import android.os.PowerManager

// [START android_media_platform_mediaplayer_background_prepare_async]
private const val ACTION_PLAY: String = "com.example.action.PLAY"

class MyService : Service(), MediaPlayer.OnPreparedListener {

    private var mMediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action: String? = intent?.action
        when (action) {
            ACTION_PLAY -> {
                mMediaPlayer = MediaPlayer().apply {
                    setOnPreparedListener(this@MyService)
                    prepareAsync() // prepare async to not block main thread
                }
            }
        }
        return START_NOT_STICKY
    }

    /** Called when MediaPlayer is ready */
    override fun onPrepared(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
// [END android_media_platform_mediaplayer_background_prepare_async]

// [START android_media_platform_mediaplayer_background_error_listener]
class MyErrorService : Service(), MediaPlayer.OnErrorListener {

    private var mediaPlayer: MediaPlayer? = null

    fun initMediaPlayer() {
        // ...initialize the MediaPlayer here...
        mediaPlayer?.setOnErrorListener(this)
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!
        return true
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
// [END android_media_platform_mediaplayer_background_error_listener]

class MediaPlayerBackgroundSnippets(private val context: Context) {
    fun setupWakeLock() {
        // [START android_media_platform_mediaplayer_background_wake_mode]
        val mediaPlayer = MediaPlayer().apply {
            // ... other initialization here ...
            setWakeMode(context.applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        }
        // [END android_media_platform_mediaplayer_background_wake_mode]
    }

    fun wifiLockUsage() {
        // [START android_media_platform_mediaplayer_background_wifi_lock_acquire]
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiLock: WifiManager.WifiLock =
            wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "mylock")

        wifiLock.acquire()
        // [END android_media_platform_mediaplayer_background_wifi_lock_acquire]

        // [START android_media_platform_mediaplayer_background_wifi_lock_release]
        wifiLock.release()
        // [END android_media_platform_mediaplayer_background_wifi_lock_release]
    }
}

// [START android_media_platform_mediaplayer_background_release]
class MyCleanupService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    // ...

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
// [END android_media_platform_mediaplayer_background_release]
