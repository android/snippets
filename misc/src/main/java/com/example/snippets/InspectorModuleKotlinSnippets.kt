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

package com.example.snippets

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_MIMETYPE
import android.media.MediaMetadataRetriever.OPTION_CLOSEST
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.MediaExtractorCompat
import androidx.media3.exoplayer.source.TrackGroupArray
import androidx.media3.inspector.FrameExtractor
import androidx.media3.inspector.MetadataRetriever
import java.io.IOException
import java.nio.ByteBuffer
import kotlinx.coroutines.guava.await

const val TAG = "InspectorModuleLog"

@Suppress("unused_parameter")
@OptIn(UnstableApi::class)
class InspectorModuleKotlinSnippets {

    // [START android_dev_retriever_media3_kotlin]
    suspend fun retrieveMetadata(context: Context, mediaItem: MediaItem) {
        try {
            // 1. Build the retriever and open a .use block.
            // This automatically calls close() when the block finishes.
            MetadataRetriever.Builder(context, mediaItem).build().use { retriever ->
                // 2. Retrieve metadata asynchronously.
                val trackGroups = retriever.retrieveTrackGroups().await()
                val timeline = retriever.retrieveTimeline().await()
                val durationUs = retriever.retrieveDurationUs().await()
                handleMetadata(trackGroups, timeline, durationUs)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
    // [END android_dev_retriever_media3_kotlin]

    // [START android_migration_retriever_platform_kotlin]
    fun retrieveMetadataPlatform(mediaPath: String) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(mediaPath)
        try {
            val mimeType = retriever.extractMetadata(METADATA_KEY_MIMETYPE)
            Log.d(TAG, "MIME type: $mimeType")
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            retriever.release()
        }
    }
    // [END android_migration_retriever_platform_kotlin]

    // [START android_migration_retriever_media3_kotlin]
    suspend fun retrieveMetadataMedia3(context: Context, mediaItem: MediaItem) {
        try {
            MetadataRetriever.Builder(context, mediaItem).build().use { retriever ->
                val trackGroups = retriever.retrieveTrackGroups().await()

                for (i in 0 until trackGroups.length) {
                    val trackGroup = trackGroups.get(i)
                    for (j in 0 until trackGroup.length) {
                        val format = trackGroup.getFormat(j)
                        val mimeType = format.containerMimeType
                        Log.d(TAG, "MIME type: $mimeType")
                    }
                }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
    // [END android_migration_retriever_media3_kotlin]

    // [START android_dev_frame_media3_kotlin]
    suspend fun extractFrame(context: Context, mediaItem: MediaItem): Bitmap? {
        return try {
            // 1. Build the extractor and open a .use block.
            // This automatically calls close() when the block finishes.
            FrameExtractor.Builder(context, mediaItem).build().use { extractor ->
                // 2. Extract the specific frame at the 5000ms (5-second) mark
                val frame = extractor.getFrame(5000L).await()
                Log.d(TAG, "Extracted frame at ${frame.presentationTimeMs} ms")
                frame.bitmap
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: $e")
            null
        }
    }
    // [END android_dev_frame_media3_kotlin]

    // [START android_migration_frame_platform_kotlin]
    fun extractFramePlatform(mediaPath: String, frameTimeMs: Long): Bitmap? {
        var retriever: MediaMetadataRetriever? = null
        val bitmap: Bitmap?
        try {
            retriever = MediaMetadataRetriever()
            retriever.setDataSource(mediaPath)

            bitmap = retriever.getFrameAtTime(
                frameTimeMs * 1000L, // Time is in microseconds
                OPTION_CLOSEST
            )
            Log.d(TAG, "Extracted frame : $bitmap")
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            retriever?.release()
        }
        return bitmap
    }
    // [END android_migration_frame_platform_kotlin]

    // [START android_migration_frame_media3_kotlin]
    suspend fun extractFrameMedia3(
        context: Context,
        mediaItem: MediaItem,
        frameTimeMs: Long
    ): Bitmap? {
        return try {
            FrameExtractor.Builder(context, mediaItem).build().use { extractor ->
                val frame = extractor.getFrame(frameTimeMs).await()
                Log.d(TAG, "Extracted frame at ${frame.presentationTimeMs} ms")
                frame.bitmap
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: $e")
            null
        }
    }
    // [END android_migration_frame_media3_kotlin]

    // [START android_dev_extractor_media3_kotlin]
    fun extractSamples(context: Context, mediaPath: String) {
        val extractor = MediaExtractorCompat(context)
        try {
            // 1. Setup the extractor
            extractor.setDataSource(mediaPath)

            // Find and select available tracks
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                extractor.selectTrack(i)
            }

            // 2. Process samples
            val buffer = ByteBuffer.allocate(10 * 1024 * 1024)
            while (true) {
                // Read an encoded sample into the buffer.
                val bytesRead = extractor.readSampleData(buffer, 0)
                if (bytesRead < 0) break

                // Access sample metadata
                val trackIndex = extractor.sampleTrackIndex
                val presentationTimeUs: Long = extractor.sampleTime
                val sampleSize: Long = extractor.sampleSize

                extractor.advance()
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } finally {
            // 3. Release the extractor
            extractor.release()
        }
    }
    // [END android_dev_extractor_media3_kotlin]

    private fun handleMetadata(trackGroups: TrackGroupArray, timeline: Timeline, durationUs: Long) {
        Log.d(TAG, "TrackGroups: $trackGroups us")
        Log.d(TAG, "Timeline: $timeline us")
        Log.d(TAG, "Duration: $durationUs us")
    }
}
