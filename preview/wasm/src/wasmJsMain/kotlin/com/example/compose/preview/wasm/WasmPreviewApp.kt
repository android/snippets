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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.catalog.library.model.Theme
import androidx.compose.material3.catalog.library.model.ThemeColorMode
import androidx.compose.material3.catalog.library.ui.theme.CatalogTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.compose.preview.wasm.navigation.extractCustomSeed
import com.example.compose.preview.wasm.navigation.extractDensityScale
import com.example.compose.preview.wasm.navigation.extractPreset
import com.example.compose.preview.wasm.navigation.extractSnippetId
import com.example.compose.preview.wasm.registry.SnippetRegistry
import com.example.compose.preview.wasm.theme.ThemePreset
import com.example.compose.preview.wasm.theme.parseHexColor
import kotlinx.browser.window
import kotlinx.coroutines.delay

/**
 * Root Composable entry point for the WebAssembly (WASM) Compose interactive runner.
 * Manages URL hash synchronization, dynamic theme updates, density scaling, and snippet rendering.
 */
@Composable
fun WasmPreviewApp() {
    val initialSearch = remember { window.location.search }
    val initialHash = remember { window.location.hash }
    val initialStandalone = remember {
        initialSearch.contains("standalone=true") || initialHash.contains("standalone=true")
    }

    var theme by remember {
        mutableStateOf(
            Theme(
                themeColorMode = if (initialSearch.contains("theme=dark") || initialHash.contains("theme=dark")) {
                    ThemeColorMode.Dark
                } else if (initialSearch.contains("theme=light") || initialHash.contains("theme=light")) {
                    ThemeColorMode.Light
                } else {
                    ThemeColorMode.System
                },
                preset = extractPreset(initialSearch, initialHash),
                customColor = extractCustomSeed(initialSearch, initialHash),
                densityScale = extractDensityScale(initialSearch, initialHash, initialStandalone)
            )
        )
    }
    var standaloneMode by remember {
        mutableStateOf(initialStandalone)
    }
    var selectedSnippetId by remember {
        mutableStateOf(extractSnippetId(initialSearch, initialHash))
    }

    // Read URL parameters on startup or hashchange
    fun parseUrlParams() {
        val search = window.location.search
        val hash = window.location.hash
        val combined = "$search&$hash"

        val snippetId = extractSnippetId(search, hash)
        if (snippetId != selectedSnippetId) {
            selectedSnippetId = snippetId
        }

        if (combined.contains("theme=dark")) {
            if (theme.themeColorMode != ThemeColorMode.Dark) {
                theme = theme.copy(themeColorMode = ThemeColorMode.Dark)
            }
        } else if (combined.contains("theme=light")) {
            if (theme.themeColorMode != ThemeColorMode.Light) {
                theme = theme.copy(themeColorMode = ThemeColorMode.Light)
            }
        }

        val presetParam = if (combined.contains("preset=")) {
            combined.substringAfter("preset=").substringBefore("&")
        } else null

        if (presetParam != null && presetParam != "custom") {
            ThemePreset.fromKey(presetParam)?.let { found ->
                if (theme.preset != found || theme.customColor != null) {
                    theme = theme.copy(preset = found, customColor = null)
                }
            }
        } else if (presetParam == "custom" || (presetParam == null && combined.contains("seed="))) {
            if (combined.contains("seed=")) {
                val rawSeed = combined.substringAfter("seed=").substringBefore("&")
                val s = if (rawSeed.startsWith("%23", ignoreCase = true)) {
                    "#" + rawSeed.substring(3)
                } else if (rawSeed.startsWith("#")) {
                    rawSeed
                } else {
                    "#" + rawSeed
                }
                val parsed = parseHexColor(s)
                if (parsed != null && theme.customColor != parsed) {
                    theme = theme.copy(customColor = parsed)
                }
            }
        }

        if (combined.contains("standalone=true")) {
            standaloneMode = true
        } else if (combined.contains("standalone=false")) {
            standaloneMode = false
        }

        val newDensity = extractDensityScale(search, hash, standaloneMode)
        if (theme.densityScale != newDensity) {
            theme = theme.copy(densityScale = newDensity)
        }
    }

    // Polling loop to detect hash or search changes across browsers safely without SAM conversion issues
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
                parseUrlParams()
            }
        }
    }

    CatalogTheme(theme = theme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val currentSnippet = selectedSnippetId?.let { SnippetRegistry.getById(it) }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (currentSnippet != null) {
                    currentSnippet()
                } else {
                    Text(
                        text = "Snippet not found: ${selectedSnippetId ?: "(no ?id= specified)"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
