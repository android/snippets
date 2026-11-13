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
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.ListenableWorker.Result
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.TimeUnit

private object FirebaseListenableWorker {
    // [START android_backgroundwork_firebase_listenable_worker]
    // import android.content.Context
    // import androidx.work.ListenableWorker
    // import androidx.work.ListenableWorker.Result
    // import androidx.work.WorkerParameters
    // import com.google.common.util.concurrent.ListenableFuture

    class MyWorker(appContext: Context, params: WorkerParameters) :
        ListenableWorker(appContext, params) {

        override fun startWork(): ListenableFuture<ListenableWorker.Result> {
            // Do your work here.
            TODO("Return a ListenableFuture<Result>")
        }

        override fun onStopped() {
            // Cleanup because you are being stopped.
        }
    }
    // [END android_backgroundwork_firebase_listenable_worker]
}

private object FirebaseWorker {
    // [START android_backgroundwork_firebase_worker]
    // import android.content.Context;
    // import androidx.work.Data;
    // import androidx.work.ListenableWorker.Result;
    // import androidx.work.Worker;
    // import androidx.work.WorkerParameters;

    class MyWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
        override fun doWork(): Result {
            TODO("Return a Result")
        }

        override fun onStopped() {
            super.onStopped()
            TODO("Cleanup, because you are being stopped")
        }
    }
    // [END android_backgroundwork_firebase_worker]
}

private fun createWorkData() {
    // [START android_backgroundwork_firebase_work_data]
    // import androidx.work.workDataOf
    val data = workDataOf("some_key" to "some_val")
    // [END android_backgroundwork_firebase_work_data]
}

private fun createConstraints() {
    // [START android_backgroundwork_firebase_constraints]
    // import androidx.work.*

    val constraints: Constraints = Constraints.Builder().apply {
        setRequiredNetworkType(NetworkType.CONNECTED)
        setRequiresCharging(true)
    }.build()
    // [END android_backgroundwork_firebase_constraints]
}

private fun createOneTimeWorkRequest(input: Data) {
    // [START android_backgroundwork_firebase_one_time_work_request]
    // import androidx.work.*
    // import java.util.concurrent.TimeUnit

    val constraints: Constraints = TODO("Define constraints as above")
    val request: OneTimeWorkRequest =
        // Tell which work to execute.
        OneTimeWorkRequestBuilder<MyWorker>()
            // Sets the input data for the ListenableWorker.
            .setInputData(input)
            // If you want to delay the start of work by 60 seconds.
            .setInitialDelay(60, TimeUnit.SECONDS)
            // Set a backoff criteria to be used when retry-ing.
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30000, TimeUnit.MILLISECONDS)
            // Set additional constraints.
            .setConstraints(constraints)
            .build()
    // [END android_backgroundwork_firebase_one_time_work_request]
}

private fun createPeriodicWorkRequest(input: Data) {
    // [START android_backgroundwork_firebase_periodic_work_request]
    val constraints: Constraints = TODO("Define constraints as above")
    val request: PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<MyWorker>(15, TimeUnit.MINUTES)
            // Sets the input data for the ListenableWorker.
            .setInputData(input)
            // Other setters.
            .build()
    // [END android_backgroundwork_firebase_periodic_work_request]
}

private fun enqueueUniqueWork(myContext: Context) {
    // [START android_backgroundwork_firebase_enqueue_unique_work]
    // import androidx.work.*

    val request: OneTimeWorkRequest = TODO("A WorkRequest")
    WorkManager.getInstance(myContext)
        .enqueueUniqueWork("my-unique-name", ExistingWorkPolicy.KEEP, request)
    // [END android_backgroundwork_firebase_enqueue_unique_work]
}

private fun cancelUniqueWork(myContext: Context) {
    // [START android_backgroundwork_firebase_cancel_unique_work]
    // import androidx.work.WorkManager
    WorkManager.getInstance(myContext).cancelUniqueWork("my-unique-name")
    // [END android_backgroundwork_firebase_cancel_unique_work]
}
