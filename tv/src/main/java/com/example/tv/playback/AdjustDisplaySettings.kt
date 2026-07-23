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

package com.example.tv.playback

import android.content.Context
import android.media.MediaCodec
import android.media.MediaFormat
import android.media.quality.MediaQualityManager
import android.media.quality.PictureProfile
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi

// [START android_tv_playback_adjust_display_settings_profile_constants]
const val NAME_STANDARD: String = "standard"
const val NAME_VIVID: String = "vivid"
const val NAME_SPORTS: String = "sports"
const val NAME_GAME: String = "game"
const val NAME_MOVIE: String = "movie"
const val NAME_ENERGY_SAVING: String = "energy_saving"
const val NAME_USER: String = "user"
// [END android_tv_playback_adjust_display_settings_profile_constants]

@RequiresApi(36)
class AdjustDisplaySettings {

    fun queryAvailableProfiles(context: Context, mediaCodec: MediaCodec) {
// [START android_tv_playback_adjust_display_settings_available_profiles]
        if (Build.VERSION.SDK_INT >= 36) {
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
// [END android_tv_playback_adjust_display_settings_available_profiles]
    }

    fun getProfileByName(mediaQualityManager: MediaQualityManager) {
// [START android_tv_playback_adjust_display_settings_get_profile]
        if (Build.VERSION.SDK_INT >= 36) {
            val profile = mediaQualityManager.getPictureProfile(
                PictureProfile.TYPE_SYSTEM, NAME_SPORTS, null
            )
        }
// [END android_tv_playback_adjust_display_settings_get_profile]
    }
}
