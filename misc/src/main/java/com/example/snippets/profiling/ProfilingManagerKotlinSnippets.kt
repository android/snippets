/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.snippets.profiling

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ProfilingManager
import android.os.ProfilingResult
import android.os.ProfilingTrigger
import android.util.Log
import android.view.Choreographer
import androidx.annotation.RequiresApi
import androidx.core.os.BufferFillPolicy
import androidx.core.os.SystemTraceRequestBuilder
import androidx.core.os.requestProfiling
import androidx.tracing.Trace
import androidx.tracing.trace
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.ArrayList
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

class ProfilingManagerKotlinSnippets {
    class MainActivity : Activity() {
        companion object {
            const val TAG = "MyApp"
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            sampleRecordSystemTrace()
        }

        // [START android_profiling_manager_record_system_trace_kotlin]
        @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
        fun sampleRecordSystemTrace() {
            val mainExecutor: Executor =
                Dispatchers.IO.asExecutor() // Your choice of executor for the callback to occur on.
            val resultCallback = Consumer<ProfilingResult> { profilingResult ->
                if (profilingResult.errorCode == ProfilingResult.ERROR_NONE) {
                    Log.d(
                        "ProfileTest",
                        "Received profiling result file=" + profilingResult.resultFilePath
                    )
                } else {
                    Log.e(
                        "ProfileTest",
                        "Profiling failed errorcode=" + profilingResult.errorCode + " errormsg=" + profilingResult.errorMessage
                    )
                }
            }
            val stopSignal = CancellationSignal()

            val requestBuilder = SystemTraceRequestBuilder()
            requestBuilder.setCancellationSignal(stopSignal)
            requestBuilder.setTag("FOO") // Caller supplied tag for identification
            requestBuilder.setDurationMs(60000)
            requestBuilder.setBufferFillPolicy(BufferFillPolicy.RING_BUFFER)
            requestBuilder.setBufferSizeKb(20971520)
            requestProfiling(applicationContext, requestBuilder.build(), mainExecutor, resultCallback)

            // Wait some time for profiling to start.

            Trace.beginSection("MyApp:HeavyOperation")
            heavyOperation()
            Trace.endSection()

            // Once the interesting code section is profiled, stop profile
            stopSignal.cancel()
        }

        fun heavyOperation() {
            // Computations you want to profile
        }
        // [END android_profiling_manager_record_system_trace_kotlin]

        // [START android_profiling_manager_triggered_trace]
        fun recordWithTrigger() {
            val profilingManager = applicationContext.getSystemService(ProfilingManager::class.java)

            val triggers = ArrayList<ProfilingTrigger>()

            val triggerBuilder = ProfilingTrigger.Builder(ProfilingTrigger.TRIGGER_TYPE_APP_FULLY_DRAWN)
                .setRateLimitingPeriodHours(1)

            triggers.add(triggerBuilder.build())

            val mainExecutor: Executor = Executors.newSingleThreadExecutor()

            val resultCallback = Consumer<ProfilingResult> { profilingResult ->
                if (profilingResult.errorCode == ProfilingResult.ERROR_NONE) {
                    Log.d(
                        "ProfileTest",
                        "Received profiling result file=" + profilingResult.resultFilePath
                    )
                    setupProfileUploadWorker(profilingResult.resultFilePath)
                } else {
                    Log.e(
                        "ProfileTest",
                        "Profiling failed errorcode=" + profilingResult.errorCode + " errormsg=" + profilingResult.errorMessage
                    )
                }
            }

            profilingManager.registerForAllProfilingResults(mainExecutor, resultCallback)
            profilingManager.addProfilingTriggers(triggers)

            // [START_EXCLUDE silent]
            Choreographer.getInstance().postFrameCallback { frameTimeNanos ->
                // This will cause the TRIGGER_TYPE_APP_FULLY_DRAWN to be emitted.
                reportFullyDrawn()
            }
            // [END_EXCLUDE silent]
        }
        // [END android_profiling_manager_triggered_trace]

        // [START android_profiling_manager_triggered_trace_setup_upload_job]
        fun setupProfileUploadWorker(resultFilePath: String?) {
            // Setup job to upload the profiling result file.
        }
        // [END android_profiling_manager_triggered_trace_setup_upload_job]
    }

    // [START android_profiling_manager_trace_upload_job_kotlin]
    class MainActivityTraceUploadJob : Activity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            sampleRecordSystemTraceWithJob()
        }

        fun heavyOperation() {
            // Work you want to trace
        }

        class TraceUploadWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
            override fun doWork(): Result {
                // Perform your uploading work here
                Log.d("ProfileTest", "Uploading trace: " + inputData.getString("PROFILE_PATH"))

                return Result.success()
            }
        }

        fun setupProfileUploadWorker(profileFilepath: String?) {
            val workMgr = WorkManager.getInstance(applicationContext)
            val workRequestBuilder = OneTimeWorkRequest.Builder(TraceUploadWorker::class)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresDeviceIdle(true)
                .setRequiresCharging(true)
                .build()
            workRequestBuilder.setConstraints(constraints)

            val inputDataBuilder = Data.Builder()
            inputDataBuilder.putString("PROFILE_PATH", profileFilepath)
            workRequestBuilder.setInputData(inputDataBuilder.build())

            workMgr.enqueue(workRequestBuilder.build())
        }

        @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
        fun sampleRecordSystemTraceWithJob() {
            val mainExecutor: Executor = Dispatchers.IO.asExecutor()
            val resultCallback = Consumer<ProfilingResult> { profilingResult ->
                if (profilingResult.errorCode == ProfilingResult.ERROR_NONE) {
                    setupUploadJob(profilingResult.resultFilePath)
                } else {
                    Log.e(
                        "ProfileTest",
                        "Profiling failed errorcode=" + profilingResult.errorCode + " errormsg=" + profilingResult.errorMessage
                    )
                }
            }
            val stopSignal = CancellationSignal()

            val requestBuilder = SystemTraceRequestBuilder()
            requestBuilder.setCancellationSignal(stopSignal)
            requestBuilder.setTag("FOO")
            requestBuilder.setDurationMs(60000)
            requestBuilder.setBufferFillPolicy(BufferFillPolicy.RING_BUFFER)
            requestProfiling(applicationContext, requestBuilder.build(), mainExecutor, resultCallback)

            // Wait some time for profiling to start.

            trace("MyApp:HeavyOperation") {
                // Code to profile
                heavyOperation()
            }

            stopSignal.cancel()
        }
    }
    // [END android_profiling_manager_trace_upload_job_kotlin]
}
