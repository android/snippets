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

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters

private fun myUploadOperation() {}

// [START android_backgroundwork_gcm_upload_worker]
class UploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        // Do the upload operation ...
        myUploadOperation()

        // Indicate whether the task finished successfully with the Result.
        return Result.success()
    }
}
// [END android_backgroundwork_gcm_upload_worker]

@Suppress("DEPRECATION")
private fun scheduleUpload() {
    // [START android_backgroundwork_gcm_schedule_upload]
    val uploadConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresCharging(true).build()

    val uploadTask = OneTimeWorkRequestBuilder<UploadWorker>()
        .setConstraints(uploadConstraints)
        .build()
    WorkManager.getInstance().enqueue(uploadTask)
    // [END android_backgroundwork_gcm_schedule_upload]
}
