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

package com.example.snippets;

import android.content.Context;
import android.media.MediaFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Timeline;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.MediaExtractorCompat;
import androidx.media3.exoplayer.source.TrackGroupArray;
import androidx.media3.inspector.FrameExtractor;
import androidx.media3.inspector.MetadataRetriever;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kotlin.Suppress;

@Suppress(names = "unused_parameter")
@OptIn(markerClass = UnstableApi.class)
public class InspectorModuleJavaSnippets {
    private final String TAG = "InspectorModuleLog";

    // [START android_media3_inspector_MetadataRetriever_java]
    public void retrieveMetadata(Context context, MediaItem mediaItem) {
        try (MetadataRetriever metadataRetriever = new MetadataRetriever.Builder(context, mediaItem).build()) {
            ListenableFuture<TrackGroupArray> trackGroupsFuture = metadataRetriever.retrieveTrackGroups();
            ListenableFuture<Timeline> timelineFuture = metadataRetriever.retrieveTimeline();
            ListenableFuture<Long> durationUsFuture = metadataRetriever.retrieveDurationUs();

            ListenableFuture<List<Object>> allFutures = Futures.allAsList(trackGroupsFuture, timelineFuture, durationUsFuture);
            Executor executor = Executors.newSingleThreadExecutor();
            Futures.addCallback(allFutures, new FutureCallback<>() {
                @Override
                public void onSuccess(List<Object> result) {
                    handleMetadata(
                            Futures.getUnchecked(trackGroupsFuture),
                            Futures.getUnchecked(timelineFuture),
                            Futures.getUnchecked(durationUsFuture));
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    handleFailure(t);
                }
            }, executor);
        }
    }
    // [END android_media3_inspector_MetadataRetriever_java]

    // [START android_media3_inspector_FrameExtractor_java]
    public void extractFrame(Context context, MediaItem mediaItem) {
        try (FrameExtractor frameExtractor = new FrameExtractor.Builder(context, mediaItem).build()) {
            ListenableFuture<FrameExtractor.Frame> frameFuture = frameExtractor.getFrame(5000L);

            Executor executor = Executors.newSingleThreadExecutor();
            Futures.addCallback(frameFuture, new FutureCallback<Object>() {
                @OptIn(markerClass = UnstableApi.class)
                @Override
                public void onSuccess(Object result) {
                    long presentationTimeMs = Futures.getUnchecked(frameFuture).presentationTimeMs;
                    Log.d(TAG, "Extracted frame at " + presentationTimeMs);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    handleFailure(t);
                }
            }, executor);
        }
    }
    // [END android_media3_inspector_FrameExtractor_java]

    // [START android_media3_inspector_MediaExtractorCompat_java]
    public void extractSamples(Context context, String mediaPath) {
        MediaExtractorCompat extractor = new MediaExtractorCompat(context);
        try {
            // 1. Setup the extractor
            extractor.setDataSource(mediaPath);

            // Find and select available tracks
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                extractor.selectTrack(i);
            }

            // 2. Process samples
            ByteBuffer buffer = ByteBuffer.allocate(10 * 1024 * 1024);
            while (true) {
                // Read an encoded sample into the buffer.
                int bytesRead = extractor.readSampleData(buffer, 0);
                if (bytesRead < 0) break;

                // Access sample metadata
                int trackIndex = extractor.getSampleTrackIndex();
                Long presentationTimeUs = extractor.getSampleTime();
                Long sampleSize = extractor.getSampleSize();

                extractor.advance();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            extractor.release(); // 3. Release the extractor
        }
    }
    // [END android_media3_inspector_MediaExtractorCompat_java]

    private void handleMetadata(TrackGroupArray trackGroups, Timeline timeline, Long durationUs) {
        Log.d(TAG, "TrackGroups: " + trackGroups);
        Log.d(TAG, "Timeline: " + timeline);
        Log.d(TAG, "Duration: " + durationUs);
    }

    private void handleFailure(@NonNull Throwable t) {
        Log.e(TAG, "Error retrieving metadata: " + t.getMessage());
    }

}
