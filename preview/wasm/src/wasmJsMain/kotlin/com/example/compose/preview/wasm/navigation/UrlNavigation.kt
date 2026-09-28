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

package com.example.compose.preview.wasm.navigation

import androidx.compose.ui.graphics.Color
import com.example.compose.preview.wasm.theme.ThemeColorMode
import com.example.compose.preview.wasm.theme.ThemePreset
import com.example.compose.preview.wasm.theme.parseHexColor

/**
 * Extracts the snippet identifier from URL search or hash parameters.
 * Supports:
 * - #/snippet/<id>
 * - #...snippet=<id>... or #...id=<id>...
 * - #/<id>
 * - ?snippet=<id> or ?id=<id>
 */
fun extractSnippetId(search: String, hash: String): String? {
    val cleanHash = hash.removePrefix("#").trim()

    if (cleanHash.startsWith("/snippet/")) {
        val snippetPart = cleanHash.removePrefix("/snippet/").substringBefore("?").substringBefore("&").trim()
        if (snippetPart.isNotEmpty()) return snippetPart
    }

    if (cleanHash.contains("snippet=")) {
        val snippetPart = cleanHash.substringAfter("snippet=").substringBefore("&").substringBefore("?").trim()
        if (snippetPart.isNotEmpty()) return snippetPart
    }
    if (cleanHash.contains("id=")) {
        val snippetPart = cleanHash.substringAfter("id=").substringBefore("&").substringBefore("?").trim()
        if (snippetPart.isNotEmpty()) return snippetPart
    }

    if (cleanHash.startsWith("/") && !cleanHash.startsWith("/?") && !cleanHash.contains("=")) {
        val snippetPart = cleanHash.removePrefix("/").substringBefore("?").substringBefore("&").trim()
        if (snippetPart.isNotEmpty()) return snippetPart
    }

    if (search.contains("snippet=")) {
        val snippetPart = search.substringAfter("snippet=").substringBefore("&").substringBefore("?").trim()
        if (snippetPart.isNotEmpty()) return snippetPart
    }
    if (search.contains("id=")) {
        val snippetPart = search.substringAfter("id=").substringBefore("&").substringBefore("?").trim()
        if (snippetPart.isNotEmpty()) return snippetPart
    }

    return null
}

/**
 * Extracts the active ThemeColorMode from URL parameters.
 */
fun extractThemeColorMode(search: String, hash: String): ThemeColorMode {
    val combined = "$search&$hash"
    return when {
        combined.contains("theme=dark") -> ThemeColorMode.Dark
        combined.contains("theme=light") -> ThemeColorMode.Light
        else -> ThemeColorMode.System
    }
}

/**
 * Extracts the active ThemePreset from URL parameters, defaulting to MONOCHROME.
 */
fun extractPreset(search: String, hash: String): ThemePreset {
    val combined = "$search&$hash"
    if (combined.contains("preset=")) {
        val p = combined.substringAfter("preset=").substringBefore("&")
        if (p != "custom") {
            ThemePreset.fromKey(p)?.let { return it }
        }
    }
    return ThemePreset.MONOCHROME
}

/**
 * Extracts a custom seed color from URL parameters only if preset is custom or unspecified.
 */
fun extractCustomSeed(search: String, hash: String): Color? {
    val combined = "$search&$hash"
    if (combined.contains("preset=")) {
        val p = combined.substringAfter("preset=").substringBefore("&")
        if (p != "custom" && ThemePreset.fromKey(p) != null) {
            return null
        }
    }
    if (combined.contains("seed=")) {
        val rawSeed = combined.substringAfter("seed=").substringBefore("&")
        val s = if (rawSeed.startsWith("%23", ignoreCase = true)) {
            "#" + rawSeed.substring(3)
        } else if (rawSeed.startsWith("#")) {
            rawSeed
        } else {
            "#" + rawSeed
        }
        return parseHexColor(s)
    }
    return null
}

/**
 * Resolves the visual density scale factor based on URL overrides.
 */
fun extractDensityScale(search: String, hash: String): Float {
    val combined = "$search&$hash"
    val scaleStr = when {
        combined.contains("density=") -> combined.substringAfter("density=").substringBefore("&")
        combined.contains("scale=") -> combined.substringAfter("scale=").substringBefore("&")
        combined.contains("densityScale=") -> combined.substringAfter("densityScale=").substringBefore("&")
        else -> null
    }
    if (scaleStr != null) {
        val parsed = scaleStr.toFloatOrNull()
        if (parsed != null && parsed in 0.1f..3.0f) {
            return parsed
        }
    }
    return 1.25f
}
