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

package com.example.media.background.wakelocks

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.IBinder
import android.os.PowerManager

class MyService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    fun setupWakeLock() {
        // [START android_media_platform_mediaplayer_background_wake_mode]
        mediaPlayer = MediaPlayer().apply {
            // ... other initialization here ...
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        }
        // [END android_media_platform_mediaplayer_background_wake_mode]
    }

    fun wifiLockUsage() {
        // [START android_media_platform_mediaplayer_background_wifi_lock_acquire]
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiLock: WifiManager.WifiLock =
            wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock")

        wifiLock.acquire()
        // [END android_media_platform_mediaplayer_background_wifi_lock_acquire]

        // [START android_media_platform_mediaplayer_background_wifi_lock_release]
        wifiLock.release()
        // [END android_media_platform_mediaplayer_background_wifi_lock_release]
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
