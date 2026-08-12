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

import com.google.mlkit.genai.prompt.GenerateContentRequest
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.TextPart
import com.google.mlkit.genai.prompt.generateTypedContentRequest
import com.google.mlkit.genai.schema.annotations.Generable
import com.google.mlkit.genai.schema.annotations.Guide

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
    // [START android_snippets_structured_output_before]
    suspend fun parseEmail(email: String): String {
        val parseEmailPrompt = "Parse this email and return the sender, title, and short summary of the email less than 10 words: "

        val parsedEmail = generativeModel.generateContent(parseEmailPrompt + email)

        return parsedEmail.candidates[0].text
    }
    // [END android_snippets_structured_output_before]
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
