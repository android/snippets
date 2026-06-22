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

package com.example.tv.ui

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import androidx.activity.ComponentActivity

class MyActivity : ComponentActivity()

class NowPlayingActivity : ComponentActivity() {
    private lateinit var session: MediaSession

    fun setSessionActivity() {
        val context: Context = this
        // [START android_tv_now_playing_set_activity]
        val pi: PendingIntent = Intent(context, MyActivity::class.java).let { intent ->
            PendingIntent.getActivity(
                context, 99 /*request code*/,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        session.setSessionActivity(pi)
        // [END android_tv_now_playing_set_activity]
    }
}
