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

import android.Manifest
import android.app.Application
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FunctionCallPart
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.LiveSession
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.SpeechConfig
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.Voice
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.liveGenerationConfig
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

// See https://github.com/android/ai-samples/tree/main/samples/gemini-live-todo
// for a sample app with the Gemini Live API
@OptIn(PublicPreviewAPI::class)
class GeminiLiveViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "GeminiLiveViewModel"

    private var liveSession: LiveSession? = null

    fun initializeLiveModel() {
        // [START android_ai_gemini_live_initialize]
        // Initialize the `LiveModel`
        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
            modelName = "gemini-2.5-flash-native-audio-preview-12-2025",
            generationConfig = liveGenerationConfig {
                responseModality = ResponseModality.AUDIO
                speechConfig = SpeechConfig(voice = Voice("FENRIR"))
            }
        )
        // [END android_ai_gemini_live_initialize]
    }

    fun initializeWithSystemInstruction() {
        // [START android_ai_gemini_live_system_instruction]
        val systemInstruction = content {
            text("You are a helpful assistant, you main role is [...]")
        }

        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
            modelName = "gemini-2.5-flash-native-audio-preview-12-2025",
            generationConfig = liveGenerationConfig {
                responseModality = ResponseModality.AUDIO
                speechConfig = SpeechConfig(voice = Voice("FENRIR"))
            },
            systemInstruction = systemInstruction,
        )
        // [END android_ai_gemini_live_system_instruction]
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startConversation() {
        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
            modelName = "gemini-2.5-flash-native-audio-preview-12-2025",
            generationConfig = liveGenerationConfig {
                responseModality = ResponseModality.AUDIO
                speechConfig = SpeechConfig(voice = Voice("FENRIR"))
            }
        )

        viewModelScope.launch {
            try {
                // [START android_ai_gemini_live_connect]
                val session = model.connect()
                session.startAudioConversation()
                // [END android_ai_gemini_live_connect]
            } catch (e: Exception) {
                Log.e(TAG, "Error connecting to the model", e)
            }
        }
    }

    // [START android_ai_gemini_live_function_declaration]
    val itemList = mutableListOf<String>()

    fun addList(item: String) {
        itemList.add(item)
    }

    val addListFunctionDeclaration = FunctionDeclaration(
        name = "addList",
        description = "Function adding an item the list",
        parameters = mapOf(
            "item" to Schema.string("A short string describing the item to add to the list")
        )
    )
    // [END android_ai_gemini_live_function_declaration]

    fun setupTool() {
        val systemInstruction = content { text("You are a helpful assistant...") }

        // [START android_ai_gemini_live_tool_setup]
        val addListTool = Tool.functionDeclarations(listOf(addListFunctionDeclaration))

        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
            modelName = "gemini-2.5-flash-native-audio-preview-12-2025",
            generationConfig = liveGenerationConfig {
                responseModality = ResponseModality.AUDIO
                speechConfig = SpeechConfig(voice = Voice("FENRIR"))
            },
            systemInstruction = systemInstruction,
            tools = listOf(addListTool)
        )
        // [END android_ai_gemini_live_tool_setup]

        viewModelScope.launch {
            try {
                liveSession = model.connect()
            } catch (e: Exception) {
                Log.e(TAG, "Error connecting to the model", e)
            }
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startWithHandler() {
        viewModelScope.launch {
            liveSession?.let { session ->
                // [START android_ai_gemini_live_function_call_handler]
    session.startAudioConversation(::functionCallHandler)

    // [START_EXCLUDE]
            }
        }
    }

    // [END_EXCLUDE]

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
    // [END android_ai_gemini_live_function_call_handler]
}