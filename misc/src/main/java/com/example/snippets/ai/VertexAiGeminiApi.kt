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

package com.example.snippets.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class VertexAiGeminiApi {

    // [START android_snippets_vertex_ai_gemini_api_model]
    val model = Firebase.ai(backend = GenerativeBackend.vertexAI())
        .generativeModel("gemini-2.5-flash")
    // [END android_snippets_vertex_ai_gemini_api_model]

    fun generateText(scope: CoroutineScope) {
        // [START android_snippets_vertex_ai_generate_content]
        // Note: generateContent() is a suspend function, which integrates well
        // with existing Kotlin code.
        scope.launch {
            val response = model.generateContent("Write a story about a magic backpack.")
        }
        // [END android_snippets_vertex_ai_generate_content]
    }
}
