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

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import androidx.work.multiprocess.RemoteCoroutineWorker
import androidx.work.multiprocess.RemoteListenableWorker.ARGUMENT_CLASS_NAME
import androidx.work.multiprocess.RemoteListenableWorker.ARGUMENT_PACKAGE_NAME
import androidx.work.multiprocess.RemoteWorkerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private object SimpleCoroutineDownloadWorker {
    // [START android_background_coroutine_download_worker]
    class CoroutineDownloadWorker(
        context: Context,
        params: WorkerParameters
    ) : CoroutineWorker(context, params) {

        override suspend fun doWork(): Result {
            val data = downloadSynchronously("https://www.google.com")
            saveData(data)
            return Result.success()
        }
    }
    // [END android_background_coroutine_download_worker]
}

private object WithContextCoroutineDownloadWorker {
    // [START android_background_coroutine_download_worker_with_context]
    class CoroutineDownloadWorker(
        context: Context,
        params: WorkerParameters
    ) : CoroutineWorker(context, params) {

        override suspend fun doWork(): Result {
            return withContext(Dispatchers.IO) {
                val data = downloadSynchronously("https://www.google.com")
                saveData(data)
                Result.success()
            }
        }
    }
    // [END android_background_coroutine_download_worker_with_context]
}

private fun remoteCoroutineWorkerRequest(): OneTimeWorkRequest {
    // [START android_background_remote_coroutine_worker_request]
    val PACKAGE_NAME = "com.example.background.multiprocess"

    val serviceName = RemoteWorkerService::class.java.name
    val componentName = ComponentName(PACKAGE_NAME, serviceName)

    val data: Data = Data.Builder()
        .putString(ARGUMENT_PACKAGE_NAME, componentName.packageName)
        .putString(ARGUMENT_CLASS_NAME, componentName.className)
        .build()

    return OneTimeWorkRequest.Builder(ExampleRemoteCoroutineWorker::class.java)
        .setInputData(data)
        .build()
    // [END android_background_remote_coroutine_worker_request]
}

private fun downloadSynchronously(url: String): String = ""
private fun saveData(data: String) {}

@SuppressLint("WorkerHasAPublicModifier")
private class ExampleRemoteCoroutineWorker(
    context: Context,
    parameters: WorkerParameters
) : RemoteCoroutineWorker(context, parameters) {
    override suspend fun doRemoteWork(): Result = Result.success()
}
