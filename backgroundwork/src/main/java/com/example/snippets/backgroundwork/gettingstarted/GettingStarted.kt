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

package com.example.snippets.backgroundwork.gettingstarted

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters

private fun uploadImages() {}

// [START android_backgroundwork_getting_started_upload_worker]
class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {

        // Do the work here--in this case, upload the images.
        uploadImages()

        // Indicate whether the work finished successfully with the Result.
        return Result.success()
    }
}
// [END android_backgroundwork_getting_started_upload_worker]

private class GettingStartedSnippet(private val myContext: Context) {
    fun createAndEnqueue() {
        // [START android_backgroundwork_getting_started_work_request]
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<UploadWorker>()
                .build()
        // [END android_backgroundwork_getting_started_work_request]

        // [START android_backgroundwork_getting_started_enqueue]
        WorkManager
            .getInstance(myContext)
            .enqueue(uploadWorkRequest)
        // [END android_backgroundwork_getting_started_enqueue]
    }
}
