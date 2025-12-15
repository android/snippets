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

package com.example.compose.snippets.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.platform.LocalContext
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

private object StateLifespansSnippets1 {
    // [START android_compose_state_lifespans_saver]
    data class Size(val x: Int, val y: Int) {
        object Saver : androidx.compose.runtime.saveable.Saver<Size, Any> by listSaver(
            save = { listOf(it.x, it.y) },
            restore = { Size(it[0], it[1]) }
        )
    }

    @Composable
    fun rememberSize(x: Int, y: Int) {
        rememberSaveable(x, y, saver = Size.Saver) {
            Size(x, y)
        }
    }
    // [END android_compose_state_lifespans_saver]
}

private object StateLifespansSnippets2 {
    // [START android_compose_state_lifespans_retain_invocation]
    @Composable
    fun MediaPlayer() {
        // Use the application context to avoid a memory leak
        val applicationContext = LocalContext.current.applicationContext
        val exoPlayer = retain { ExoPlayer.Builder(applicationContext).apply { /* ... */ }.build() }
        // ...
    }
    // [END android_compose_state_lifespans_retain_invocation]
}

private object StateLifespansSnippets3 {

    @Serializable
    object AnotherSerializableType

    private fun <T> defaultValue(): T = TODO()

    // [START android_compose_state_lifespans_combined_retain_save]
    @Composable
    fun rememberAndRetain(): CombinedRememberRetained {
        val saveData = rememberSerializable(serializer = serializer<ExtractedSaveData>()) {
            ExtractedSaveData()
        }
        val retainData = retain { ExtractedRetainData() }
        return remember(saveData, retainData) {
            CombinedRememberRetained(saveData, retainData)
        }
    }

    @Serializable
    data class ExtractedSaveData(
        // All values that should persist process death should be managed by this class.
        var savedData: AnotherSerializableType = defaultValue()
    )

    class ExtractedRetainData {
        // All values that should be retained should appear in this class.
        // It's possible to manage a CoroutineScope using RetainObserver.
        // See the full sample for details.
        var retainedData = Any()
    }

    class CombinedRememberRetained(
        private val saveData: ExtractedSaveData,
        private val retainData: ExtractedRetainData,
    ) {
        fun doAction() {
            // Manipulate the retained and saved state as needed.
        }
    }
    // [END android_compose_state_lifespans_combined_retain_save]
}

private object StateLifespansSnippets4 {
    // [START android_compose_state_lifespans_remember_factory_functions]
    @Composable
    fun rememberImageState(
        imageUri: String,
        initialZoom: Float = 1f,
        initialPanX: Int = 0,
        initialPanY: Int = 0
    ): ImageState {
        return rememberSaveable(imageUri, saver = ImageState.Saver) {
            ImageState(
                imageUri, initialZoom, initialPanX, initialPanY
            )
        }
    }

    data class ImageState(
        val imageUri: String,
        val zoom: Float,
        val panX: Int,
        val panY: Int
    ) {
        object Saver : androidx.compose.runtime.saveable.Saver<ImageState, Any> by listSaver(
            save = { listOf(it.imageUri, it.zoom, it.panX, it.panY) },
            restore = { ImageState(it[0] as String, it[1] as Float, it[2] as Int, it[3] as Int) }
        )
    }
    // [END android_compose_state_lifespans_remember_factory_functions]
}
