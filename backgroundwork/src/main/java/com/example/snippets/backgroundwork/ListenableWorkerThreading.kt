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

package com.example.snippets.backgroundwork

import android.content.ComponentName
import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import androidx.work.multiprocess.RemoteListenableWorker
import androidx.work.multiprocess.RemoteListenableWorker.ARGUMENT_CLASS_NAME
import androidx.work.multiprocess.RemoteListenableWorker.ARGUMENT_PACKAGE_NAME
import androidx.work.multiprocess.RemoteWorkerService
import com.google.common.util.concurrent.ListenableFuture
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private object CallbackWorkerBasic {
    // [START android_background_callback_worker]
    class CallbackWorker(
        context: Context,
        params: WorkerParameters
    ) : ListenableWorker(context, params) {
        override fun startWork(): ListenableFuture<Result> {
            return CallbackToFutureAdapter.getFuture { completer ->
                val callback = object : Callback {
                    var successes = 0

                    override fun onFailure(call: Call, e: IOException) {
                        completer.setException(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        successes++
                        if (successes == 100) {
                            completer.set(Result.success())
                        }
                    }
                }

                repeat(100) {
                    downloadAsynchronously("https://example.com", callback)
                }

                callback
            }
        }
    }
    // [END android_background_callback_worker]
}

private object CallbackWorkerCancellation {
    // [START android_background_callback_worker_cancellation]
    class CallbackWorker(
        context: Context,
        params: WorkerParameters
    ) : ListenableWorker(context, params) {
        override fun startWork(): ListenableFuture<Result> {
            return CallbackToFutureAdapter.getFuture { completer ->
                val callback = object : Callback {
                    var successes = 0

                    override fun onFailure(call: Call, e: IOException) {
                        completer.setException(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        ++successes
                        if (successes == 100) {
                            completer.set(Result.success())
                        }
                    }
                }

                completer.addCancellationListener(cancelDownloadsRunnable, executor)

                repeat(100) {
                    downloadAsynchronously("https://example.com", callback)
                }

                callback
            }
        }
    }
    // [END android_background_callback_worker_cancellation]
}

private fun remoteListenableWorkerRequest(): OneTimeWorkRequest {
    // [START android_background_remote_listenable_worker_request]
    val PACKAGE_NAME = "com.example.background.multiprocess"

    val serviceName = RemoteWorkerService::class.java.name
    val componentName = ComponentName(PACKAGE_NAME, serviceName)

    val data: Data = Data.Builder()
        .putString(ARGUMENT_PACKAGE_NAME, componentName.packageName)
        .putString(ARGUMENT_CLASS_NAME, componentName.className)
        .build()

    return OneTimeWorkRequest.Builder(ExampleRemoteListenableWorker::class.java)
        .setInputData(data)
        .build()
    // [END android_background_remote_listenable_worker_request]
}

private interface Callback {
    fun onFailure(call: Call, e: IOException)
    fun onResponse(call: Call, response: Response)
}

private class Call
private class Response

private fun downloadAsynchronously(url: String, callback: Callback) {}
private val cancelDownloadsRunnable = Runnable {}
private val executor: Executor = Executors.newSingleThreadExecutor()

private class ExampleRemoteListenableWorker(
    context: Context,
    parameters: WorkerParameters
) : RemoteListenableWorker(context, parameters) {
    override fun startRemoteWork(): ListenableFuture<Result> =
        CallbackToFutureAdapter.getFuture { it.set(Result.success()) }
}
