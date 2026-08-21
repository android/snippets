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

package com.example.camerax.snippets

import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView

// [START android_camerax_transform_output_get_correction_matrix]
fun getCorrectionMatrix(imageProxy: ImageProxy, previewView: PreviewView): Matrix {
    val cropRect = imageProxy.cropRect
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
    val matrix = Matrix()

    // A float array of the source vertices (crop rect) in clockwise order.
    val source = floatArrayOf(
        cropRect.left.toFloat(),
        cropRect.top.toFloat(),
        cropRect.right.toFloat(),
        cropRect.top.toFloat(),
        cropRect.right.toFloat(),
        cropRect.bottom.toFloat(),
        cropRect.left.toFloat(),
        cropRect.bottom.toFloat()
    )

    // A float array of the destination vertices in clockwise order.
    val destination = floatArrayOf(
        0f,
        0f,
        previewView.width.toFloat(),
        0f,
        previewView.width.toFloat(),
        previewView.height.toFloat(),
        0f,
        previewView.height.toFloat()
    )

    // The destination vertexes need to be shifted based on rotation degrees. The
    // rotation degree represents the clockwise rotation needed to correct the image.

    // Each vertex is represented by 2 float numbers in the vertices array.
    val vertexSize = 2
    // The destination needs to be shifted 1 vertex for every 90° rotation.
    val shiftOffset = rotationDegrees / 90 * vertexSize
    val tempArray = destination.clone()
    for (toIndex in source.indices) {
        val fromIndex = (toIndex + shiftOffset) % source.size
        destination[toIndex] = tempArray[fromIndex]
    }
    matrix.setPolyToPoly(source, 0, destination, 0, 4)
    return matrix
}
// [END android_camerax_transform_output_get_correction_matrix]
