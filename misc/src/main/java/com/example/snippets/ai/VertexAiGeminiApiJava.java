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

package com.example.snippets.ai;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

import java.util.concurrent.Executor;

public class VertexAiGeminiApiJava {

    // [START android_snippets_vertex_ai_gemini_api_model_java]
    GenerativeModel firebaseAI = FirebaseAI.getInstance(GenerativeBackend.vertexAI())
            .generativeModel("gemini-2.5-flash");

    GenerativeModelFutures model = GenerativeModelFutures.from(firebaseAI);
    // [END android_snippets_vertex_ai_gemini_api_model_java]

    void generateText(Executor executor) {
        // [START android_snippets_vertex_ai_generate_content_java]
        Content prompt = new Content.Builder()
                .addText("Write a story about a magic backpack.")
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                // ...
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
        // [END android_snippets_vertex_ai_generate_content_java]
    }
}
