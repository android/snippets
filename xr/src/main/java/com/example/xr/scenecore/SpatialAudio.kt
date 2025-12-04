/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.xr.scenecore

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_ASSISTANCE_SONIFICATION
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.audio.AudioCapabilities
import androidx.xr.runtime.Session
import androidx.xr.scenecore.Entity
import androidx.xr.scenecore.PointSourceParams
import androidx.xr.scenecore.SoundFieldAttributes
import androidx.xr.scenecore.SpatialCapability
import androidx.xr.scenecore.SpatialMediaPlayer
import androidx.xr.scenecore.SpatialSoundPool
import androidx.xr.scenecore.SpatializerConstants
import androidx.xr.scenecore.scene

private fun playSpatialAudioAtEntity(session: Session, appContext: Context, entity: Entity) {
    // [START androidxr_scenecore_playSpatialAudio]
    // Check spatial capabilities before using spatial audio
    if (session.scene.spatialCapabilities.contains(SpatialCapability.SPATIAL_AUDIO)
    ) { // The session has spatial audio capabilities
        val maxVolume = 1F
        val lowPriority = 0
        val infiniteLoop = -1
        val normalSpeed = 1F

        val soundPool = SoundPool.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(CONTENT_TYPE_SONIFICATION)
                    .setUsage(USAGE_ASSISTANCE_SONIFICATION)
                    .build()
            )
            .build()

        val pointSource = PointSourceParams(entity)

        val soundEffect = appContext.assets.openFd("sounds/tiger_16db.mp3")
        val pointSoundId = soundPool.load(soundEffect, lowPriority)

        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            // wait for the sound file to be loaded into the soundPool
            if (status == 0) {
                SpatialSoundPool.play(
                    session = session,
                    soundPool = soundPool,
                    soundID = pointSoundId,
                    params = pointSource,
                    volume = maxVolume,
                    priority = lowPriority,
                    loop = infiniteLoop,
                    rate = normalSpeed
                )
            }
        }
    } else {
        // The session does not have spatial audio capabilities
    }
    // [END androidxr_scenecore_playSpatialAudio]
}

private fun playSpatialAudioAtEntitySurround(session: Session, appContext: Context) {
    // [START androidxr_scenecore_playSpatialAudioSurround]
    // Check spatial capabilities before using spatial audio
    if (session.scene.spatialCapabilities.contains(SpatialCapability.SPATIAL_AUDIO)) {
        // The session has spatial audio capabilities

        val pointSourceAttributes = PointSourceParams(session.scene.mainPanelEntity)

        val mediaPlayer = MediaPlayer()

        val fivePointOneAudio = appContext.assets.openFd("sounds/aac_51.ogg")
        mediaPlayer.reset()
        mediaPlayer.setDataSource(fivePointOneAudio)

        val audioAttributes =
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()

        SpatialMediaPlayer.setPointSourceParams(
            session,
            mediaPlayer,
            pointSourceAttributes
        )

        mediaPlayer.setAudioAttributes(audioAttributes)
        mediaPlayer.prepare()
        mediaPlayer.start()
    } else {
        // The session does not have spatial audio capabilities
    }
    // [END androidxr_scenecore_playSpatialAudioSurround]
}

private fun playSpatialAudioAtEntityAmbionics(session: Session, appContext: Context) {
    // [START androidxr_scenecore_playSpatialAudioAmbionics]
    // Check spatial capabilities before using spatial audio
    if (session.scene.spatialCapabilities.contains(SpatialCapability.SPATIAL_AUDIO)) {
        // The session has spatial audio capabilities

        val soundFieldAttributes =
            SoundFieldAttributes(SpatializerConstants.AmbisonicsOrder.FIRST_ORDER)

        val mediaPlayer = MediaPlayer()

        val soundFieldAudio = appContext.assets.openFd("sounds/foa_basketball_16bit.wav")

        mediaPlayer.reset()
        mediaPlayer.setDataSource(soundFieldAudio)

        val audioAttributes =
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()

        SpatialMediaPlayer.setSoundFieldAttributes(
            session,
            mediaPlayer,
            soundFieldAttributes
        )

        mediaPlayer.setAudioAttributes(audioAttributes)
        mediaPlayer.prepare()
        mediaPlayer.start()
    } else {
        // The session does not have spatial audio capabilities
    }
    // [END androidxr_scenecore_playSpatialAudioAmbionics]
}

@OptIn(UnstableApi::class)
private fun detectSupport(context: Context) {
    // [START androidxr_scenecore_dolby_detect_support]
    val audioCapabilities = AudioCapabilities.getCapabilities(context, androidx.media3.common.AudioAttributes.DEFAULT, null)
    if (audioCapabilities.supportsEncoding(C.ENCODING_AC3)) {
        // Device supports playback of the Dolby Digital media format.
    }
    if (audioCapabilities.supportsEncoding(C.ENCODING_E_AC3)) {
        // Device supports playback of the Dolby Digital Plus media format.
    }
    if (audioCapabilities.supportsEncoding(C.ENCODING_E_AC3_JOC)) {
        // Device supports playback of the Dolby Digital Plus with Dolby Atmos media format.
    }
    // [END androidxr_scenecore_dolby_detect_support]
}
