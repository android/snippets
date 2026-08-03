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

package com.example.snippets.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.InferenceMode
import com.google.firebase.ai.OnDeviceConfig
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.PublicPreviewAPI

@OptIn(PublicPreviewAPI::class)
private object HybridInferenceSnippets {
    private suspend fun runHybridInference() {
        // [START android_hybrid_inference_snippet]
        val model = Firebase.ai(backend = GenerativeBackend.Companion.googleAI())
            .generativeModel(
                modelName = "gemini-2.5-flash",
                onDeviceConfig = OnDeviceConfig(mode = InferenceMode.Companion.PREFER_ON_DEVICE)
            )

        val response = model.generateContent("Write a story about a green robot.")
        print(response.text)
        // [END android_hybrid_inference_snippet]
    }
}
