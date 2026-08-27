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
import com.google.firebase.ai.Chat
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.content

@OptIn(PublicPreviewAPI::class)
private fun createGenerativeModel(): GenerativeModel {
    // [START android_ai_socialite_generative_model]
    val generativeModel = GenerativeModel(
        // Set the model name to the latest Gemini model.
        modelName = "gemini-2.0-flash-lite-001",
        // Set a system instruction to set the behavior of the model.
        systemInstruction = content {
            text("Please respond to this chat conversation like a friendly cat.")
        },
    )
    // [END android_ai_socialite_generative_model]
    return generativeModel
}

@OptIn(PublicPreviewAPI::class)
private fun startChatExample(generativeModel: GenerativeModel, chatId: String): Chat {
    // [START android_ai_socialite_start_chat]
    val pastMessages = getMessageHistory(chatId)
    val chat = generativeModel.startChat(
        history = pastMessages,
    )
    // [END android_ai_socialite_start_chat]
    return chat
}

@OptIn(PublicPreviewAPI::class)
private fun GenerativeModel(
    modelName: String,
    systemInstruction: Content? = null,
): GenerativeModel {
    return Firebase.ai.generativeModel(
        modelName = modelName,
        systemInstruction = systemInstruction,
    )
}

private fun getMessageHistory(chatId: String): List<Content> = emptyList()
