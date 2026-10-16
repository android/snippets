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

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.example.compose.preview.wasm.theme.loadPreviewFontFamily
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLElement

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val root = document.getElementById("composeApp") ?: document.body!!
    MainScope().launch {
        // Load the preview font before the first frame so text never flashes in the fallback face.
        val fontFamily = loadPreviewFontFamily()
        ComposeViewport(root) {
            (document.getElementById("loading") as? HTMLElement)?.style?.display = "none"
            WasmPreviewApp(fontFamily = fontFamily)
        }
    }
}
