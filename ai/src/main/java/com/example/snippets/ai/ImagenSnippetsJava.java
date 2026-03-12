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

import android.graphics.Bitmap;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.java.ImagenModelFutures;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.ai.type.ImagenAspectRatio;
import com.google.firebase.ai.type.ImagenGenerationConfig;
import com.google.firebase.ai.type.ImagenGenerationResponse;
import com.google.firebase.ai.type.ImagenImageFormat;
import com.google.firebase.ai.type.ImagenInlineImage;
import com.google.firebase.ai.type.ImagenPersonFilterLevel;
import com.google.firebase.ai.type.ImagenSafetyFilterLevel;
import com.google.firebase.ai.type.ImagenSafetySettings;
import com.google.firebase.ai.type.PublicPreviewAPI;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
@PublicPreviewAPI
final class ImagenSnippetsJava {

    private ImagenSnippetsJava() {}

    static final class ImagenModelConfigurationJava {
        public static ImagenModelFutures model;
    }

    static {
        // [START android_imagen_model_configuration_java]
        ImagenGenerationConfig config = new ImagenGenerationConfig.Builder()
                .setNumberOfImages(2)
                .setAspectRatio(ImagenAspectRatio.LANDSCAPE_16x9)
                .setImageFormat(ImagenImageFormat.jpeg(100))
                .setAddWatermark(false)
                .build();

        // For Vertex AI use Firebase.ai(backend = GenerativeBackend.vertexAI())
        ImagenModelFutures model = ImagenModelFutures.from(
                FirebaseAI.getInstance(GenerativeBackend.googleAI()).imagenModel(
                        "imagen-4.0-generate-001",
                        config,
                        new ImagenSafetySettings(
                                ImagenSafetyFilterLevel.BLOCK_LOW_AND_ABOVE,
                                ImagenPersonFilterLevel.BLOCK_ALL))
        );
        // [END android_imagen_model_configuration_java]
        ImagenModelConfigurationJava.model = model;
    }

    public static void generateImagesWithImagen(Executor executor) {
        ImagenModelFutures model = ImagenModelConfigurationJava.model;
        // [START android_imagen_generate_images_java]
        ListenableFuture<ImagenGenerationResponse<ImagenInlineImage>> futureResponse =
                model.generateImages(
                        "A hyper realistic picture of a t-rex with a blue bagpack in a prehistoric forest");

        try {
            ImagenGenerationResponse<ImagenInlineImage> imageResponse = futureResponse.get();
            List<ImagenInlineImage> images = null;
            if (imageResponse != null) {
                images = imageResponse.getImages();
            }
            if (images != null && !images.isEmpty()) {
                ImagenInlineImage image = images.get(0);
                Bitmap bitmapImage = image.asBitmap();
                // Use bitmapImage
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        // [END android_imagen_generate_images_java]
    }
}
