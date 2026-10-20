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

package com.example.snippets.ai;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.genai.common.DownloadCallback;
import com.google.mlkit.genai.common.FeatureStatus;
import com.google.mlkit.genai.common.GenAiException;
import com.google.mlkit.genai.prompt.CachedContext;
import com.google.mlkit.genai.prompt.CreateCachedContextRequest;
import com.google.mlkit.genai.prompt.GenerateContentRequest;
import com.google.mlkit.genai.prompt.GenerateContentResponse;
import com.google.mlkit.genai.prompt.Generation;
import com.google.mlkit.genai.prompt.ImagePart;
import com.google.mlkit.genai.prompt.PromptPrefix;
import com.google.mlkit.genai.prompt.TextPart;
import com.google.mlkit.genai.prompt.java.CachesFutures;
import com.google.mlkit.genai.prompt.java.GenerativeModelFutures;

@SuppressWarnings("unused")
public class MLKitPromptAPISnippetsJava {

    private static final String TAG = "MLKitPromptAPISnippetsJava";

    private final GenerativeModelFutures generativeModelFutures =
            GenerativeModelFutures.from(Generation.INSTANCE.getClient());
    private final CachesFutures cachesFutures =
            CachesFutures.from(Generation.INSTANCE.getClient());

    public void createClient() {
        // [START android_snippets_mlkit_get_client_java]
        // Get a GenerativeModel instance
        GenerativeModelFutures generativeModelFutures = GenerativeModelFutures
            .from(Generation.INSTANCE.getClient());
        // [END android_snippets_mlkit_get_client_java]
    }

    public void checkAndDownloadModel(Context context) {
        // [START android_snippets_mlkit_check_and_download_java]
        ListenableFuture<Integer> status = generativeModelFutures.checkStatus();
        Futures.addCallback(generativeModelFutures.checkStatus(), new FutureCallback<>() {
            @Override
            public void onSuccess(Integer featureStatus) {
                switch (featureStatus) {
                    case FeatureStatus.AVAILABLE -> {
                        // Gemini Nano currently downloaded and available to use on this device
                    }
                    case FeatureStatus.UNAVAILABLE -> {
                        // Gemini Nano not supported on this device or device hasn't fetched the latest configuration to support it
                    }
                    case FeatureStatus.DOWNLOADING -> {
                        // Gemini Nano currently being downloaded
                    }
                    case FeatureStatus.DOWNLOADABLE -> {
                        generativeModelFutures.download(new DownloadCallback() {
                            @Override
                            public void onDownloadStarted(long l) {
                                Log.d(TAG, "starting download for Gemini Nano");
                            }
                            @Override
                            public void onDownloadProgress(long l) {
                                Log.d(TAG, "Nano " + l + " bytes downloaded");
                            }
                            @Override
                            public void onDownloadCompleted() {
                                Log.d(TAG, "Gemini Nano download complete");
                            }
                            @Override
                            public void onDownloadFailed(@NonNull GenAiException e) {
                                Log.e(TAG, "Nano download failed: " + e.getMessage());
                            }
                        });
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Throwable t) {
                // Failed to check status
            }
        }, ContextCompat.getMainExecutor(context));
        // [END android_snippets_mlkit_check_and_download_java]
    }

    public void generateTextOnly() throws Exception {
        // [START android_snippets_mlkit_text_only_java]
        GenerateContentResponse response = generativeModelFutures.generateContent(
            new GenerateContentRequest.Builder(
                new TextPart("Write a 3 sentence story about a magical dog."))
                .build())
            .get();
        // [END android_snippets_mlkit_text_only_java]
    }

    public void generateTextWithParams() throws Exception {
        // [START android_snippets_mlkit_text_with_params_java]
        GenerateContentRequest.Builder requestBuilder =
                new GenerateContentRequest.Builder(
                        new TextPart("Write a 3 sentence story about a magical dog."));
        requestBuilder.setTemperature(.2f);
        requestBuilder.setTopK(10);
        requestBuilder.setCandidateCount(3);

        GenerateContentResponse response =
                generativeModelFutures.generateContent(requestBuilder.build()).get();
        // [END android_snippets_mlkit_text_with_params_java]
    }

    public void generateMultimodal(Bitmap bitmap) throws Exception {
        // [START android_snippets_mlkit_multimodal_java]
        GenerateContentResponse response = generativeModelFutures.generateContent(
            new GenerateContentRequest.Builder(
                new ImagePart(bitmap),
                new TextPart("textPrompt"))
                // optional parameters
                .build())
            .get();
        // [END android_snippets_mlkit_multimodal_java]
    }

    public void generateNonStreaming() throws Exception {
        // [START android_snippets_mlkit_non_streaming_java]
        GenerateContentResponse response = generativeModelFutures.generateContent(
                new GenerateContentRequest.Builder(
                        new TextPart("Write a 3 sentence story about a magical dog."))
                        .build())
                .get();
        // [END android_snippets_mlkit_non_streaming_java]
    }

    public void generateStreaming() {
        // [START android_snippets_mlkit_streaming_java]
        // Streaming inference
        StringBuilder fullResponse = new StringBuilder();
        generativeModelFutures.generateContent(new GenerateContentRequest.Builder(
            (new TextPart("Write a 3 sentence story about a magical dog"))).build(),
                chunk -> {
                    Log.d(TAG, chunk);
                    fullResponse.append(chunk);
                });
        // [END android_snippets_mlkit_streaming_java]
    }

    public void implicitPrefixCaching() throws Exception {
        // [START android_snippets_mlkit_prefix_caching_implicit_java]
        String promptPrefix = "Reverse the given sentence: ";
        String dynamicSuffix = "Hello World";

        GenerateContentRequest.Builder requestBuilder =
            new GenerateContentRequest.Builder(new TextPart(dynamicSuffix));
        requestBuilder.setPromptPrefix(new PromptPrefix(promptPrefix));

        GenerateContentResponse response =
            generativeModelFutures.generateContent(requestBuilder.build()).get();
        // [END android_snippets_mlkit_prefix_caching_implicit_java]
    }

    public void explicitPrefixCachingCreateAndUse() throws Exception {
        // [START android_snippets_mlkit_prefix_caching_explicit_create_java]
        String cacheName = "my_cache";
        String promptPrefix = "Reverse the given sentence: ";
        String dynamicSuffix = "Hello World";

        // Create a cache
        CachedContext cache = cachesFutures.create(
            new CreateCachedContextRequest.Builder(cacheName, new PromptPrefix(promptPrefix))
                .build())
            .get();

        // Run inference with the cache
        GenerateContentRequest.Builder requestBuilder =
            new GenerateContentRequest.Builder(new TextPart(dynamicSuffix));
        requestBuilder.setCachedContextName(cache.getName());

        GenerateContentResponse response =
            generativeModelFutures.generateContent(requestBuilder.build()).get();
        // [END android_snippets_mlkit_prefix_caching_explicit_create_java]
    }

    public void explicitPrefixCachingManage() throws Exception {
        // [START android_snippets_mlkit_prefix_caching_explicit_manage_java]
        String cacheName = "my_cache";

        // Query pre-created caches
        for (CachedContext cache : cachesFutures.list().get()) {
            // Do something with cache
        }

        // Get specific cache
        CachedContext cache = cachesFutures.get(cacheName).get();

        // Delete a pre-created cache
        cachesFutures.delete(cacheName);
        // [END android_snippets_mlkit_prefix_caching_explicit_manage_java]
    }
}
