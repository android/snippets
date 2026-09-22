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
import androidx.concurrent.futures.await
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.UUID

private const val PHOTO_UPLOAD_WORK_NAME = "photo_upload"

// [START android_background_update_photo_upload_work]
suspend fun updatePhotoUploadWork(context: Context) {
    // Get instance of WorkManager.
    val workManager = WorkManager.getInstance(context)

    // Retrieve the work request ID. In this example, the work being updated is unique
    // work so we can retrieve the ID using the unique work name.
    val photoUploadWorkInfoList = workManager.getWorkInfosForUniqueWork(
        PHOTO_UPLOAD_WORK_NAME
    ).await()

    val existingWorkRequestId = photoUploadWorkInfoList.firstOrNull()?.id ?: return

    // Update the constraints of the WorkRequest to not require a charging device.
    val newConstraints = Constraints.Builder()
        // Add other constraints as required here.
        .setRequiresCharging(false)
        .build()

    // Create new WorkRequest from existing Worker, new constraints, and the id of the old WorkRequest.
    val updatedWorkRequest: WorkRequest =
        OneTimeWorkRequestBuilder<MyWorker>()
            .setConstraints(newConstraints)
            .setId(existingWorkRequestId)
            .build()

    // Pass the new WorkRequest to updateWork().
    workManager.updateWork(updatedWorkRequest)
}
// [END android_background_update_photo_upload_work]

private fun trackWorkGeneration(context: Context, oldWorkRequestId: UUID) {
    // [START android_background_track_work_generation]
    // Get instance of WorkManager.
    val workManager = WorkManager.getInstance(context)

    // Retrieve WorkInfo instance.
    val workInfo = workManager.getWorkInfoById(oldWorkRequestId).get()

    // Call getGeneration to retrieve the generation.
    val generation = workInfo?.generation
    // [END android_background_track_work_generation]
}

private class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result = Result.success()
}
