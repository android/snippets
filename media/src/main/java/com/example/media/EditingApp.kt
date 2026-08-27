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

import android.content.Context
import android.graphics.Matrix
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.audio.ChannelMixingAudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Crop
import androidx.media3.effect.MatrixTransformation
import androidx.media3.effect.ScaleAndRotateTransformation
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.transformer.Composition
import androidx.media3.transformer.Composition.HDR_MODE_KEEP_HDR
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Transformer
import com.google.common.collect.ImmutableList
import kotlin.math.min

@OptIn(UnstableApi::class)
private fun configureTranscode(context: Context) {
    // [START android_media_editing_transcode]
    val transformer = Transformer.Builder(context)
        .setVideoMimeType(MimeTypes.VIDEO_H265)
        .setAudioMimeType(MimeTypes.AUDIO_AAC)
        .build()
    // [END android_media_editing_transcode]
}

@OptIn(UnstableApi::class)
private fun setHdrMode(videoSequence: EditedMediaItemSequence) {
    // [START android_media_editing_set_hdr_mode]
    val composition = Composition.Builder(
        ImmutableList.of(videoSequence))
        .setHdrMode(HDR_MODE_KEEP_HDR)
        .build()
    // [END android_media_editing_set_hdr_mode]
}

@OptIn(UnstableApi::class)
private fun trimVideo(videoUri: Uri) {
    // [START android_media_editing_trim_video]
    val clippingConfiguration = MediaItem.ClippingConfiguration.Builder()
        .setStartPositionMs(10_000) // start at 10 seconds
        .setEndPositionMs(20_000) // end at 20 seconds
        .build()
    val mediaItem = MediaItem.Builder()
        .setUri(videoUri)
        .setClippingConfiguration(clippingConfiguration)
        .build()
    // [END android_media_editing_trim_video]
}

@OptIn(UnstableApi::class)
private fun builtInEffects(mediaItem: MediaItem) {
    // [START android_media_editing_built_in_effects]
    val channelMixingProcessor = ChannelMixingAudioProcessor()
    val rotateEffect = ScaleAndRotateTransformation.Builder().setRotationDegrees(60f).build()
    val cropEffect = Crop(-0.5f, 0.5f, -0.5f, 0.5f)
    val effects = Effects(listOf(channelMixingProcessor), listOf(rotateEffect, cropEffect))

    val editedMediaItem = EditedMediaItem.Builder(mediaItem)
        .setEffects(effects)
        .build()
    // [END android_media_editing_built_in_effects]
}

@OptIn(UnstableApi::class)
private fun customEffects(inputMediaItem: MediaItem) {
    // [START android_media_editing_custom_effects]
    val zoomEffect = MatrixTransformation { presentationTimeUs ->
        val transformationMatrix = Matrix()
        // Set the scaling factor based on the playback position
        val scale = min(1f, presentationTimeUs / 1_000f)
        transformationMatrix.postScale(/* x */ scale, /* y */ scale)
        transformationMatrix
    }

    val editedMediaItem = EditedMediaItem.Builder(inputMediaItem)
        .setEffects(Effects(listOf(), listOf(zoomEffect)))
        .build()
    // [END android_media_editing_custom_effects]
}

@OptIn(UnstableApi::class)
private fun previewEffects(context: Context, inputMediaItem: MediaItem, zoomEffect: MatrixTransformation) {
    // [START android_media_editing_preview_effects]
    val player = ExoPlayer.Builder(context)
        .build()
        .also { exoPlayer ->
            exoPlayer.setMediaItem(inputMediaItem)
            exoPlayer.setVideoEffects(listOf(zoomEffect))
            exoPlayer.prepare()
        }
    // [END android_media_editing_preview_effects]
}

@OptIn(UnstableApi::class)
private fun previewAudioEffects(context: Context, channelMixingProcessor: ChannelMixingAudioProcessor) {
    // [START android_media_editing_preview_audio_effects]
    val player = ExoPlayer.Builder(context, object : DefaultRenderersFactory(context) {
        override fun buildAudioSink(
            context: Context,
            enableFloatOutput: Boolean,
            enableAudioTrackPlaybackParams: Boolean,
            // [START_EXCLUDE silent]
            /*
            // [END_EXCLUDE]
            enableOffload: Boolean
            // [START_EXCLUDE silent]
            */
            // [END_EXCLUDE]
        ): AudioSink? {
            return DefaultAudioSink.Builder(context)
                .setEnableFloatOutput(enableFloatOutput)
                .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                // [START_EXCLUDE silent]
                /*
                // [END_EXCLUDE]
                .setOffloadMode(if (enableOffload) {
                         DefaultAudioSink.OFFLOAD_MODE_ENABLED_GAPLESS_REQUIRED
                     } else {
                         DefaultAudioSink.OFFLOAD_MODE_DISABLED
                     })
                // [START_EXCLUDE silent]
                */
                // [END_EXCLUDE]
                .setAudioProcessors(arrayOf(channelMixingProcessor))
                .build()
            }
        }).build()
    // [END android_media_editing_preview_audio_effects]
}

@OptIn(UnstableApi::class)
private fun startTransformation(
    context: Context,
    editedMediaItem: EditedMediaItem,
    outputPath: String,
    listener: Transformer.Listener
) {
    // [START android_media_editing_start_transformation]
    val transformer = Transformer.Builder(context)
        .addListener(listener)
        .build()
    transformer.start(editedMediaItem, outputPath)
    // [END android_media_editing_start_transformation]
}
