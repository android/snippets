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

package com.example.android.performance.profiling

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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.BufferFillPolicy
import androidx.core.os.JavaHeapDumpRequestBuilder
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
import com.example.android.performance.R
import java.util.ArrayList
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            // [END_EXCLUDE]
        }
        // [END android_profiling_manager_triggered_trace]

        // [START android_profiling_manager_trace_upload_job_kotlin]        // [START android_profiling_manager_trace_upload_job_kotlin]
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
        // [END android_profiling_manager_trace_upload_job_kotlin]
    }

    private object OomCaseStudySnippets {
        // [START android_profiling_manager_oom_case_study_register_trigger_kotlin]
        @RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
        fun registerOomTrigger(context: Context) {
            val profilingManager =
                context.getSystemService(ProfilingManager::class.java) ?: return
            val callbackExecutor: Executor = Executors.newSingleThreadExecutor()

            val resultCallback = Consumer<ProfilingResult> { profilingResult ->
                if (
                    profilingResult.errorCode == ProfilingResult.ERROR_NONE &&
                    profilingResult.triggerType == ProfilingTrigger.TRIGGER_TYPE_OOM
                ) {
                    val heapDumpPath = profilingResult.resultFilePath
                    // Schedule a background job to compress and upload heapDumpPath
                }
            }

            profilingManager.registerForAllProfilingResults(callbackExecutor, resultCallback)

            val oomTrigger = ProfilingTrigger.Builder(ProfilingTrigger.TRIGGER_TYPE_OOM)
                .build()
            profilingManager.addProfilingTriggers(listOf(oomTrigger))
        }
        // [END android_profiling_manager_oom_case_study_register_trigger_kotlin]

        // [START android_profiling_manager_oom_case_study_stats_reporter_kotlin]
        class StatsReporter private constructor() {
            fun interface StatsListener {
                fun onReportStats(stats: String)
            }

            private val listeners: MutableList<StatsListener> = CopyOnWriteArrayList()
            private var isReporting = false

            init {
                startReporting()
            }

            fun registerListener(listener: StatsListener) {
                if (!listeners.contains(listener)) {
                    listeners.add(listener)
                }
            }

            fun unregisterListener(listener: StatsListener) {
                listeners.remove(listener)
            }

            private fun startReporting() {
                if (isReporting) return
                isReporting = true

                Thread({
                    while (true) {
                        try {
                            // Poll every 10 seconds
                            Thread.sleep(10000)
                        } catch (e: InterruptedException) {
                            Thread.currentThread().interrupt()
                            break
                        }

                        for (listener in listeners) {
                            // Send periodic mock stats update from the background thread
                            listener.onReportStats("Update from StatsReporter background polling thread")
                        }
                    }
                }, "StatsReporterPollingThread").start()
            }

            companion object {
                private var instance: StatsReporter? = null

                @JvmStatic
                fun getInstance(): StatsReporter {
                    return instance ?: StatsReporter().also { instance = it }
                }
            }
        }
        // [END android_profiling_manager_oom_case_study_stats_reporter_kotlin]

        // [START android_profiling_manager_oom_case_study_oom_sample_activity_kotlin]
        class OomSampleActivity : Activity(), StatsReporter.StatsListener {
            // Allocate some memory.
            private val heavyData = ByteArray(30 * 1024 * 1024)

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_oom_sample)

                StatsReporter.getInstance().registerListener(this)
            }

            override fun onReportStats(stats: String) {
                // Read from heavyData so R8 doesn't strip it out as dead code during release builds
                Log.d(TAG, "Received stats in heavy leaked activity: $stats | Data size: ${heavyData.size}")
            }

            companion object {
                private const val TAG = "OomSampleActivity"
            }
        }
        // [END android_profiling_manager_oom_case_study_oom_sample_activity_kotlin]

        // [START android_profiling_manager_oom_case_study_oom_sample_activity_fixed_kotlin]
        class OomSampleActivityFixed : Activity(), StatsReporter.StatsListener {
            // Allocate some memory.
            private val heavyData = ByteArray(30 * 1024 * 1024)

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_oom_sample)

                // Register the activity as a listener
                StatsReporter.getInstance().registerListener(this)
            }

            override fun onDestroy() {
                super.onDestroy()
                // FIX: Unregister the listener to prevent the memory leak
                // when the Activity is destroyed (for example, during screen rotation).
                StatsReporter.getInstance().unregisterListener(this)
            }

            override fun onReportStats(stats: String) {
                // Read from heavyData so R8 doesn't strip it out as dead code during release builds
                Log.d(TAG, "Received stats in fixed heavy activity: $stats | Data size: ${heavyData.size}")
            }

            companion object {
                private const val TAG = "OomSampleFixed"
            }
        }
        // [END android_profiling_manager_oom_case_study_oom_sample_activity_fixed_kotlin]
    }

    private object AppDrivenProfilingSnippets {
        // [START android_profiling_manager_app_driven_record_system_trace_kotlin]
        @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
        @Composable
        fun ProfiledScreen(modifier: Modifier = Modifier) {
            // Use the application context: requestProfiling resolves the ProfilingManager
            // system service from it, so there's no reason to hand it a short-lived Activity.
            val appContext = LocalContext.current.applicationContext
            val scope = rememberCoroutineScope()

            Button(
                onClick = {
                    // Run the orchestration off the main thread. Profiling a heavy operation
                    // on the UI thread would freeze the UI (ANR) and distort the very metrics
                    // you're trying to capture.
                    //
                    // Note: this scope is tied to composition. If the user leaves this screen
                    // mid-session, the coroutine is cancelled and stopSignal.cancel() might not
                    // run, but setDurationMs() acts as a safety net and ends the profile.
                    scope.launch(Dispatchers.Default) {
                        val callbackExecutor = Dispatchers.IO.asExecutor()
                        val resultCallback = Consumer<ProfilingResult> { profilingResult ->
                            if (profilingResult.errorCode == ProfilingResult.ERROR_NONE) {
                                Log.d("ProfileTest", "Result file: ${profilingResult.resultFilePath}")
                            } else {
                                // errorMessage explains the failure (e.g., rate limiting); keep it.
                                Log.e(
                                    "ProfileTest",
                                    "Profiling failed errorCode=${profilingResult.errorCode} " +
                                        "errorMessage=${profilingResult.errorMessage}"
                                )
                            }
                        }

                        val stopSignal = CancellationSignal()
                        val requestBuilder = SystemTraceRequestBuilder().apply {
                            setCancellationSignal(stopSignal)
                            setTag("FOO") // Caller-supplied tag for identification.
                            setDurationMs(60000) // Hard cap: ends the session if cancel() never fires.
                            setBufferFillPolicy(BufferFillPolicy.RING_BUFFER)
                            setBufferSizeKb(32768)
                        }

                        // 1. Start the session. This is asynchronous system IPC. The tracing
                        //    engine takes a moment to start and allocate buffers.
                        requestProfiling(appContext, requestBuilder.build(), callbackExecutor, resultCallback)

                        // 2. The API exposes no "profiling started" signal, so pad with a short,
                        //    best-effort delay before running the code you care about. This is
                        //    approximate. Increase it on slower or heavily loaded devices.
                        delay(STARTUP_PADDING_MS)

                        // 3. The session is already recording every thread in your app. This slice
                        //    doesn't scope what's captured. It just labels this region of the
                        //    timeline so heavyOperation() is easier to find. trace { } closes the
                        //    section even if the block throws.

                        trace("MyApp:HeavyOperation") {
                            heavyOperation()
                        }

                        // 4. Stop recording. Until this fires or the setDurationMs() cap is
                        //    reached (whichever comes first), the session keeps capturing app-wide
                        //    activity.

                        stopSignal.cancel()
                    }
                }
            ) {
                Text("Run & Profile Heavy Operation")
            }
        }

        // Best-effort wait for the system trace engine to initialize before profiling.
        // There is no deterministic start callback; tune this for your target devices.
        private const val STARTUP_PADDING_MS = 100L

        fun heavyOperation() {
            // Background computations to profile.
        }
        // [END android_profiling_manager_app_driven_record_system_trace_kotlin]

        fun recordSystemTraceWithCallstacks(
            context: Context,
            callbackExecutor: Executor,
            resultCallback: Consumer<ProfilingResult>,
        ) {
            // [START android_profiling_manager_app_driven_stack_sampling_kotlin]
            val profilingManager = context.getSystemService(ProfilingManager::class.java)

            // Pass a Bundle to customize the capture, or pass null for default settings.
            val config = Bundle().apply {
                putInt("KEY_DURATION_MS", 15000) // 15 seconds
                putInt("KEY_SIZE_KB", 20480) // 20 MB buffer size

                // Collect sampled CPU call stacks alongside the system trace.
                putBoolean("KEY_COLLECT_STACK_SAMPLING", true)
                putInt("KEY_FREQUENCY_HZ", 100) // Sampling frequency in Hz
                // Optional: Set KEY_SAMPLE_BINDER_ONLY to true to sample only binder calls.
            }
            profilingManager?.requestProfiling(
                ProfilingManager.PROFILING_TYPE_SYSTEM_TRACE,
                config,
                "trace-with-callstacks",
                null,
                callbackExecutor,
                resultCallback
            )
            // [END android_profiling_manager_app_driven_stack_sampling_kotlin]
        }

        fun captureJavaHeapDump(
            appContext: Context,
            callbackExecutor: Executor,
            resultCallback: Consumer<ProfilingResult>,
        ) {
            // [START android_profiling_manager_app_driven_java_heap_dump_kotlin]
            val heapDumpRequest = JavaHeapDumpRequestBuilder()
                .setTag("memory-leak-check")
                .build()

            requestProfiling(
                appContext,
                heapDumpRequest,
                callbackExecutor,
                resultCallback
            )
            // [END android_profiling_manager_app_driven_java_heap_dump_kotlin]
        }
    }
}
