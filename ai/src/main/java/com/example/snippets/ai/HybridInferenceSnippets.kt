package com.example.snippets.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.InferenceMode
import com.google.firebase.ai.OnDeviceConfig
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.PublicPreviewAPI

@OptIn(PublicPreviewAPI::class)
object HybridInferenceSnippets {
    suspend fun runHybridInference() {
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