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

package com.example.snippets.ai;

import android.app.Application;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.snippets.R;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.ChatFutures;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.ai.type.ImagePart;
import com.google.firebase.ai.type.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
public final class GeminiDeveloperApiSnippetsJava {

    private static final String TAG = "GeminiDeveloperApiSnippetsJava";

    private GeminiDeveloperApiSnippetsJava() {}

    static final class GeminiDeveloperApi25FlashModelConfigurationJava {
        public static GenerativeModelFutures model;

        static {
            // [START android_gemini_developer_api_gemini_25_flash_model_java]
            GenerativeModel firebaseAI = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                    .generativeModel("gemini-2.5-flash");

            GenerativeModelFutures model = GenerativeModelFutures.from(firebaseAI);
            // [END android_gemini_developer_api_gemini_25_flash_model_java]
            GeminiDeveloperApi25FlashModelConfigurationJava.model = model;
        }
    }

    static final class Gemini25FlashImagePreviewModelConfigurationJava {
        public static GenerativeModelFutures model;

        static {
            // [START android_gemini_developer_api_gemini_25_flash_image_model_java]
            GenerativeModel firebaseAI = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                    .generativeModel("gemini-2.5-flash");

            GenerativeModelFutures model = GenerativeModelFutures.from(firebaseAI);
            // [END android_gemini_developer_api_gemini_25_flash_image_model_java]
            Gemini25FlashImagePreviewModelConfigurationJava.model = model;
        }

    }

    public static void textOnlyInput(Executor executor) {
        GenerativeModelFutures model = GeminiDeveloperApi25FlashModelConfigurationJava.model;
        // [START android_gemini_developer_api_text_only_input_java]
        Content prompt = new Content.Builder()
                .addText("Write a story about a magic backpack.")
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
        // [END android_gemini_developer_api_text_only_input_java]
    }

    public static void textAndImageInput(Executor executor, Bitmap bitmap) {
        GenerativeModelFutures model = GeminiDeveloperApi25FlashModelConfigurationJava.model;
        // [START android_gemini_developer_api_multimodal_input_java]
        Content content = new Content.Builder()
                .addImage(bitmap)
                .addText("what is the object in the picture?")
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
        // [END android_gemini_developer_api_multimodal_input_java]
    }

    public static void textAndAudioInput(Executor executor, Application applicationContext, Uri audioUri) {
        GenerativeModelFutures model = GeminiDeveloperApi25FlashModelConfigurationJava.model;
        // [START android_gemini_developer_api_multimodal_audio_input_java]
        ContentResolver resolver = applicationContext.getContentResolver();

        try (InputStream stream = resolver.openInputStream(audioUri)) {
            File audioFile = new File(new URI(audioUri.toString()));
            int audioSize = (int) audioFile.length();
            byte[] audioBytes = new byte[audioSize];
            if (stream != null) {
                stream.read(audioBytes, 0, audioBytes.length);
                stream.close();

                // Provide a prompt that includes audio specified earlier and text
                Content prompt = new Content.Builder()
                        .addInlineData(audioBytes, "audio/mpeg")  // Specify the appropriate audio MIME type
                        .addText("Transcribe what's said in this audio recording.")
                        .build();

                // To generate text output, call `generateContent` with the prompt
                ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
                Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String text = result.getText();
                        Log.d(TAG, (text == null) ? "" : text);
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, "Failed to generate a response", t);
                    }
                }, executor);
            } else {
                Log.e(TAG, "Error getting input stream for file.");
                // Handle the error appropriately
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to read the audio file", e);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Invalid audio file", e);
        }
        // [END android_gemini_developer_api_multimodal_audio_input_java]
    }

    public static void textAndVideoInput(Executor executor, Application applicationContext, Uri videoUri) {
        GenerativeModelFutures model = GeminiDeveloperApi25FlashModelConfigurationJava.model;
        // [START android_gemini_developer_api_multimodal_video_input_java]
        ContentResolver resolver = applicationContext.getContentResolver();

        try (InputStream stream = resolver.openInputStream(videoUri)) {
            File videoFile = new File(new URI(videoUri.toString()));
            int videoSize = (int) videoFile.length();
            byte[] videoBytes = new byte[videoSize];
            if (stream != null) {
                stream.read(videoBytes, 0, videoBytes.length);
                stream.close();

                // Provide a prompt that includes video specified earlier and text
                Content prompt = new Content.Builder()
                        .addInlineData(videoBytes, "video/mp4")
                        .addText("Describe the content of this video")
                        .build();

                // To generate text output, call generateContent with the prompt
                ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
                Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = result.getText();
                        System.out.println(resultText);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                }, executor);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // [END android_gemini_developer_api_multimodal_video_input_java]
    }

    public static void multiTurnChat(Executor executor) {
        GenerativeModelFutures model = GeminiDeveloperApi25FlashModelConfigurationJava.model;
        // [START android_gemini_developer_api_multiturn_chat_java]
        Content.Builder userContentBuilder = new Content.Builder();
        userContentBuilder.setRole("user");
        userContentBuilder.addText("Hello, I have 2 dogs in my house.");
        Content userContent = userContentBuilder.build();

        Content.Builder modelContentBuilder = new Content.Builder();
        modelContentBuilder.setRole("model");
        modelContentBuilder.addText("Great to meet you. What would you like to know?");
        Content modelContent = modelContentBuilder.build();

        List<Content> history = Arrays.asList(userContent, modelContent);

        // Initialize the chat
        ChatFutures chat = model.startChat(history);

        // Create a new user message
        Content.Builder messageBuilder = new Content.Builder();
        messageBuilder.setRole("user");
        messageBuilder.addText("How many paws are in my house?");

        Content message = messageBuilder.build();

        // Send the message
        ListenableFuture<GenerateContentResponse> response = chat.sendMessage(message);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                System.out.println(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
        // [END android_gemini_developer_api_multiturn_chat_java]
    }

    public static void generateImageFromText(Executor executor) {
        GenerativeModelFutures model = Gemini25FlashImagePreviewModelConfigurationJava.model;
        // [START android_gemini_developer_api_generate_image_from_text_java]
        // Provide a text prompt instructing the model to generate an image
        Content prompt = new Content.Builder()
                .addText("Generate an image of the Eiffel Tower with fireworks in the background.")
                .build();
        // To generate an image, call `generateContent` with the text input
        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                // iterate over all the parts in the first candidate in the result object
                for (Part part : result.getCandidates().get(0).getContent().getParts()) {
                    if (part instanceof ImagePart) {
                        ImagePart imagePart = (ImagePart) part;
                        // The returned image as a bitmap
                        Bitmap generatedImageAsBitmap = imagePart.getImage();
                        break;
                    }
                }
            }
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
        // [END android_gemini_developer_api_generate_image_from_text_java]
    }

    public static void editImage(Executor executor, Resources resources) {
        GenerativeModelFutures model = Gemini25FlashImagePreviewModelConfigurationJava.model;
        // [START android_gemini_developer_api_edit_image_java]
        // Provide an image for the model to edit
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.scones);
        // Provide a text prompt instructing the model to edit the image
        Content promptcontent = new Content.Builder()
                .addImage(bitmap)
                .addText("Edit this image to make it look like a cartoon")
                .build();
        // To edit the image, call `generateContent` with the prompt (image and text input)
        ListenableFuture<GenerateContentResponse> response = model.generateContent(promptcontent);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                // iterate over all the parts in the first candidate in the result object
                for (Part part : result.getCandidates().get(0).getContent().getParts()) {
                    if (part instanceof ImagePart) {
                        ImagePart imagePart = (ImagePart) part;
                        Bitmap generatedImageAsBitmap = imagePart.getImage();
                        break;
                    }
                }
            }
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
        // [END android_gemini_developer_api_edit_image_java]
    }

    public static void editImageWithChat(Executor executor, Resources resources) {
        GenerativeModelFutures model = Gemini25FlashImagePreviewModelConfigurationJava.model;
        // [START android_gemini_developer_api_edit_image_chat_java]
        // Provide an image for the model to edit
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.scones);
        // Initialize the chat
        ChatFutures chat = model.startChat();
        // Create the initial prompt instructing the model to edit the image
        Content prompt = new Content.Builder()
                .setRole("user")
                .addImage(bitmap)
                .addText("Edit this image to make it look like a cartoon")
                .build();
        // To generate an initial response, send a user message with the image and text prompt
        ListenableFuture<GenerateContentResponse> response = chat.sendMessage(prompt);
        // Extract the image from the initial response
        ListenableFuture<Bitmap> initialRequest = Futures.transform(response,
                result -> {
                    for (Part part : result.getCandidates().get(0).getContent().getParts()) {
                        if (part instanceof ImagePart) {
                            ImagePart imagePart = (ImagePart) part;
                            return imagePart.getImage();
                        }
                    }
                    return null;
                }, executor);
        // Follow up requests do not need to specify the image again
        ListenableFuture<GenerateContentResponse> modelResponseFuture = Futures.transformAsync(
                initialRequest,
                generatedImage -> {
                    Content followUpPrompt = new Content.Builder()
                            .addText("But make it old-school line drawing style")
                            .build();
                    return chat.sendMessage(followUpPrompt);
                }, executor);
        // Add a final callback to check the reworked image
        Futures.addCallback(modelResponseFuture, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                for (Part part : result.getCandidates().get(0).getContent().getParts()) {
                    if (part instanceof ImagePart) {
                        ImagePart imagePart = (ImagePart) part;
                        Bitmap generatedImageAsBitmap = imagePart.getImage();
                        break;
                    }
                }
            }
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
        // [END android_gemini_developer_api_edit_image_chat_java]
    }
}
