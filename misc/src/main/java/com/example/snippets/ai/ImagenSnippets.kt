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
@file:OptIn(PublicPreviewAPI::class)

package com.example.snippets.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagenAspectRatio
import com.google.firebase.ai.type.ImagenGenerationConfig
import com.google.firebase.ai.type.ImagenImageFormat
import com.google.firebase.ai.type.ImagenPersonFilterLevel
import com.google.firebase.ai.type.ImagenSafetyFilterLevel
import com.google.firebase.ai.type.ImagenSafetySettings
import com.google.firebase.ai.type.PublicPreviewAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private object ImagenModelConfiguration {
    // [START android_imagen_model_configuration]
    val config = ImagenGenerationConfig(
        numberOfImages = 2,
        aspectRatio = ImagenAspectRatio.LANDSCAPE_16x9,
        imageFormat = ImagenImageFormat.jpeg(compressionQuality = 100),
        addWatermark = false,
    )

    // Initialize the Gemini Developer API backend service
    // For Vertex AI use Firebase.ai(backend = GenerativeBackend.vertexAI())
    val model = Firebase.ai(backend = GenerativeBackend.googleAI()).imagenModel(
        modelName = "imagen-4.0-generate-001",
        generationConfig = config,
        safetySettings = ImagenSafetySettings(
            safetyFilterLevel = ImagenSafetyFilterLevel.BLOCK_LOW_AND_ABOVE,
            personFilterLevel = ImagenPersonFilterLevel.BLOCK_ALL
        ),
    )
    // [END android_imagen_model_configuration]
}

private fun generateImagesWithImagen(scope: CoroutineScope) {
    val model = ImagenModelConfiguration.model
    scope.launch {
        // [START android_imagen_generate_images]
        val imageResponse = model.generateImages(
            prompt = "A hyper realistic picture of a t-rex with a blue bagpack in a prehistoric forest",
        )
        val image = imageResponse.images.first()
        val bitmapImage = image.asBitmap()
        // [END android_imagen_generate_images]
    }
}
