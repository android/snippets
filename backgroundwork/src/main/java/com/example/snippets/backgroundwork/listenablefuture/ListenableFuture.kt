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

package com.example.snippets.backgroundwork.listenablefuture

import android.content.Context
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.guava.await

private class QueryResult

private fun getQueryResult(): QueryResult = QueryResult()

private class AddCallbackSnippet(private val context: Context) {
    fun addCallback(queryResultFuture: ListenableFuture<QueryResult>) {
        // [START android_backgroundwork_listenablefuture_add_callback]
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        val future: ListenableFuture<QueryResult> = ...
        // [START_EXCLUDE silent]
         */
        val future = queryResultFuture
        // [END_EXCLUDE]
        Futures.addCallback(
            future,
            object : FutureCallback<QueryResult> {
                override fun onSuccess(result: QueryResult) {
                    // handle success.
                }

                override fun onFailure(t: Throwable) {
                    // handle failure.
                }
            },
            // causes the callbacks to be executed on the main (UI) thread.
            context.mainExecutor
        )
        // [END android_backgroundwork_listenablefuture_add_callback]
    }
}

private class AwaitSnippet {
    suspend fun awaitResult(queryResultFuture: ListenableFuture<QueryResult>): QueryResult {
        // [START android_backgroundwork_listenablefuture_await]
        // import kotlinx.coroutines.guava.await

        // ...

        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        val future: ListenableFuture<QueryResult> = ...
        // [START_EXCLUDE silent]
         */
        val future = queryResultFuture
        // [END_EXCLUDE]
        val queryResult = future.await() // suspends awaiting success.
        // [END android_backgroundwork_listenablefuture_await]
        return queryResult
    }
}

private object ImmediateFutureSnippet {
    // [START android_backgroundwork_listenablefuture_immediate_future]
    fun getResult(): ListenableFuture<QueryResult> {
        try {
            val queryResult = getQueryResult()
            return Futures.immediateFuture(queryResult)
        } catch (e: Exception) {
            return Futures.immediateFailedFuture(e)
        }
    }
    // [END android_backgroundwork_listenablefuture_immediate_future]
}
