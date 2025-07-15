package com.example.snippets.profiling;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import java.util.function.Consumer;
import java.util.concurrent.Executor;
import android.os.ProfilingResult;
import java.util.concurrent.Executors;
import android.os.CancellationSignal;
import androidx.tracing.Trace;
import androidx.core.os.Profiling;
import androidx.core.os.SystemTraceRequestBuilder;
import androidx.core.os.BufferFillPolicy;

public class ProfilingManagerJavaSnippets {
  public class MainActivityJava extends Activity {

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
  }
}
