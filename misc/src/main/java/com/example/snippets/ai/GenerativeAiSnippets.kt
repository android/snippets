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

package com.example.snippets.ai

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object GeminiDeveloperApi25FlashModelConfiguration {
    // [START firebase_ai_generative_backend_configuration]
    // Start by instantiating a GenerativeModel and specifying the model name:
    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")
    // [END firebase_ai_generative_backend_configuration]
}

object Gemini25FlashImagePreviewModelConfiguration {
    // [START firebase_ai_generative_image_model_configuration]
    val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
        modelName = "gemini-2.5-flash-image-preview",
        // Configure the model to respond with text and images (required)
        generationConfig = generationConfig {
            responseModalities = listOf(
                ResponseModality.TEXT,
                ResponseModality.IMAGE
            )
        }
    )
    // [END firebase_ai_generative_image_model_configuration]
}

fun textOnlyInput(scope: CoroutineScope) {
    val model = GeminiDeveloperApi25FlashModelConfiguration.model
    // [START firebase_ai_text_only_input]
    scope.launch {
        val response = model.generateContent("Write a story about a magic backpack.")
    }
    // [END firebase_ai_text_only_input]
}

fun textAndImageInput(scope: CoroutineScope, bitmap: Bitmap) {
    val model = GeminiDeveloperApi25FlashModelConfiguration.model
    // [START firebase_ai_multimodal_input]
    scope.launch {
        val response = model.generateContent(
            content {
                image(bitmap)
                text("what is the object in the picture?")
            }
        )
    }
    // [END firebase_ai_multimodal_input]
}

fun textAndAudioInput(
    scope: CoroutineScope,
    contentResolver: ContentResolver,
    audioUri: Uri
) {
    val model = GeminiDeveloperApi25FlashModelConfiguration.model
    // [START firebase_ai_multimodal_audio_input]
    scope.launch {
        contentResolver.openInputStream(audioUri).use { stream ->
            stream?.let {
                val bytes = it.readBytes()

                val prompt = content {
                    inlineData(bytes, "audio/mpeg") // Specify the appropriate audio MIME type
                    text("Transcribe this audio recording.")
                }

                val response = model.generateContent(prompt)
            }
        }
    }
    // [END firebase_ai_multimodal_audio_input]
}

fun textAndVideoInput(
    scope: CoroutineScope,
    contentResolver: ContentResolver,
    videoUri: Uri
) {
    val model = GeminiDeveloperApi25FlashModelConfiguration.model
    // [START firebase_ai_multimodal_video_input]
    scope.launch {
        contentResolver.openInputStream(videoUri).use { stream ->
            stream?.let {
                val bytes = it.readBytes()

                val prompt = content {
                    inlineData(bytes, "video/mp4") // Specify the appropriate video MIME type
                    text("Describe the content of this video")
                }

                val response = model.generateContent(prompt)
            }
        }
    }
    // [END firebase_ai_multimodal_video_input]
}

fun multiTurnChat(scope: CoroutineScope) {
    val model = GeminiDeveloperApi25FlashModelConfiguration.model
    // [START firebase_ai_multiturn_chat]
    val chat = model.startChat(
        history = listOf(
            content(role = "user") { text("Hello, I have 2 dogs in my house.") },
            content(role = "model") { text("Great to meet you. What would you like to know?") }
        )
    )

    scope.launch {
        val response = chat.sendMessage("How many paws are in my house?")
    }
    // [END firebase_ai_multiturn_chat]
}

fun generateImageFromText(scope: CoroutineScope) {
    val model = Gemini25FlashImagePreviewModelConfiguration.model
    // [START firebase_ai_generate_image_from_text]
    scope.launch {
        // Provide a text prompt instructing the model to generate an image
        val prompt =
            "A hyper realistic picture of a t-rex with a blue bag pack roaming a pre-historic forest."
        // To generate image output, call `generateContent` with the text input
        val generatedImageAsBitmap: Bitmap? = model.generateContent(prompt)
            .candidates.first().content.parts.filterIsInstance<ImagePart>()
            .firstOrNull()?.image
    }
    // [END firebase_ai_generate_image_from_text]
}

fun editImage(scope: CoroutineScope, bitmap: Bitmap) {
    val model = Gemini25FlashImagePreviewModelConfiguration.model
    // [START firebase_ai_edit_image]
    scope.launch {
        // Provide a text prompt instructing the model to edit the image
        val prompt = content {
            image(bitmap)
            text("Edit this image to make it look like a cartoon")
        }
        // To edit the image, call `generateContent` with the prompt (image and text input)
        val generatedImageAsBitmap: Bitmap? = model.generateContent(prompt)
            .candidates.first().content.parts.filterIsInstance<ImagePart>().firstOrNull()?.image
        // Handle the generated text and image
    }
    // [END firebase_ai_edit_image]
}

fun editImageWithChat(scope: CoroutineScope, bitmap: Bitmap) {
    val model = Gemini25FlashImagePreviewModelConfiguration.model
    // [START firebase_ai_edit_image_chat]
    scope.launch {
        // Create the initial prompt instructing the model to edit the image
        val prompt = content {
            image(bitmap)
            text("Edit this image to make it look like a cartoon")
        }
        // Initialize the chat
        val chat = model.startChat()
        // To generate an initial response, send a user message with the image and text prompt
        var response = chat.sendMessage(prompt)
        // Inspect the returned image
        var generatedImageAsBitmap: Bitmap? = response
            .candidates.first().content.parts.filterIsInstance<ImagePart>().firstOrNull()?.image
        // Follow up requests do not need to specify the image again
        response = chat.sendMessage("But make it old-school line drawing style")
        generatedImageAsBitmap = response
            .candidates.first().content.parts.filterIsInstance<ImagePart>().firstOrNull()?.image
    }
    // [END firebase_ai_edit_image_chat]
}
