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

package com.example.snippets.backgroundwork.threading

import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.IOException
import java.util.concurrent.Executors

private class ConfigureExecutorSnippet(private val context: Context) {
    fun initialize() {
        // [START android_backgroundwork_worker_threading_executor]
        WorkManager.initialize(
            context,
            Configuration.Builder()
                // Uses a fixed thread pool of size 8 threads.
                .setExecutor(Executors.newFixedThreadPool(8))
                .build()
        )
        // [END android_backgroundwork_worker_threading_executor]
    }
}

@Throws(IOException::class)
private fun downloadSynchronously(url: String) {}

private object BasicDownloadWorker {
    // [START android_backgroundwork_worker_threading_download_worker]
    class DownloadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

        override fun doWork(): ListenableWorker.Result {
            repeat(100) {
                try {
                    downloadSynchronously("https://www.google.com")
                } catch (e: IOException) {
                    return ListenableWorker.Result.failure()
                }
            }

            return ListenableWorker.Result.success()
        }
    }
    // [END android_backgroundwork_worker_threading_download_worker]
}

private object StoppableDownloadWorker {
    // [START android_backgroundwork_worker_threading_stopped_worker]
    class DownloadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

        override fun doWork(): ListenableWorker.Result {
            for (i in 0 until 100) {
                if (isStopped) {
                    break
                }

                try {
                    downloadSynchronously("https://www.google.com")
                } catch (e: IOException) {
                    return ListenableWorker.Result.failure()
                }
            }

            return ListenableWorker.Result.success()
        }
    }
    // [END android_backgroundwork_worker_threading_stopped_worker]
}
