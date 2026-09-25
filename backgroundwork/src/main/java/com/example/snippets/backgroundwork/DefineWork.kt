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
import android.app.Notification
import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

private fun enqueueWorkSample(myContext: Context) {
    // [START android_background_enqueue_work_request]
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val myWorkRequest = ...
    // [START_EXCLUDE silent]
     */
    val myWorkRequest = OneTimeWorkRequestBuilder<MyWork>().build()
    // [END_EXCLUDE]
    WorkManager.getInstance(myContext).enqueue(myWorkRequest)
    // [END android_background_enqueue_work_request]
}

private fun scheduleOneTimeWorkFrom() {
    // [START android_background_schedule_one_time_work_from]
    val myWorkRequest = OneTimeWorkRequest.from(MyWork::class.java)
    // [END android_background_schedule_one_time_work_from]
}

private fun scheduleOneTimeWorkBuilder() {
    // [START android_background_schedule_one_time_work_builder]
    val uploadWorkRequest: WorkRequest =
        OneTimeWorkRequestBuilder<MyWork>()
            // Additional configuration.
            .build()
    // [END android_background_schedule_one_time_work_builder]
}

private fun expeditedWorkRequest(context: Context) {
    // [START android_background_expedited_work_request]
    val request = OneTimeWorkRequestBuilder<SyncWorker>()
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .build()

    WorkManager.getInstance(context)
        .enqueue(request)
    // [END android_background_expedited_work_request]
}

// [START android_background_expedited_coroutine_worker]
class ExpeditedWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            NOTIFICATION_ID, createNotification()
        )
    }

    override suspend fun doWork(): Result {
        TODO()
    }

    private fun createNotification(): Notification {
        TODO()
    }

    // [START_EXCLUDE silent]
    companion object {
        private const val NOTIFICATION_ID = 1
    }
    // [END_EXCLUDE]
}
// [END android_background_expedited_coroutine_worker]

private fun schedulePeriodicWork() {
    // [START android_background_periodic_work_request]
    val saveRequest =
        PeriodicWorkRequestBuilder<SaveImageToFileWorker>(1, TimeUnit.HOURS)
            // Additional configuration.
            .build()
    // [END android_background_periodic_work_request]
}

private fun schedulePeriodicWorkFlex() {
    // [START android_background_periodic_work_request_flex]
    val myUploadWork = PeriodicWorkRequestBuilder<SaveImageToFileWorker>(
        1, TimeUnit.HOURS, // repeatInterval (the period cycle).
        15, TimeUnit.MINUTES
    ) // flexInterval.
        .build()
    // [END android_background_periodic_work_request_flex]
}

private fun workConstraints() {
    // [START android_background_work_constraints]
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresCharging(true)
        .build()

    val myWorkRequest: WorkRequest =
        OneTimeWorkRequestBuilder<MyWork>()
            .setConstraints(constraints)
            .build()
    // [END android_background_work_constraints]
}

private fun delayedWork() {
    // [START android_background_delayed_work]
    val myWorkRequest = OneTimeWorkRequestBuilder<MyWork>()
        .setInitialDelay(10, TimeUnit.MINUTES)
        .build()
    // [END android_background_delayed_work]
}

private fun retryBackoffPolicy() {
    // [START android_background_retry_backoff_policy]
    val myWorkRequest = OneTimeWorkRequestBuilder<MyWork>()
        .setBackoffCriteria(
            BackoffPolicy.LINEAR,
            WorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        )
        .build()
    // [END android_background_retry_backoff_policy]
}

private fun tagWork() {
    // [START android_background_tag_work]
    val myWorkRequest = OneTimeWorkRequestBuilder<MyWork>()
        .addTag("cleanup")
        .build()
    // [END android_background_tag_work]
}

// [START android_background_assign_input_data]
// Define the Worker requiring input.
class UploadWork(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val imageUriInput =
            inputData.getString("IMAGE_URI") ?: return Result.failure()

        uploadFile(imageUriInput)
        return Result.success()
    }
    // [START_EXCLUDE]
    private fun uploadFile(uri: String) {}
    // [END_EXCLUDE]
}

// Create a WorkRequest for your Worker and sending it input.
val myUploadWork = OneTimeWorkRequestBuilder<UploadWork>()
    .setInputData(
        workDataOf(
            "IMAGE_URI" to "http://..."
        )
    )
    .build()
// [END android_background_assign_input_data]

@SuppressLint("WorkerHasAPublicModifier")
private class MyWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result = Result.success()
}

@SuppressLint("WorkerHasAPublicModifier")
private class SyncWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result = Result.success()
}

@SuppressLint("WorkerHasAPublicModifier")
private class SaveImageToFileWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result = Result.success()
}
