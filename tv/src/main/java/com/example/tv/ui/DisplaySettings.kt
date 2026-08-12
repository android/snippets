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

import android.content.Context
import android.media.MediaFormat
import android.media.quality.MediaQualityManager
import android.media.quality.PictureProfile
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity

// [START android_tv_adjust_display_constants]
const val NAME_STANDARD: String = "standard"
const val NAME_VIVID: String = "vivid"
const val NAME_SPORTS: String = "sports"
const val NAME_GAME: String = "game"
const val NAME_MOVIE: String = "movie"
const val NAME_ENERGY_SAVING: String = "energy_saving"
const val NAME_USER: String = "user"
// [END android_tv_adjust_display_constants]

class DisplaySettingsActivity : ComponentActivity() {
    private lateinit var context: Context
    private lateinit var mediaCodec: android.media.MediaCodec
    private lateinit var mediaQualityManager: MediaQualityManager

    fun queryAndApplySportsProfile() {
        // [START android_tv_adjust_display_query]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            val mediaQualityManager: MediaQualityManager =
                context.getSystemService(MediaQualityManager::class.java)
            val profiles = mediaQualityManager.getAvailablePictureProfiles(null)
            for (profile in profiles) {
                // If we have a system sports profile, apply it to our media codec
                if (profile.profileType == PictureProfile.TYPE_SYSTEM &&
                    profile.name == NAME_SPORTS
                ) {
                    val bundle = Bundle().apply { 
                        putParcelable(MediaFormat.KEY_PICTURE_PROFILE_INSTANCE, profile)
                    }
                    mediaCodec.setParameters(bundle)
                }
            }
        }
        // [END android_tv_adjust_display_query]
    }

    fun getSpecificProfileByName() {
        // [START android_tv_adjust_display_get_specific]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            val profile = mediaQualityManager.getPictureProfile(
                PictureProfile.TYPE_SYSTEM, NAME_SPORTS, null
            )
        }
        // [END android_tv_adjust_display_get_specific]
    }
}
