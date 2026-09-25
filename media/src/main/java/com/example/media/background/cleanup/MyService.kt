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

package com.example.media.background.cleanup

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

// [START android_media_platform_mediaplayer_background_release]
class MyService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    // ...

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
    // [START_EXCLUDE silent]
    override fun onBind(intent: Intent?): IBinder? = null
    // [END_EXCLUDE]
}
// [END android_media_platform_mediaplayer_background_release]
