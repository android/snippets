package com.example.snippets.profiling;

import android.app.Activity;
import android.os.Bundle;
import android.os.ProfilingManager;
import android.os.ProfilingTrigger;
import android.util.Log;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.ProfilingResult;
import java.util.concurrent.Executors;
import android.os.CancellationSignal;
import android.view.Choreographer;
import androidx.tracing.Trace;
import androidx.core.os.Profiling;
import androidx.core.os.SystemTraceRequestBuilder;
import androidx.core.os.BufferFillPolicy;

public class ProfilingManagerJavaSnippets {
  public class MainActivityJava extends Activity {

    public static final String TAG = "ProfilingManager";

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      sampleRecordSystemTrace();
    }

    // [START android_profiling_manager_record_system_trace_java]
    void heavyOperation() {
      // Computations you want to profile
    }

    void sampleRecordSystemTrace() {
      Executor mainExecutor = Executors.newSingleThreadExecutor();
      Consumer<ProfilingResult> resultCallback =
          new Consumer<ProfilingResult>() {
            @Override
            public void accept(ProfilingResult profilingResult) {
              if (profilingResult.getErrorCode() == ProfilingResult.ERROR_NONE) {
                Log.d(
                    "ProfileTest",
                    "Received profiling result file=" + profilingResult.getResultFilePath());
              } else {
                Log.e(
                    "ProfileTest",
                    "Profiling failed errorcode="

                        + profilingResult.getErrorCode()
                        + " errormsg="
                        + profilingResult.getErrorMessage());
              }
            }
          };
      CancellationSignal stopSignal = new CancellationSignal();

      SystemTraceRequestBuilder requestBuilder = new SystemTraceRequestBuilder();
      requestBuilder.setCancellationSignal(stopSignal);
      requestBuilder.setTag("FOO");
      requestBuilder.setDurationMs(60000);
      requestBuilder.setBufferFillPolicy(BufferFillPolicy.RING_BUFFER);
      requestBuilder.setBufferSizeKb(20971520);
      Profiling.requestProfiling(getApplicationContext(), requestBuilder.build(), mainExecutor,
          resultCallback);

      // Wait some time for profiling to start.

      Trace.beginSection("MyApp:HeavyOperation");
      heavyOperation();
      Trace.endSection();

      // Once the interesting code section is profiled, stop profile
      stopSignal.cancel();
    }
    // [END android_profiling_manager_record_system_trace_java]

    // [START android_profiling_manager_triggered_trace_java]
    public void recordWithTrigger() {
      ProfilingManager profilingManager = getApplicationContext().getSystemService(
          ProfilingManager.class);
      List<ProfilingTrigger> triggers = new ArrayList<>();
      ProfilingTrigger.Builder triggerBuilder = new ProfilingTrigger.Builder(
          ProfilingTrigger.TRIGGER_TYPE_APP_FULLY_DRAWN);
      triggerBuilder.setRateLimitingPeriodHours(1);
      triggers.add(triggerBuilder.build());

      Executor mainExecutor = Executors.newSingleThreadExecutor();
      Consumer<ProfilingResult> resultCallback =
          new Consumer<ProfilingResult>() {
            @Override
            public void accept(ProfilingResult profilingResult) {
              // ...
              if (profilingResult.getErrorCode() == ProfilingResult.ERROR_NONE) {
                Log.d(TAG,
                    "Received profiling result. file: " + profilingResult.getResultFilePath());
              }
            }
          };
      profilingManager.registerForAllProfilingResults(mainExecutor, resultCallback);
      profilingManager.addProfilingTriggers(triggers);

      Choreographer.getInstance().postFrameCallback((f) -> {
        // This will cause the TRIGGER_TYPE_APP_FULLY_DRAWN to be emitted.
        reportFullyDrawn();
      });
    }
    // [END android_profiling_manager_triggered_trace_java]

    
  }
}
