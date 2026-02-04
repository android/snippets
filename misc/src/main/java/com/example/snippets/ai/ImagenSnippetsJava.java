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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.google.firebase.ai.FirebaseAi;
import com.google.firebase.ai.ImagenModel;
import com.google.firebase.ai.type.Dimensions;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.ai.type.ImagenAspectRatio;
import com.google.firebase.ai.type.ImagenBackgroundMask;
import com.google.firebase.ai.type.ImagenControlReference;
import com.google.firebase.ai.type.ImagenControlType;
import com.google.firebase.ai.type.ImagenEditMode;
import com.google.firebase.ai.type.ImagenEditingConfig;
import com.google.firebase.ai.type.ImagenGenerationConfig;
import com.google.firebase.ai.type.ImagenGenerationResponse;
import com.google.firebase.ai.type.ImagenImageFormat;
import com.google.firebase.ai.type.ImagenImagePlacement;
import com.google.firebase.ai.type.ImagenInlineImage;
import com.google.firebase.ai.type.ImagenMaskReference;
import com.google.firebase.ai.type.ImagenPersonFilterLevel;
import com.google.firebase.ai.type.ImagenRawImage;
import com.google.firebase.ai.type.ImagenReferenceImage;
import com.google.firebase.ai.type.ImagenSafetyFilterLevel;
import com.google.firebase.ai.type.ImagenSafetySettings;
import com.google.firebase.ai.type.ImagenStyleReference;
import com.google.firebase.ai.type.ImagenSubjectReference;
import com.google.firebase.ai.type.ImagenSubjectReferenceType;
import com.google.android.gms.tasks.Task;
import java.util.Arrays;
import java.util.List;

public class ImagenSnippetsJava {

    private void imagenModelConfiguration() {
        // [START android_imagen_model_configuration_java]
        ImagenGenerationConfig config = new ImagenGenerationConfig.Builder()
                .setNumberOfImages(2)
                .setAspectRatio(ImagenAspectRatio.LANDSCAPE_16X9)
                .setImageFormat(ImagenImageFormat.jpeg(100))
                .setAddWatermark(false)
                .build();

        // Initialize the Gemini Developer API backend service
        // For Vertex AI use FirebaseAi.getInstance(GenerativeBackend.vertexAI())
        ImagenModel model = FirebaseAi.getInstance(GenerativeBackend.googleAI()).getImagenModel(
                "imagen-4.0-generate-001",
                config,
                new ImagenSafetySettings.Builder()
                        .setSafetyFilterLevel(ImagenSafetyFilterLevel.BLOCK_LOW_AND_ABOVE)
                        .setPersonFilterLevel(ImagenPersonFilterLevel.BLOCK_ALL)
                        .build()
        );
        // [END android_imagen_model_configuration_java]
    }

    private void imagenVertexAIModelConfiguration() {
        // [START android_imagen_vertex_model_configuration_java]
        ImagenModel imagenModel = FirebaseAi.getInstance(GenerativeBackend.vertexAI())
                .getImagenModel("imagen-3.0-capability-001");
        // [END android_imagen_vertex_model_configuration_java]
    }

    private void generateImages(ImagenModel model) {
        // [START android_imagen_generate_images_java]
        Task<ImagenGenerationResponse> task = model.generateImages(
                "A hyper realistic picture of a t-rex with a blue bagpack in a prehistoric forest"
        );

        task.addOnSuccessListener(imageResponse -> {
            if (!imageResponse.getImages().isEmpty()) {
                ImagenInlineImage image = imageResponse.getImages().get(0);
                Bitmap bitmapImage = image.asBitmap();
            }
        });
        // [END android_imagen_generate_images_java]
    }

    // [START android_imagen_inpaint_insertion_java]
    public Task<ImagenGenerationResponse> insertFlowersIntoImage(
            ImagenModel model,
            Bitmap originalImage,
            ImagenMaskReference mask) {
        String prompt = "a vase of flowers";

        // Pass the original image, a mask, the prompt, and an editing configuration.
        List<ImagenReferenceImage> referenceImages = Arrays.asList(
                new ImagenRawImage(ImagenInlineImage.fromBitmap(originalImage)),
                mask
        );

        return model.editImage(
                referenceImages,
                prompt,
                // Define the editing configuration for inpainting and insertion.
                new ImagenEditingConfig.Builder()
                        .setEditMode(ImagenEditMode.INPAINT_INSERTION)
                        .build()
        );
    }
    // [END android_imagen_inpaint_insertion_java]

    // [START android_imagen_inpaint_removal_java]
    public Task<ImagenGenerationResponse> removeBallFromImage(
            ImagenModel model,
            Bitmap originalImage,
            ImagenMaskReference mask) {

        // Optional: provide the prompt describing the content to be removed.
        String prompt = "a ball";

        // Pass the original image, a mask, the prompt, and an editing configuration.
        List<ImagenReferenceImage> referenceImages = Arrays.asList(
                new ImagenRawImage(ImagenInlineImage.fromBitmap(originalImage)),
                mask
        );

        return model.editImage(
                referenceImages,
                prompt,
                // Define the editing configuration for inpainting and removal.
                new ImagenEditingConfig.Builder()
                        .setEditMode(ImagenEditMode.INPAINT_REMOVAL)
                        .build()
        );
    }
    // [END android_imagen_inpaint_removal_java]

    // [START android_imagen_editing_create_mask_java]
    private Bitmap createMaskBitmap(Bitmap sourceBitmap, List<Path> paths) {
        Bitmap maskBitmap = Bitmap.createBitmap(
                sourceBitmap.getWidth(),
                sourceBitmap.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(maskBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(70f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);

        // iterating straight over Path objects since we assume standard Android Paths
        for (Path path : paths) {
            canvas.drawPath(path, paint);
        }

        return maskBitmap;
    }
    // [END android_imagen_editing_create_mask_java]

    // [START android_imagen_expand_image_java]
    public Task<ImagenGenerationResponse> expandImage(
            Bitmap originalImage,
            ImagenModel imagenModel) {

        // Optionally describe what should appear in the expanded area.
        String prompt = "a sprawling sandy beach next to the ocean";

        return imagenModel.outpaintImage(
                ImagenInlineImage.fromBitmap(originalImage),
                new Dimensions(1024, 1024),
                prompt,
                ImagenImagePlacement.LEFT_CENTER
        );
    }
    // [END android_imagen_expand_image_java]

    // [START android_imagen_replace_background_java]
    public Task<ImagenGenerationResponse> replaceBackground(
            ImagenModel model,
            Bitmap originalImage) {
        // Provide the prompt describing the new background.
        String prompt = "space background";

        // Pass the original image, a mask, the prompt, and an editing configuration.
        List<ImagenReferenceImage> referenceImages = Arrays.asList(
                new ImagenRawImage(ImagenInlineImage.fromBitmap(originalImage)),
                new ImagenBackgroundMask()
        );

        return model.editImage(
                referenceImages,
                prompt,
                new ImagenEditingConfig.Builder()
                        .setEditMode(ImagenEditMode.INPAINT_INSERTION)
                        .build()
        );
    }
    // [END android_imagen_replace_background_java]

    // [START android_imagen_customize_subject_java]
    public Task<ImagenGenerationResponse> customizeCatImage(
            ImagenModel model,
            Bitmap referenceCatImage) {

        // Define the subject reference using the reference image.
        ImagenSubjectReference subjectReference = new ImagenSubjectReference.Builder(
                ImagenInlineImage.fromBitmap(referenceCatImage))
                .setReferenceId(1)
                .setDescription("cat")
                .setSubjectType(ImagenSubjectReferenceType.ANIMAL)
                .build();

        // Provide a prompt that describes the final image.
        // The "[1]" links the prompt to the subject reference with ID 1.
        String prompt = "A cat[1] flying through outer space";

        // Use the editImage API to perform the subject customization.
        return model.editImage(
                Arrays.asList(subjectReference),
                prompt,
                new ImagenEditingConfig.Builder()
                        .setEditSteps(50) // Number of editing steps, a higher value can improve quality
                        .build()
        );
    }
    // [END android_imagen_customize_subject_java]

    // [START android_imagen_customize_control_java]
    public Task<ImagenGenerationResponse> customizeCatImageByControl(
            ImagenModel model,
            Bitmap referenceImage) {

        // Define the subject reference using the reference image.
        ImagenControlReference controlReference = new ImagenControlReference.Builder(
                ImagenInlineImage.fromBitmap(referenceImage))
                .setReferenceId(1)
                .setType(ImagenControlType.SCRIBBLE)
                .build();

        String prompt = "A cat flying through outer space arranged like the scribble map[1]";

        return model.editImage(
                Arrays.asList(controlReference),
                prompt,
                new ImagenEditingConfig.Builder()
                        .setEditSteps(50)
                        .build()
        );
    }
    // [END android_imagen_customize_control_java]

    // [START android_imagen_customize_style_java]
    public Task<ImagenGenerationResponse> customizeImageByStyle(
            ImagenModel model,
            Bitmap referenceVanGoghImage) {

        // Define the style reference using the reference image.
        ImagenStyleReference styleReference = new ImagenStyleReference.Builder(
                ImagenInlineImage.fromBitmap(referenceVanGoghImage))
                .setReferenceId(1)
                .setDescription("Van Gogh style")
                .build();

        // Provide a prompt that describes the final image.
        // The "1" links the prompt to the style reference with ID 1.
        String prompt = "A cat flying through outer space, in the Van Gogh style[1]";

        // Use the editImage API to perform the style customization.
        return model.editImage(
                Arrays.asList(styleReference),
                prompt,
                new ImagenEditingConfig.Builder()
                        .setEditSteps(50) // Number of editing steps, a higher value can improve quality
                        .build()
        );
    }
    // [END android_imagen_customize_style_java]
}

