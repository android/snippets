package com.example.snippets

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.MediaExtractorCompat
import androidx.media3.exoplayer.source.TrackGroupArray
import androidx.media3.inspector.FrameExtractor
import androidx.media3.inspector.MetadataRetriever
import kotlinx.coroutines.guava.await
import java.io.IOException
import java.nio.ByteBuffer

const val TAG = "InspectorModuleLog"

@Suppress("unused_parameter")
@OptIn(UnstableApi::class)
class InspectorModuleKotlinSnippets {

    // [START android_media3_inspector_MetadataRetriever_kotlin]
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
    // [END android_media3_inspector_MetadataRetriever_kotlin]

    // [START android_media3_inspector_FrameExtractor_kotlin]
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
    // [END android_media3_inspector_FrameExtractor_kotlin]

    // [START android_media3_inspector_MediaExtractorCompat_kotlin]
    fun extractSamples(context: Context, mediaPath: String){
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
            extractor.release() // 3. Release the extractor
        }
    }
    // [END android_media3_inspector_MediaExtractorCompat_kotlin]

    private fun handleMetadata(trackGroups: TrackGroupArray, timeline: Timeline, durationUs: Long) {
        Log.d(TAG, "TrackGroups: $trackGroups us")
        Log.d(TAG, "Timeline: $timeline us")
        Log.d(TAG, "Duration: $durationUs us")
    }

}