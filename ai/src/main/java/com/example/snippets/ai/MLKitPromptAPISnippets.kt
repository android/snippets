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

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.prompt.GenerateContentRequest
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.ImagePart
import com.google.mlkit.genai.prompt.PromptPrefix
// [START android_snippets_mlkit_system_instructions_imports]
import com.google.mlkit.genai.prompt.SystemInstruction
import com.google.mlkit.genai.prompt.TextPart
// [START_EXCLUDE silent]
import com.google.mlkit.genai.prompt.createCachedContextRequest
// [END_EXCLUDE]
import com.google.mlkit.genai.prompt.generateContentRequest
// [END android_snippets_mlkit_system_instructions_imports]
import com.google.mlkit.genai.prompt.generateTypedContentRequest
// [START android_snippets_mlkit_structured_output_schema]
import com.google.mlkit.genai.schema.annotations.Generable
import com.google.mlkit.genai.schema.annotations.Guide

@Generable
data class PlantList(
    @Guide(description = "The list of plants found", minItems = 1, maxItems = 5)
    val plants: List<Plant>
)

@Generable("Information about a plant species")
data class Plant(
    @Guide(description = "The common name of the plant")
    val commonName: String,

    @Guide(description = "The full latin scientific name of the plant")
    val scientificName: String,

    @Guide(
        description = "The maximum height of the plant in centimeters.",
        minimum = 1.0,
        maximum = 10000.0
    )
    val maxHeightCm: Int,

    @Guide(description = "Whether the plant is poisonous or not")
    val isPoisonous: Boolean?,

    @Guide(
        description = "The primary continent where this plant is native to",
        enumValues = ["Africa", "Antarctica", "Asia", "Australia", "Europe", "North America", "South America"]
    )
    val nativeContinent: String
)
// [END android_snippets_mlkit_structured_output_schema]

val generativeModel = Generation.getClient()

fun instantiateModel() {
// [START android_snippets_mlkit_instantiate_model]
// Instantiating model in activity, fragment, or ViewModel
    val generativeModel = Generation.getClient()

// When activity, fragment, or ViewModel is destroyed
    generativeModel.close()
// [END android_snippets_mlkit_instantiate_model]
}

class MLKitPromptAPISnippets {
    private val TAG = "MLKitPromptAPISnippets"
    private var modelDownloaded = false

    // [START android_snippets_structured_output_before]
    suspend fun parseEmail(email: String): String {
        val parseEmailPrompt = "Parse this email and return the sender, title, and short summary of the email less than 10 words: "

        val parsedEmail = generativeModel.generateContent(parseEmailPrompt + email)

        return parsedEmail.candidates[0].text
    }
    // [END android_snippets_structured_output_before]

    fun createClient() {
        // [START android_snippets_mlkit_get_client]
        // Get a GenerativeModel instance
        val generativeModel = Generation.getClient()
        // [END android_snippets_mlkit_get_client]
    }

    suspend fun checkAndDownloadModel() {
        // [START android_snippets_mlkit_check_and_download]
        val status = generativeModel.checkStatus()
        when (status) {
            FeatureStatus.UNAVAILABLE -> {
                // Gemini Nano not supported on this device or device hasn't fetched the latest configuration to support it
            }

            FeatureStatus.DOWNLOADABLE -> {
                // Gemini Nano can be downloaded on this device, but is not currently downloaded
                generativeModel.download().collect { status ->
                    when (status) {
                        is DownloadStatus.DownloadStarted ->
                            Log.d(TAG, "starting download for Gemini Nano")

                        is DownloadStatus.DownloadProgress ->
                            Log.d(TAG, "Nano ${status.totalBytesDownloaded} bytes downloaded")

                        DownloadStatus.DownloadCompleted -> {
                            Log.d(TAG, "Gemini Nano download complete")
                            modelDownloaded = true
                        }

                        is DownloadStatus.DownloadFailed -> {
                            Log.e(TAG, "Nano download failed ${status.e.message}")
                        }
                    }
                }
            }

            FeatureStatus.DOWNLOADING -> {
                // Gemini Nano currently being downloaded
            }

            FeatureStatus.AVAILABLE -> {
                // Gemini Nano currently downloaded and available to use on this device
            }
        }
        // [END android_snippets_mlkit_check_and_download]
    }

    suspend fun generateTextOnly() {
        // [START android_snippets_mlkit_text_only]
        val response = generativeModel.generateContent("Write a 3 sentence story about a magical dog.")
        // [END android_snippets_mlkit_text_only]
    }

    suspend fun generateTextWithParams() {
        // [START android_snippets_mlkit_text_with_params]
        val response = generativeModel.generateContent(
            generateContentRequest(
                TextPart("Write a 3 sentence story about a magical dog."),
            ) {
                // Optional parameters
                temperature = 0.2f
                topK = 10
                candidateCount = 3
            },
        )
        // [END android_snippets_mlkit_text_with_params]
    }

    suspend fun generateMultimodal(bitmap: Bitmap, textPrompt: String) {
        // [START android_snippets_mlkit_multimodal]
        val response = generativeModel.generateContent(
            generateContentRequest(ImagePart(bitmap), TextPart(textPrompt)) {
                // optional parameters
            },
        )
        // [END android_snippets_mlkit_multimodal]
    }

    suspend fun generateNonStreaming() {
        // [START android_snippets_mlkit_non_streaming]
        // Call the AI model to generate content and store the complete
        // in a new variable named 'response' once it's finished
        val response = generativeModel.generateContent("Write a 3 sentence story about a magical dog")
        // [END android_snippets_mlkit_non_streaming]
    }

    suspend fun generateStreaming() {
        // [START android_snippets_mlkit_streaming]
        // Streaming inference
        var fullResponse = ""
        generativeModel.generateContentStream("Write a 3 sentence story about a magical dog").collect { chunk ->
            val newChunkReceived = chunk.candidates[0].text
            print(newChunkReceived)
            fullResponse += newChunkReceived
        }
        // [END android_snippets_mlkit_streaming]
    }

    suspend fun implicitPrefixCaching() {
        // [START android_snippets_mlkit_prefix_caching_implicit]
        val promptPrefix = "Reverse the given sentence: "
        val dynamicSuffix = "Hello World"

        val result = generativeModel.generateContent(
            generateContentRequest(TextPart(dynamicSuffix)) {
                this.promptPrefix = PromptPrefix(promptPrefix)
            }
        )
        // [END android_snippets_mlkit_prefix_caching_implicit]
    }

    suspend fun explicitPrefixCachingCreateAndUse() {
        // [START android_snippets_mlkit_prefix_caching_explicit_create]
        val cacheName = "my_cache"
        val promptPrefix = "Reverse the given sentence: "
        val dynamicSuffix = "Hello World"

        // Create a cache
        val cacheRequest = createCachedContextRequest(cacheName, PromptPrefix(promptPrefix))
        val cache = generativeModel.caches.create(cacheRequest)

        // Run inference with the cache
        val response = generativeModel.generateContent(
            generateContentRequest(TextPart(dynamicSuffix)) {
                cachedContextName = cache.name
            }
        )
        // [END android_snippets_mlkit_prefix_caching_explicit_create]
    }

    suspend fun explicitPrefixCachingManage() {
        // [START android_snippets_mlkit_prefix_caching_explicit_manage]
        val cacheName = "my_cache"

        // Query pre-created caches
        for (cache in generativeModel.caches.list()) {
            // Do something with cache
        }

        // Get specific cache
        val cache = generativeModel.caches.get(cacheName)

        // Delete a pre-created cache
        generativeModel.caches.delete(cacheName)
        // [END android_snippets_mlkit_prefix_caching_explicit_manage]
    }

    suspend fun generateStructuredContent() {
        // [START android_snippets_mlkit_structured_output_generate]
        // 1. Initialize your GenerativeModel as usual
        val generativeModel = Generation.getClient()

        // 2. Prepare the prompt text
        val promptText = "List some common plants found in California."
        val baseRequest = GenerateContentRequest.Builder(TextPart(promptText)).build()

        // 3. Create the typed request, specifying the target class (e.g., PlantList)
        val typedRequest = generateTypedContentRequest(
            generateContentRequest = baseRequest,
            outputClass = PlantList::class
        )

        // 4. Run the inference
        try {
            val typedResponse = generativeModel.generateContent(typedRequest)

            // 5. Access the parsed object
            // The response candidates contain the parsed object of type T (PlantList in this case)
            val plantList: PlantList? = typedResponse.candidates.firstOrNull()?.response

            if (plantList != null) {
                // Process the structured data
                for (plant in plantList.plants) {
                    Log.d("StructuredOutput", "Found plant: ${plant.commonName} (${plant.scientificName})")
                }
            } else {
                Log.e("StructuredOutput", "Failed to parse response into the desired structure.")

                // Inspect finish reason for details
                val finishReason = typedResponse.candidates.firstOrNull()?.finishReason
                Log.d("StructuredOutput", "Finish reason: $finishReason")
            }
        } catch (e: GenAiException) {
            // Handle API errors
            when (e.errorCode) {
                GenAiException.ErrorCode.STRUCTURED_OUTPUT_REQUEST_ERROR -> {
                    Log.e("StructuredOutput", "The class structure is not supported.")
                }
                GenAiException.ErrorCode.STRUCTURED_OUTPUT_RESPONSE_ERROR -> {
                    Log.e("StructuredOutput", "The model generated values that violate the schema constraints.")
                }
                else -> {
                    Log.e("StructuredOutput", "API error: ${e.message}")
                }
            }
        }
        // [END android_snippets_mlkit_structured_output_generate]
    }

    suspend fun useSystemInstructions() {
        // [START android_snippets_mlkit_system_instructions_builder]
        // 1. Define the system instruction
        val systemInstruction =
            SystemInstruction("You are a concise assistant. Answer in 2 sentences or less.")

        // 2. Create the request
        val request = generateContentRequest(TextPart("How does photosynthesis work?")) {
            this.systemInstruction = systemInstruction
        }

        // 3. Run inference
        try {
            val response = generativeModel.generateContent(request)
            println(response.candidates.firstOrNull()?.text)
        } catch (e: GenAiException) {
            // Handle SDK-specific exceptions
        }
        // [END android_snippets_mlkit_system_instructions_builder]
    }

    fun useSystemInstructionsShorthand() {
        // [START android_snippets_mlkit_system_instructions_shorthand]
        val request = generateContentRequest(
            SystemInstruction("You are a pirate. Speak like one."),
            TextPart("What is the weather like today?")
        ) {
            // Optional configurations like temperature
            temperature = 0.7f
        }
        // [END android_snippets_mlkit_system_instructions_shorthand]
    }
}

// [START android_snippets_structured_output_after]
@Generable
data class ParsedEmail(
    @Guide(description = "Sender of the email")
    var sender: String = "",

    @Guide(description = "Title of the email")
    var title: String = "",

    @Guide(description = "Summary of the email less than 10 words")
    var summary: String = ""
)

suspend fun parseEmail(email: String): ParsedEmail? {
    val parseEmailPrompt =
        "Parse this email: $email"

    val baseRequest = GenerateContentRequest.Builder(TextPart(parseEmailPrompt)).build()
    val typedRequest = generateTypedContentRequest(baseRequest, ParsedEmail::class)
    val typedResponse = generativeModel.generateContent(typedRequest)
    return typedResponse.candidates[0].response
}
// [END android_snippets_structured_output_after]

class ReturnTypeExampleBefore {
    // [START android_snippets_structured_output_return_type_before]
    fun parseEmail(email: String): String {
        // [START_EXCLUDE]
        return ""
        // [END_EXCLUDE]
    }
    // [END android_snippets_structured_output_return_type_before]
}

class ReturnTypeExampleAfter {
    // [START android_snippets_structured_output_return_type_after]
    fun parseEmail(email: String): ParsedEmail? {
        // [START_EXCLUDE]
        return null
        // [END_EXCLUDE]
    }
    // [END android_snippets_structured_output_return_type_after]
}
