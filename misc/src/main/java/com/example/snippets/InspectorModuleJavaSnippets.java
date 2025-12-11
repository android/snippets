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

import static android.media.MediaMetadataRetriever.METADATA_KEY_MIMETYPE;
import static android.media.MediaMetadataRetriever.OPTION_CLOSEST;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.Format;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Timeline;
import androidx.media3.common.TrackGroup;
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

    // [START android_dev_retriever_media3_java]
    public void retrieveMetadata(Context context, MediaItem mediaItem) {
        try (MetadataRetriever retriever = new MetadataRetriever.Builder(context, mediaItem).build()) {
            ListenableFuture<TrackGroupArray> trackGroupsFuture = retriever.retrieveTrackGroups();
            ListenableFuture<Timeline> timelineFuture = retriever.retrieveTimeline();
            ListenableFuture<Long> durationUsFuture = retriever.retrieveDurationUs();

            ListenableFuture<List<Object>> allFutures = Futures.allAsList(trackGroupsFuture, timelineFuture, durationUsFuture);
            Executor executor = Executors.newSingleThreadExecutor();
            Futures.addCallback(allFutures, new FutureCallback<>() {
                @Override
                public void onSuccess(List<Object> result) {
                    handleMetadata(
                            Futures.getUnchecked(trackGroupsFuture),
                            Futures.getUnchecked(timelineFuture),
                            Futures.getUnchecked(durationUsFuture)
                    );
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    handleFailure(t);
                }
            }, executor);
        }
    }
    // [END android_dev_retriever_media3_java]

    // [START android_migration_retriever_platform_java]
    public void retrieveMetadataPlatform(String mediaPath) {
        try (MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
            retriever.setDataSource(mediaPath);
            String mimeType = retriever.extractMetadata(METADATA_KEY_MIMETYPE);
            Log.d(TAG, "MIME type: " + mimeType);
            retriever.release();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // [END android_migration_retriever_platform_java]

    // [START android_migration_retriever_media3_java]
    public void retrieveMetadataMedia3(Context context, MediaItem mediaItem) {
        try (MetadataRetriever retriever = new MetadataRetriever.Builder(context, mediaItem).build()) {
            ListenableFuture<TrackGroupArray> trackGroupsFuture = retriever.retrieveTrackGroups();

            Executor executor = Executors.newSingleThreadExecutor();
            Futures.addCallback(trackGroupsFuture, new FutureCallback<Object>() {
                @Override
                public void onSuccess(Object trackGroupsObject) {
                    TrackGroupArray trackGroups = (TrackGroupArray) trackGroupsObject;
                    for (int i = 0; i < trackGroups.length; i++) {
                        TrackGroup trackGroup = trackGroups.get(i);
                        for (int j = 0; j < trackGroup.length; j++) {
                            Format format = trackGroup.getFormat(j);
                            String mimeType = format.containerMimeType;
                            Log.d(TAG, "MIME type: " + mimeType);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "Error retrieving metadata: " + t.getMessage());
                }
            }, executor);
        }
    }
    // [END android_migration_retriever_media3_java]

    // [START android_dev_frame_media3_java]
    public void extractFrame(Context context, MediaItem mediaItem) {
        try (FrameExtractor frameExtractor = new FrameExtractor.Builder(context, mediaItem).build()) {
            ListenableFuture<FrameExtractor.Frame> frameFuture = frameExtractor.getFrame(5000L);

            Executor executor = Executors.newSingleThreadExecutor();
            Futures.addCallback(frameFuture, new FutureCallback<Object>() {
                @Override
                public void onSuccess(Object frameObject) {
                    FrameExtractor.Frame frame = (FrameExtractor.Frame) frameObject;
                    long presentationTimeMs = frame.presentationTimeMs;
                    Log.d(TAG, "Extracted frame at " + presentationTimeMs);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    handleFailure(t);
                }
            }, executor);
        }
    }
    // [END android_dev_frame_media3_java]

    // [START android_migration_frame_platform_java]
    public Bitmap extractFramePlatform(String mediaPath, Long frameTimeMs) {
        Bitmap bitmap;
        try (MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
            retriever.setDataSource(mediaPath);
            bitmap = retriever.getFrameAtTime(frameTimeMs * 1000L, // Time is in microseconds
                    OPTION_CLOSEST);
            Log.d(TAG, "Extracted frame " + bitmap);
            retriever.release();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bitmap;
    }
    // [END android_migration_frame_platform_java]

    // [START android_dev_extractor_media3_java]
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
            // 3. Release the extractor
            extractor.release();
        }
    }
    // [END android_dev_extractor_media3_java]

    private void handleMetadata(TrackGroupArray trackGroups, Timeline timeline, Long durationUs) {
        Log.d(TAG, "TrackGroups: " + trackGroups);
        Log.d(TAG, "Timeline: " + timeline);
        Log.d(TAG, "Duration: " + durationUs);
    }

    private void handleFailure(@NonNull Throwable t) {
        Log.e(TAG, "Error retrieving metadata: " + t.getMessage());
    }

}
