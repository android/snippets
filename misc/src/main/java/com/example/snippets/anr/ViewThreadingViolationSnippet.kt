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

package com.example.snippets.anr

import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Mock/stub classes so the documentation snippets compile cleanly.
interface MyData {
    val title: String
}

interface MyRepository {
    suspend fun getData(): MyData
}

@Suppress("unused")
object ViewThreadingViolationSnippet {

    fun switchContext(
        lifecycleOwner: LifecycleOwner,
        myRepository: MyRepository,
        myTextView: TextView
    ) {
        // [START android_view_threading_violation_coroutine_kotlin]
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val data = myRepository.getData()
            withContext(Dispatchers.Main) { // Switch context to Main
                myTextView.text = data.title
            }
        }
        // [END android_view_threading_violation_coroutine_kotlin]
    }

    @RequiresApi(37)
    fun checkViewThreadingViolation() {
        // [START android_view_threading_violation_listener_kotlin]
        val listener = object : View.CalledFromWrongThreadListener {
            override fun onCalledFromWrongThread() {
                // Handle the issue, e.g. crash if this is a dev build, or log an event
                // e.g. Log.d(TAG, "CalledFromWrongThread: ${Exception().stackTraceToString()}")
                // Unregister the listener to avoid redundant notifications for the same issue
                View.unregisterCalledFromWrongThreadListener(this)
            }
        }
        View.registerCalledFromWrongThreadListener(listener)
        // [END android_view_threading_violation_listener_kotlin]
    }
}
