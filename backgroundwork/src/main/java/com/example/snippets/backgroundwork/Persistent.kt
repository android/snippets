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
import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters

private object PersistentWorkSnippets {
    object Constants {
        const val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"
        const val TAG_OUTPUT = "OUTPUT"
    }

    class CleanupWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
        override fun doWork(): Result = Result.success()
    }

    class WaterColorFilterWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
        override fun doWork(): Result = Result.success()
    }

    class GrayScaleFilterWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
        override fun doWork(): Result = Result.success()
    }

    class BlurEffectFilterWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
        override fun doWork(): Result = Result.success()
    }

    class SaveImageToGalleryWorker(ctx: Context, params: WorkerParameters) :
        Worker(ctx, params) {
        override fun doWork(): Result = Result.success()
    }

    inline fun <reified T : ListenableWorker> workRequest(tag: String): OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<T>().addTag(tag).build()

    @SuppressLint("EnqueueWork")
    fun chainWork(context: Context, save: Boolean) {
        // [START android_backgroundwork_persistent_work_chaining]
        val continuation = WorkManager.getInstance(context)
            .beginUniqueWork(
                Constants.IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker::class.java)
            ).then(OneTimeWorkRequest.from(WaterColorFilterWorker::class.java))
            .then(OneTimeWorkRequest.from(GrayScaleFilterWorker::class.java))
            .then(OneTimeWorkRequest.from(BlurEffectFilterWorker::class.java))
            .then(
                if (save) {
                    workRequest<SaveImageToGalleryWorker>(tag = Constants.TAG_OUTPUT)
                } else /* upload */ {
                    workRequest<UploadWorker>(tag = Constants.TAG_OUTPUT)
                }
            )
        // [END android_backgroundwork_persistent_work_chaining]
    }
}
