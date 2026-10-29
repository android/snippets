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

package com.example.snippets.backgroundwork.chainwork

import android.content.Context
import androidx.work.ArrayCreatingInputMerger
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters

class PlantWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result = Result.success()
}

private class ChainWorkSnippet(private val myContext: Context) {
    private val plantName1: OneTimeWorkRequest = OneTimeWorkRequestBuilder<PlantWorker>().build()
    private val plantName2: OneTimeWorkRequest = OneTimeWorkRequestBuilder<PlantWorker>().build()
    private val plantName3: OneTimeWorkRequest = OneTimeWorkRequestBuilder<PlantWorker>().build()
    private val cache: OneTimeWorkRequest = OneTimeWorkRequestBuilder<PlantWorker>().build()
    private val upload: OneTimeWorkRequest = OneTimeWorkRequestBuilder<PlantWorker>().build()

    fun chain() {
        // [START android_backgroundwork_chain_work_chain]
        WorkManager.getInstance(myContext)
            // Candidates to run in parallel.
            .beginWith(listOf(plantName1, plantName2, plantName3))
            // Dependent work (only runs after all previous work in chain).
            .then(cache)
            .then(upload)
            // Call enqueue to kick things off.
            .enqueue()
        // [END android_backgroundwork_chain_work_chain]
    }
}

private class ArrayCreatingInputMergerSnippet(private val constraints: Constraints) {
    fun build(): OneTimeWorkRequest {
        // [START android_backgroundwork_chain_work_array_creating_input_merger]
        val cache: OneTimeWorkRequest = OneTimeWorkRequestBuilder<PlantWorker>()
            .setInputMerger(ArrayCreatingInputMerger::class.java)
            .setConstraints(constraints)
            .build()
        // [END android_backgroundwork_chain_work_array_creating_input_merger]
        return cache
    }
}
