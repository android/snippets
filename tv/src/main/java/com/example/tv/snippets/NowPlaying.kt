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

package com.example.tv.snippets

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession

class MyActivity : Activity()

class NowPlayingActivity : Activity() {
    private lateinit var session: MediaSession
    private lateinit var context: Context

    fun setSessionActivity() {
        // [START android_tv_now_playing_set_activity]
        val pi: PendingIntent = Intent(context, MyActivity::class.java).let { intent ->
            PendingIntent.getActivity(
                context, 99 /*request code*/,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        session.setSessionActivity(pi)
        // [END android_tv_now_playing_set_activity]
    }
}
