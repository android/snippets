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
import android.view.View
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.TimeUnit

private fun manageWorkEnqueue(requireContext: () -> Context) {
    // [START android_background_manage_work_enqueue]
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val myWork: WorkRequest = // ... OneTime or PeriodicWork
    // [START_EXCLUDE silent]
     */
    val myWork: WorkRequest = OneTimeWorkRequestBuilder<MyWork>().build()
    // [END_EXCLUDE]
    WorkManager.getInstance(requireContext()).enqueue(myWork)
    // [END android_background_manage_work_enqueue]
}

private fun Context.uniquePeriodicWork() {
    // [START android_background_unique_periodic_work]
    val sendLogsWorkRequest =
        PeriodicWorkRequestBuilder<SendLogsWorker>(24, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresCharging(true)
                    .build()
            )
            .build()
    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
        "sendLogs",
        ExistingPeriodicWorkPolicy.KEEP,
        sendLogsWorkRequest
    )
    // [END android_background_unique_periodic_work]
}

private fun observeWorkQuery(workManager: WorkManager, syncWorker: WorkRequest) {
    // [START android_background_observe_work_query]
    // by id
    workManager.getWorkInfoById(syncWorker.id) // ListenableFuture<WorkInfo>

    // by name
    workManager.getWorkInfosForUniqueWork("sync") // ListenableFuture<List<WorkInfo>>

    // by tag
    workManager.getWorkInfosByTag("syncTag") // ListenableFuture<List<WorkInfo>>
    // [END android_background_observe_work_query]
}

private suspend fun observeWorkFlow(
    workManager: WorkManager,
    syncWorker: WorkRequest,
    requireView: () -> View
) {
    // [START android_background_observe_work_flow]
    workManager.getWorkInfoByIdFlow(syncWorker.id)
        .collect { workInfo ->
            if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                Snackbar.make(
                    requireView(),
                    R.string.work_completed, Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    // [END android_background_observe_work_flow]
}

private fun complexWorkQueries(workManager: WorkManager) {
    // [START android_background_complex_work_queries]
    val workQuery = WorkQuery.Builder
        .fromTags(listOf("syncTag"))
        .addStates(listOf(WorkInfo.State.FAILED, WorkInfo.State.CANCELLED))
        .addUniqueWorkNames(
            listOf("preProcess", "sync")
        )
        .build()

    val workInfos: ListenableFuture<List<WorkInfo>> = workManager.getWorkInfos(workQuery)
    // [END android_background_complex_work_queries]
}

private fun cancelWork(workManager: WorkManager, syncWorker: WorkRequest) {
    // [START android_background_cancel_work]
    // by id
    workManager.cancelWorkById(syncWorker.id)

    // by name
    workManager.cancelUniqueWork("sync")

    // by tag
    workManager.cancelAllWorkByTag("syncTag")
    // [END android_background_cancel_work]
}

class SendLogsWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result = Result.success()
}
