package com.example.snippets.profiling

import android.app.Activity
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ProfilingResult
import android.util.Log
import androidx.tracing.Trace
import androidx.core.os.requestProfiling
import androidx.core.os.SystemTraceRequestBuilder
import androidx.core.os.BufferFillPolicy
import java.util.concurrent.Executor
import java.util.function.Consumer
import kotlinx.coroutines.Dispatchers
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.asExecutor

class MainActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    sampleRecordSystemTrace()
  }

  // [START android_profiling_manager_record_system_trace_kotlin]
  @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
  fun sampleRecordSystemTrace() {
    val mainExecutor: Executor = Dispatchers.IO.asExecutor() // Your choice of executor for the callback to occur on.
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
}
