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

import com.google.adk.kt.agents.Instruction
import com.google.adk.kt.agents.LlmAgent
import com.google.adk.kt.annotations.Param
import com.google.adk.kt.annotations.Tool
import com.google.adk.kt.models.Gemini
import com.google.adk.kt.models.mlkit.GenaiPrompt
import com.google.adk.kt.runners.InMemoryRunner
import com.google.adk.kt.sessions.InMemorySessionService
import com.google.adk.kt.types.Content
import com.google.adk.kt.types.Part
import com.google.adk.kt.types.Role
import com.google.mlkit.genai.prompt.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// [START android_ai_adk_define_agent]
class TimeService {
    /** Mock tool implementation */
    @Tool
    fun getCurrentTime(
        @Param("Name of the city to get the time for") city: String
    ): Map<String, String> {
        return mapOf("city" to city, "time" to "The time is 10:30am.")
    }
}

object HelloTimeAgent {
    @JvmField
    val rootAgent = LlmAgent(
        name = "hello_time_agent",
        description = "Tells the current time in a specified city.",
        model = Gemini(
            name = "gemini-flash-latest",
            apiKey = System.getenv("GOOGLE_API_KEY")
                ?: error("GOOGLE_API_KEY environment variable not set."),
        ),
        instruction = Instruction(
            "You are a helpful assistant that tells the current time in a city. " +
                "Use the 'getCurrentTime' tool for this purpose."
        ),
        tools = TimeService().generatedTools(),
    )
}
// [END android_ai_adk_define_agent]

private fun runAgentExample(scope: CoroutineScope) {
    // [START android_ai_adk_run_agent]
    // Create a runner and session service
    val sessionService = InMemorySessionService()
    val runner = InMemoryRunner(
        agent = HelloTimeAgent.rootAgent,
        sessionService = sessionService,
    )
    // Call the agent from a coroutine (e.g. in a ViewModel or Activity)
    scope.launch {
        runner.runAsync(
            userId = "user-123",
            sessionId = "session-123",
            newMessage = Content(
                role = Role.USER,
                parts = listOf(Part(text = "What time is it in New York?")),
            ),
        ).collect { event ->
            val text = event.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                // Update your UI with the agent's response
            }
        }
    }
    // [END android_ai_adk_run_agent]
}

private fun onDeviceModelExample(mockGenerativeModel: GenerativeModel) {
    // [START android_ai_adk_on_device_models]
    // Create an ML Kit GenerativeModel for on-device inference
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val generativeModel: GenerativeModel = // ... initialize using ML Kit
    // [START_EXCLUDE silent]
     */
    val generativeModel: GenerativeModel = mockGenerativeModel
    // [END_EXCLUDE]
    val onDeviceModel = GenaiPrompt.create(
        generativeModel = generativeModel,
        name = "gemini-nano",
    )
    val agent = LlmAgent(
        name = "on_device_agent",
        model = onDeviceModel,
        instruction = Instruction("You are a helpful assistant."),
    )
    // [END android_ai_adk_on_device_models]
}
