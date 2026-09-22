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

// [START android_background_observe_progress_worker]
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
// [END android_background_observe_progress_worker]
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import java.util.UUID

// [START android_background_observe_progress_worker]
class ProgressWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    companion object {
        const val Progress = "Progress"
        private const val delayDuration = 1L
    }

    override suspend fun doWork(): Result {
        val firstUpdate = workDataOf(Progress to 0)
        val lastUpdate = workDataOf(Progress to 100)
        setProgress(firstUpdate)
        delay(delayDuration)
        setProgress(lastUpdate)
        return Result.success()
    }
}
// [END android_background_observe_progress_worker]

private suspend fun observeProgressFlow(applicationContext: Context, requestId: UUID) {
    // [START android_background_observe_progress_flow]
    WorkManager.getInstance(applicationContext)
        // requestId is the WorkRequest id
        .getWorkInfoByIdFlow(requestId)
        .collect { workInfo: WorkInfo? ->
            if (workInfo != null) {
                val progress = workInfo.progress
                val value = progress.getInt("Progress", 0)
                // Do something with progress information
            }
        }
    // [END android_background_observe_progress_flow]
}

private suspend fun observeStopReason(workManager: WorkManager, syncWorker: WorkRequest) {
    // [START android_background_observe_stop_reason]
    workManager.getWorkInfoByIdFlow(syncWorker.id)
        .collect { workInfo ->
            if (workInfo != null) {
                val stopReason = workInfo.stopReason
                logStopReason(syncWorker.id, stopReason)
            }
        }
    // [END android_background_observe_stop_reason]
}

private fun logStopReason(id: UUID, stopReason: Int) {}
