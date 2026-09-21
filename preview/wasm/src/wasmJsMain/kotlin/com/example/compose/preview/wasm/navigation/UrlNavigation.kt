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
import com.example.compose.preview.wasm.theme.ThemePreset
import com.example.compose.preview.wasm.theme.parseHexColor
import kotlinx.browser.window

/**
 * Extracts the snippet identifier from URL search or hash parameters.
 * Supports:
 * - #/snippet/<id>
 * - #...snippet=<id>...
 * - #/<id>
 * - ?snippet=<id>
 * Returns null if catalog overview is requested (#catalog or #/catalog).
 */
fun extractSnippetId(search: String, hash: String): String? {
    val cleanHash = hash.removePrefix("#").trim()

    // 1. If hash explicitly requests the catalog overview, return null
    if (cleanHash == "catalog" || cleanHash == "/catalog" || cleanHash.startsWith("/catalog?") || cleanHash.startsWith("catalog?")) {
        return null
    }

    // 2. If hash specifies a snippet via route path: #/snippet/<id>
    if (cleanHash.startsWith("/snippet/")) {
        val snippetPart = cleanHash.removePrefix("/snippet/").substringBefore("?").substringBefore("&").trim()
        if (snippetPart.isNotEmpty()) return snippetPart
    }

    // 3. If hash has snippet query parameter: #...snippet=<id>...
    if (cleanHash.contains("snippet=")) {
        val snippetPart = cleanHash.substringAfter("snippet=").substringBefore("&").substringBefore("?").trim()
        if (snippetPart.isNotEmpty()) return snippetPart
    }

    // 4. If hash has direct path #/<id> (e.g. #/filled-button)
    if (cleanHash.startsWith("/") && !cleanHash.startsWith("/?") && !cleanHash.contains("=")) {
        val snippetPart = cleanHash.removePrefix("/").substringBefore("?").substringBefore("&").trim()
        if (snippetPart.isNotEmpty()) return snippetPart
    }

    // 5. If hash did not specify a snippet or catalog overview, fall back to URL query search: ?snippet=<id>
    if (search.contains("snippet=")) {
        val snippetPart = search.substringAfter("snippet=").substringBefore("&").substringBefore("?").trim()
        if (snippetPart.isNotEmpty()) return snippetPart
    }

    return null
}

/**
 * Extracts the active ThemePreset from URL parameters, defaulting to ANDROID_GREEN.
 */
fun extractPreset(search: String, hash: String): ThemePreset {
    val combined = "$search&$hash"
    if (combined.contains("preset=")) {
        val p = combined.substringAfter("preset=").substringBefore("&")
        if (p != "custom") {
            ThemePreset.fromKey(p)?.let { return it }
        }
    }
    return ThemePreset.ANDROID_GREEN
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
 * Detects whether the WASM app is running embedded inside an iframe.
 */
fun isDisplayedInIframe(): Boolean {
    return try {
        window.parent != window
    } catch (e: Throwable) {
        true
    }
}

/**
 * Resolves the visual density scale factor based on URL overrides, fullscreen state, or iframe container.
 */
fun extractDensityScale(search: String, hash: String, standalone: Boolean): Float {
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
    // When in standalone / fullscreen mode, render at 1.0x desktop density
    if (standalone || combined.contains("standalone=true")) {
        return 1.0f
    }
    // When displayed inside the phone iframe (or any iframe), fake density to 0.75f
    if (isDisplayedInIframe() || combined.contains("phone=true")) {
        return 0.75f
    }
    return 1.0f
}
