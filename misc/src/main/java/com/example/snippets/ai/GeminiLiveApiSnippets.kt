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
import com.google.firebase.ai.type.FunctionCallPart
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.SpeechConfig
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.Voice
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.liveGenerationConfig
import com.google.firebase.ai.type.FunctionDeclaration
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

@Suppress("unused", "UNUSED_VARIABLE")
class GeminiLiveApiSnippets {

    fun initializeLiveModel() {
        // [START android_gemini_live_api_initialize]
        // Initialize the `LiveModel`
        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
            modelName = "gemini-live-2.5-flash-preview",
            generationConfig = liveGenerationConfig {
                responseModality = ResponseModality.AUDIO
                speechConfig = SpeechConfig(voice = Voice("FENRIR"))
            })
        // [END android_gemini_live_api_initialize]
    }

    fun initializeLiveModelWithSystemInstruction() {
        // [START android_gemini_live_api_initialize_system_instruction]
        val systemInstruction = content {
            text("You are a helpful assistant, you main role is [...]")
        }

        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
            modelName = "gemini-live-2.5-flash-preview",
            generationConfig = liveGenerationConfig {
                responseModality = ResponseModality.AUDIO
                speechConfig = SpeechConfig(voice = Voice("FENRIR"))
            },
            systemInstruction = systemInstruction,
        )
        // [END android_gemini_live_api_initialize_system_instruction]
    }

    fun initializeLiveApiSession() {
        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
            modelName = "gemini-live-2.5-flash-preview",
        )
        // [START android_gemini_live_api_initialize_session]
        val session = model.connect()
        session.startAudioConversation()
        // [END android_gemini_live_api_initialize_session]
    }

    fun functionCalling() {
        val itemList = mutableListOf<String>()

        fun addList(item: String) {
            itemList.add(item)
        }

        // [START android_gemini_live_api_function_calling_declaration]
        val addListFunctionDeclaration = FunctionDeclaration(
            name = "addList",
            description = "Function adding an item the list",
            parameters = mapOf(
                "item" to Schema.string("A short string describing the item to add to the list")
            )
        )
        // [END android_gemini_live_api_function_calling_declaration]

        // [START android_gemini_live_api_function_calling_tool]
        val addListTool = Tool.functionDeclarations(listOf(addListFunctionDeclaration))

        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
            modelName = "gemini-live-2.5-flash-preview",
            generationConfig = liveGenerationConfig {
                responseModality = ResponseModality.AUDIO
                speechConfig = SpeechConfig(voice = Voice("FENRIR"))
            },
            systemInstruction = content { text("...") },
            tools = listOf(addListTool)
        )
        // [END android_gemini_live_api_function_calling_tool]

        val session = model.connect()

        // [START android_gemini_live_api_function_calling_handler]
        session.startAudioConversation(::functionCallHandler)

        // ...

        fun functionCallHandler(functionCall: FunctionCallPart): FunctionResponsePart {
            return when (functionCall.name) {
                "addList" -> {
                    // Extract function parameter from functionCallPart
                    val itemName = functionCall.args["item"]!!.jsonPrimitive.content
                    // Call function with parameter
                    addList(itemName)
                    // Confirm the function call to the model
                    val response = JsonObject(
                        mapOf(
                            "success" to JsonPrimitive(true),
                            "message" to JsonPrimitive("Item $itemName added to the todo list")
                        )
                    )
                    FunctionResponsePart(functionCall.name, response)
                }

                else -> {
                    val response = JsonObject(
                        mapOf(
                            "error" to JsonPrimitive("Unknown function: ${functionCall.name}")
                        )
                    )
                    FunctionResponsePart(functionCall.name, response)
                }
            }
        }
        // [END android_gemini_live_api_function_calling_handler]
    }
}
