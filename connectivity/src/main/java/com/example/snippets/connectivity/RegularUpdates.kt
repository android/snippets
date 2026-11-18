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

package com.example.snippets.connectivity

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

// Takes the Context as context, the name the page passes to WorkManager.getInstance().
private fun scheduleHeadlinesDownload(context: Context) {
    // [START android_connectivity_regular_updates_workmanager]
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .build()
    val request =
        PeriodicWorkRequestBuilder<DownloadHeadlinesWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 1L, TimeUnit.MINUTES)
            .build()
    WorkManager.getInstance(context).enqueue(request)
    // [END android_connectivity_regular_updates_workmanager]
}

// The page's Worker subclass that retrieves the latest news headlines. It's public because
// WorkManager creates workers by class name.
class DownloadHeadlinesWorker(
    context: Context,
    params: WorkerParameters,
) : Worker(context, params) {
    override fun doWork(): Result = Result.success()
}
