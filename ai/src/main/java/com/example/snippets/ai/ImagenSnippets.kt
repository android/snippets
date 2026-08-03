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

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import com.google.firebase.Firebase
import com.google.firebase.ai.ImagenModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Dimensions
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagenAspectRatio
import com.google.firebase.ai.type.ImagenBackgroundMask
import com.google.firebase.ai.type.ImagenControlReference
import com.google.firebase.ai.type.ImagenControlType
import com.google.firebase.ai.type.ImagenEditMode
import com.google.firebase.ai.type.ImagenEditingConfig
import com.google.firebase.ai.type.ImagenGenerationConfig
import com.google.firebase.ai.type.ImagenGenerationResponse
import com.google.firebase.ai.type.ImagenImageFormat
import com.google.firebase.ai.type.ImagenImagePlacement
import com.google.firebase.ai.type.ImagenInlineImage
import com.google.firebase.ai.type.ImagenMaskReference
import com.google.firebase.ai.type.ImagenPersonFilterLevel
import com.google.firebase.ai.type.ImagenRawImage
import com.google.firebase.ai.type.ImagenSafetyFilterLevel
import com.google.firebase.ai.type.ImagenSafetySettings
import com.google.firebase.ai.type.ImagenStyleReference
import com.google.firebase.ai.type.ImagenSubjectReference
import com.google.firebase.ai.type.ImagenSubjectReferenceType
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.toImagenInlineImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.min
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor

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

private object ImagenVertexAIModelConfiguration {
    // [START android_imagen_vertex_model_configuration]
    val imagenModel = Firebase.ai(backend = GenerativeBackend.vertexAI())
        .imagenModel("imagen-3.0-capability-001")
    // [END android_imagen_vertex_model_configuration]
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

// [START android_imagen_inpaint_insertion]
suspend fun insertFlowersIntoImage(
    model: ImagenModel,
    originalImage: Bitmap,
    mask: ImagenMaskReference
): ImagenGenerationResponse<ImagenInlineImage> {
    val prompt = "a vase of flowers"

    // Pass the original image, a mask, the prompt, and an editing configuration.
    val editedImage = model.editImage(
        referenceImages = listOf(
            ImagenRawImage(originalImage.toImagenInlineImage()),
            mask,
        ),
        prompt = prompt,
        // Define the editing configuration for inpainting and insertion.
        config = ImagenEditingConfig(ImagenEditMode.INPAINT_INSERTION)
    )
    return editedImage
}
// [END android_imagen_inpaint_insertion]

// [START android_imagen_inpaint_removal]
suspend fun removeBallFromImage(
    model: ImagenModel,
    originalImage: Bitmap,
    mask: ImagenMaskReference
): ImagenGenerationResponse<ImagenInlineImage> {

    // Optional: provide the prompt describing the content to be removed.
    val prompt = "a ball"

    // Pass the original image, a mask, the prompt, and an editing configuration.
    val editedImage = model.editImage(
        referenceImages = listOf(
            ImagenRawImage(originalImage.toImagenInlineImage()),
            mask
        ),
        prompt = prompt,
        // Define the editing configuration for inpainting and removal.
        config = ImagenEditingConfig(ImagenEditMode.INPAINT_REMOVAL)
    )

    return editedImage
}
// [END android_imagen_inpaint_removal]

// [START android_imagen_editing_mask_editor]
//import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun ImagenEditingMaskEditor(
    sourceBitmap: Bitmap,
    onMaskFinalized: (Bitmap) -> Unit,
) {

    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            val transformedStart = Offset(
                                (startOffset.x - offsetX) / scale,
                                (startOffset.y - offsetY) / scale,
                            )
                            currentPath = Path().apply { moveTo(transformedStart.x, transformedStart.y) }
                        },
                        onDrag = { change, _ ->
                            currentPath?.let {
                                val transformedChange = Offset(
                                    (change.position.x - offsetX) / scale,
                                    (change.position.y - offsetY) / scale,
                                )
                                it.lineTo(transformedChange.x, transformedChange.y)
                                currentPath = Path().apply { addPath(it) }
                            }
                            change.consume()
                        },
                        onDragEnd = {
                            currentPath?.let { paths.add(it) }
                            currentPath = null
                        },
                    )
                },
        ) {
            Image(
                bitmap = sourceBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val bitmapWidth = sourceBitmap.width.toFloat()
                val bitmapHeight = sourceBitmap.height.toFloat()
                scale = min(canvasWidth / bitmapWidth, canvasHeight / bitmapHeight)
                offsetX = (canvasWidth - bitmapWidth * scale) / 2
                offsetY = (canvasHeight - bitmapHeight * scale) / 2
                withTransform(
                    {
                        translate(left = offsetX, top = offsetY)
                        scale(scale, scale, pivot = Offset.Zero)
                    },
                ) {
                    val strokeWidth = 70f / scale
                    val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    val pathColor = ComposeColor.White.copy(alpha = 0.5f)
                    paths.forEach { path ->
                        drawPath(path = path, color = pathColor, style = stroke)
                    }
                    currentPath?.let { path ->
                        drawPath(path = path, color = pathColor, style = stroke)
                    }
                }
            }
        }
        Button(
            onClick = {
                val maskBitmap = createMaskBitmap(sourceBitmap, paths)
                onMaskFinalized(maskBitmap)
            },
        ) {
            Text("Save mask")
        }
    }
}
// [END android_imagen_editing_mask_editor]

// [START android_imagen_editing_create_mask]
// import android.graphics.Color as AndroidColor
// import android.graphics.Paint

private fun createMaskBitmap(
    sourceBitmap: Bitmap,
    paths: SnapshotStateList<Path>,
): Bitmap {
    val maskBitmap = Bitmap.createBitmap(sourceBitmap.width, sourceBitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(maskBitmap)
    val paint = Paint().apply {
        color = AndroidColor.RED
        strokeWidth = 70f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }
    paths.forEach { path -> canvas.drawPath(path.asAndroidPath(), paint) }

    return maskBitmap
}
// [END android_imagen_editing_create_mask]

// [START android_imagen_expand_image]
suspend fun expandImage(originalImage: Bitmap, imagenModel: ImagenModel): ImagenGenerationResponse<ImagenInlineImage> {

    // Optionally describe what should appear in the expanded area.
    val prompt = "a sprawling sandy beach next to the ocean"

    val editedImage = imagenModel.outpaintImage(
        originalImage.toImagenInlineImage(),
        Dimensions(1024, 1024),
        prompt = prompt,
        newPosition = ImagenImagePlacement.LEFT_CENTER
    )


    return editedImage
}
// [END android_imagen_expand_image]

// [START android_imagen_replace_background]
suspend fun replaceBackground(model: ImagenModel, originalImage: Bitmap): ImagenGenerationResponse<ImagenInlineImage> {
    // Provide the prompt describing the new background.
    val prompt = "space background"

    // Pass the original image, a mask, the prompt, and an editing configuration.
    val editedImage = model.editImage(
        referenceImages = listOf(
            ImagenRawImage(originalImage.toImagenInlineImage()),
            ImagenBackgroundMask(),
        ),
        prompt = prompt,
        config = ImagenEditingConfig(ImagenEditMode.INPAINT_INSERTION)
    )

    return editedImage
}
// [END android_imagen_replace_background]

// [START android_imagen_customize_subject]
suspend fun customizeCatImage(model: ImagenModel, referenceCatImage: Bitmap): ImagenGenerationResponse<ImagenInlineImage> {

    // Define the subject reference using the reference image.
    val subjectReference = ImagenSubjectReference(
        image = referenceCatImage.toImagenInlineImage(),
        referenceId = 1,
        description = "cat",
        subjectType = ImagenSubjectReferenceType.ANIMAL
    )

    // Provide a prompt that describes the final image.
    // The "[1]" links the prompt to the subject reference with ID 1.
    val prompt = "A cat[1] flying through outer space"

    // Use the editImage API to perform the subject customization.
    val editedImage = model.editImage(
        referenceImages = listOf(subjectReference),
        prompt = prompt,
        config = ImagenEditingConfig(
            editSteps = 50 // Number of editing steps, a higher value can improve quality
        )
    )

    return editedImage
}
// [END android_imagen_customize_subject]

// [START android_imagen_customize_control]
suspend fun customizeCatImageByControl(model: ImagenModel, referenceImage: Bitmap): ImagenGenerationResponse<ImagenInlineImage> {

    // Define the subject reference using the reference image.
    val controlReference = ImagenControlReference(
        image = referenceImage.toImagenInlineImage(),
        referenceId = 1,
        type = ImagenControlType.SCRIBBLE,
    )

    val prompt = "A cat flying through outer space arranged like the scribble map[1]"

    val editedImage = model.editImage(
        referenceImages = listOf(controlReference),
        prompt = prompt,
        config = ImagenEditingConfig(
            editSteps = 50
        ),
    )

    return editedImage
}
// [END android_imagen_customize_control]

// [START android_imagen_customize_style]
suspend fun customizeImageByStyle(model: ImagenModel, referenceVanGoghImage: Bitmap): ImagenGenerationResponse<ImagenInlineImage> {

    // Define the style reference using the reference image.
    val styleReference = ImagenStyleReference(
        image = referenceVanGoghImage.toImagenInlineImage(),
        referenceId = 1,
        description = "Van Gogh style"
    )

    // Provide a prompt that describes the final image.
    // The "1" links the prompt to the style reference with ID 1.
    val prompt = "A cat flying through outer space, in the Van Gogh style[1]"

    // Use the editImage API to perform the style customization.
    val editedImage = model.editImage(
        referenceImages = listOf(styleReference),
        prompt = prompt,
        config = ImagenEditingConfig(
            editSteps = 50 // Number of editing steps, a higher value can improve quality
        ),
    )

    return editedImage
}
// [END android_imagen_customize_style]
