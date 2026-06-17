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

package com.example.snippets.engage

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

@SuppressWarnings("unused")
// [START android_engage_publisher_implementation]
object EngagePublisher {

    fun publishPeriodically(context: Context, publishType: String) {
        val workRequest = PeriodicWorkRequestBuilder<EngageWorker>(Constants.REPEAT_INTERVAL, TimeUnit.HOURS)
            .setInputData(workDataOf(Constants.PUBLISH_TYPE_KEY to publishType))
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("EngagePeriodic", ExistingPeriodicWorkPolicy.KEEP, workRequest)
    }

    fun publishOneTime(context: Context, publishType: String) {

        val workRequest = OneTimeWorkRequestBuilder<EngageWorker>()
            .setInputData(workDataOf(Constants.PUBLISH_TYPE_KEY to publishType))
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork("EngageOneTime", ExistingWorkPolicy.REPLACE, workRequest)
    }
}
// [END android_engage_publisher_implementation]
