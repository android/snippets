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

package com.example.android.coroutines

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun show(doc: String) {}
suspend fun fetchDoc(id: Int): String = "Doc $id"
private fun placeholder(): Boolean = true

// [START android_kotlin_coroutines_adv_suspend]
suspend fun fetchDocs() { // Dispatchers.Main
    val result = get("https://developer.android.com") // Dispatchers.IO for `get`
    show(result) // Dispatchers.Main
}

suspend fun get(url: String) = withContext(Dispatchers.IO) {
    /* ... */ /* [START_EXCLUDE silent] */ ; "result" /* [END_EXCLUDE] */
}
// [END android_kotlin_coroutines_adv_suspend]

class CoroutinesAdvMainSafety {
    // [START android_kotlin_coroutines_adv_mainsafety]
    suspend fun fetchDocs() { // Dispatchers.Main
        val result = get("developer.android.com") // Dispatchers.Main
        show(result) // Dispatchers.Main
    }

    suspend fun get(url: String) = // Dispatchers.Main
        withContext(Dispatchers.IO) { // Dispatchers.IO (main-safety block)
            /* perform network IO here */ // Dispatchers.IO (main-safety block)
            // [START_EXCLUDE silent]
            "result"
            // [END_EXCLUDE]
        } // Dispatchers.Main
    // [END android_kotlin_coroutines_adv_mainsafety]
}

class ParallelDecomposition {
    // [START android_kotlin_coroutines_adv_parallel]
    suspend fun fetchTwoDocs() =
        coroutineScope {
            val deferredOne = async { fetchDoc(1) }
            val deferredTwo = async { fetchDoc(2) }
            deferredOne.await()
            deferredTwo.await()
        }
    // [END android_kotlin_coroutines_adv_parallel]
}

class ParallelDecompositionCollection {
    // [START android_kotlin_coroutines_adv_parallel_collection]
    suspend fun fetchTwoDocs() = // called on any Dispatcher (any thread, possibly Main)
        coroutineScope {
            val deferreds = listOf( // fetch two docs at the same time
                async { fetchDoc(1) }, // async returns a result for the first doc
                async { fetchDoc(2) } // async returns a result for the second doc
            )
            deferreds.awaitAll() // use awaitAll to wait for both network requests
        }
    // [END android_kotlin_coroutines_adv_parallel_collection]
}

// [START android_kotlin_coroutines_adv_scope]
class ExampleClass {

    // Job and Dispatcher are combined into a CoroutineContext which
    // will be discussed shortly
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun exampleMethod() {
        // Starts a new coroutine within the scope
        scope.launch {
            // New coroutine that can call suspend functions
            fetchDocs()
        }
    }

    fun cleanUp() {
        // Cancel the scope to cancel ongoing coroutines work
        scope.cancel()
    }
}
// [END android_kotlin_coroutines_adv_scope]

private object JobSnippet {
    // [START android_kotlin_coroutines_adv_job]
    class ExampleClass {
        // [START_EXCLUDE]
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        val condition = true
        // [END_EXCLUDE]
        fun exampleMethod() {
            // Handle to the coroutine, you can control its lifecycle
            val job = scope.launch {
                // New coroutine
            }

            if (condition) {
                // Cancel the coroutine started above, this doesn't affect the scope
                // this coroutine was launched in
                job.cancel()
            }
        }
    }
    // [END android_kotlin_coroutines_adv_job]
}

private object ContextSnippet {
    // [START android_kotlin_coroutines_adv_context]
    class ExampleClass {
        val scope = CoroutineScope(Job() + Dispatchers.Main)

        fun exampleMethod() {
            // Starts a new coroutine on Dispatchers.Main as it's the scope's default
            val job1 = scope.launch {
                // New coroutine with CoroutineName = "coroutine" (default)
            }

            // Starts a new coroutine on Dispatchers.Default
            val job2 = scope.launch(Dispatchers.Default + CoroutineName("BackgroundCoroutine")) {
                // New coroutine with CoroutineName = "BackgroundCoroutine" (overridden)
            }
        }
    }
    // [END android_kotlin_coroutines_adv_context]
}
