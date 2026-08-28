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

package com.example.compose.snippets.background

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters

private const val TAG = "BgWorkRestrictions"

// [START android_background_restrictions_schedule_work]
fun scheduleWork(context: Context) {
    val workManager = WorkManager.getInstance(context)
    val workRequest = OneTimeWorkRequestBuilder<MyWorker>()
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
        )
        .build()

    workManager.enqueue(workRequest)
}
// [END android_background_restrictions_schedule_work]

// [START android_background_restrictions_worker_triggered_content]
class MyWorker(
    appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        StringBuilder().apply {
            append("Media content has changed:\n")
            params.triggeredContentAuthorities
                .takeIf { it.isNotEmpty() }
                ?.let { authorities ->
                    append("Authorities: ${authorities.joinToString(", ")}\n")
                    append(params.triggeredContentUris.joinToString("\n"))
                } ?: append("(No content)")
            Log.i(TAG, toString())
        }
        return Result.success()
    }
}
// [END android_background_restrictions_worker_triggered_content]
