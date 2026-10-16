/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.compose.preview.wasm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import com.example.compose.preview.wasm.navigation.extractCustomSeed
import com.example.compose.preview.wasm.navigation.extractDensityScale
import com.example.compose.preview.wasm.navigation.extractPreset
import com.example.compose.preview.wasm.navigation.extractSnippetId
import com.example.compose.preview.wasm.navigation.extractThemeColorMode
import com.example.compose.preview.wasm.registry.SnippetRegistry
import com.example.compose.preview.wasm.registry.previewFrameFor
import com.example.compose.preview.wasm.theme.CatalogTheme
import com.example.compose.preview.wasm.theme.Theme
import kotlinx.browser.window
import kotlinx.coroutines.delay

/** Largest density scale applied when fitting a snippet to the stage (1.25x on the DAC stage). */
private const val MaxDensityScale = 1.25f

/**
 * Root Composable entry point for the WebAssembly (WASM) Compose interactive runner.
 * Manages URL hash synchronization, dynamic theme updates, density scaling, and snippet rendering.
 */
@Composable
fun WasmPreviewApp(fontFamily: FontFamily? = null) {
    fun buildThemeFromUrl(search: String, hash: String): Theme = Theme(
        themeColorMode = extractThemeColorMode(search, hash),
        preset = extractPreset(search, hash),
        customColor = extractCustomSeed(search, hash),
    )

    var theme by remember {
        mutableStateOf(buildThemeFromUrl(window.location.search, window.location.hash))
    }
    var densityOverride by remember {
        mutableStateOf(extractDensityScale(window.location.search, window.location.hash))
    }
    var selectedSnippetId by remember {
        mutableStateOf(extractSnippetId(window.location.search, window.location.hash))
    }

    LaunchedEffect(Unit) {
        var lastHash = window.location.hash
        var lastSearch = window.location.search
        while (true) {
            delay(100)
            val currentHash = window.location.hash
            val currentSearch = window.location.search
            if (currentHash != lastHash || currentSearch != lastSearch) {
                lastHash = currentHash
                lastSearch = currentSearch
                selectedSnippetId = extractSnippetId(currentSearch, currentHash)
                theme = buildThemeFromUrl(currentSearch, currentHash)
                densityOverride = extractDensityScale(currentSearch, currentHash)
            }
        }
    }

    // Scale the density so the snippet's PreviewFrame fits the stage, matching the area the
    // Roborazzi screenshot was captured in. The default 384x216dp frame on the 270px-tall DAC
    // stage gives 1.25x; larger frames (dialogs, pickers) and small stages zoom out instead of
    // being clipped. Never zoom in beyond 1.25x, so opening wasm.html in a large window keeps
    // components at their normal size.
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val snippetId = selectedSnippetId
        val frame = previewFrameFor(snippetId.orEmpty())
        val fitScale = minOf(maxWidth.value / frame.widthDp, maxHeight.value / frame.heightDp)
        val densityScale = densityOverride
            ?: fitScale.takeIf { it.isFinite() && it > 0f }?.coerceAtMost(MaxDensityScale)
            ?: MaxDensityScale
        CatalogTheme(theme = theme.copy(densityScale = densityScale), fontFamily = fontFamily) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val currentSnippet = snippetId?.let { SnippetRegistry.getById(it) }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentSnippet != null) {
                        currentSnippet()
                    } else {
                        Text(
                            text = "Snippet not found: ${snippetId ?: "(no ?id= specified)"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
