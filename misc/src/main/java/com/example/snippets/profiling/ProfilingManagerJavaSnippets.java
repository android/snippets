package com.example.snippets.profiling;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.ProfilingManager;
import android.os.ProfilingTrigger;
import android.content.Context;
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
import androidx.annotation.RequiresApi;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.Data;
import com.example.snippets.R;

public class ProfilingManagerJavaSnippets {

  public class MainActivityJava extends Activity {

    // [START android_profiling_manager_anr_case_study_java_snippet_2]
    private static final int NETWORK_TIMEOUT_MILLISECS = 2000;
    // [END android_profiling_manager_anr_case_study_java_snippet_2]
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
                setupProfileUploadWorker(profilingResult.getResultFilePath());
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
              if (profilingResult.getErrorCode() == ProfilingResult.ERROR_NONE) {
                Log.d(
                    "ProfileTest",
                    "Received profiling result file=" + profilingResult.getResultFilePath());
                setupProfileUploadWorker(profilingResult.getResultFilePath());
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
      profilingManager.registerForAllProfilingResults(mainExecutor, resultCallback);
      profilingManager.addProfilingTriggers(triggers);

      // [START_EXCLUDE silent]
      Choreographer.getInstance().postFrameCallback((f) -> {
        // This will cause the TRIGGER_TYPE_APP_FULLY_DRAWN to be emitted.
        reportFullyDrawn();
      });
      // [END_EXCLUDE silent]
    }
    // [END android_profiling_manager_triggered_trace_java]

    // [START android_profiling_manager_anr_case_study_java_snippet_1]
    public void addANRTrigger() {
      ProfilingManager profilingManager = getApplicationContext().getSystemService(
          ProfilingManager.class);
      List<ProfilingTrigger> triggers = new ArrayList<>();
      ProfilingTrigger.Builder triggerBuilder = new ProfilingTrigger.Builder(
          ProfilingTrigger.TRIGGER_TYPE_ANR);
      triggers.add(triggerBuilder.build());
      Executor mainExecutor = Executors.newSingleThreadExecutor();
      Consumer<ProfilingResult> resultCallback =
          profilingResult -> {
            // Handle uploading trace to your back-end
          };
      profilingManager.registerForAllProfilingResults(mainExecutor, resultCallback);
      profilingManager.addProfilingTriggers(triggers);
    }
    // [END android_profiling_manager_anr_case_study_java_snippet_1]

    // [START android_profiling_manager_anr_case_study_java_snippet_2]
    public void setupButtonCallback() {
      findViewById(R.id.submit).setOnClickListener(submitButtonView -> {
        Trace.beginSection("MyApp:SubmitButton");
        onClickSubmit();
        Trace.endSection();
      });
    }

    public void onClickSubmit() {
      prepareNetworkRequest();

      boolean networkRequestSuccess = false;
      int maxAttempts = 10;
      while (!networkRequestSuccess && maxAttempts > 0) {
        networkRequestSuccess = performNetworkRequest(NETWORK_TIMEOUT_MILLISECS);
        maxAttempts--;
      }

      if (networkRequestSuccess) {
        handleNetworkResponse();
      }
    }

    boolean performNetworkRequest(int timeoutMiliseconds) {
      // [START_EXCLUDE]
      cpuIntensiveComputation(20);
      try {
        if (Math.random() < 0.2) {
          // Simulate performing a network request by waiting a random period of time
          int networkRequestTimeMs = (int) (Math.random() * timeoutMiliseconds);
          Thread.sleep(networkRequestTimeMs);
          return true;
        } else {
          // Simulate a timeout
          Thread.sleep(timeoutMiliseconds);
        }
      } catch (InterruptedException e) {
      }
      return false;
      // [END_EXCLUDE]
    }

    // [START_EXCLUDE silent]
    void cpuIntensiveComputation(int durationMs) {
      long start = System.currentTimeMillis();
      while (System.currentTimeMillis() - start < durationMs) {
      }
    }
    // [END_EXCLUDE silent]

    void prepareNetworkRequest() {
      // [START_EXCLUDE]
      cpuIntensiveComputation(1000);
      // [END_EXCLUDE]
    }

    public void handleNetworkResponse() {
      Trace.beginSection("handleNetworkResponse");
      // [START_EXCLUDE]
      cpuIntensiveComputation(2000);
      // [END_EXCLUDE]
      Trace.endSection();
    }
    // [END android_profiling_manager_anr_case_study_java_snippet_2]

    // [START android_profiling_manager_trace_upload_job_java]
    public static class TraceUploadWorker extends Worker {

      public TraceUploadWorker(
          @androidx.annotation.NonNull Context context,
          @androidx.annotation.NonNull WorkerParameters workerParams) {
        super(context, workerParams);
      }

      @androidx.annotation.NonNull
      @Override
      public Result doWork() {
        // Perform your uploading work here
        Log.d("ProfileTest", "Uploading trace: " + getInputData().getString("PROFILE_PATH"));

        return Result.success();
      }
    }

    public void setupProfileUploadWorker(String profileFilepath) {
      WorkManager workMgr = WorkManager.getInstance(getApplicationContext());
      OneTimeWorkRequest.Builder workRequestBuilder = new OneTimeWorkRequest.Builder(
          TraceUploadWorker.class);

      Constraints constraints = new Constraints.Builder()
          .setRequiredNetworkType(NetworkType.UNMETERED)
          .setRequiresDeviceIdle(true)
          .build();
      workRequestBuilder.setConstraints(constraints);

      Data.Builder inputDataBuilder = new Data.Builder();
      inputDataBuilder.putString("PROFILE_PATH", profileFilepath);
      workRequestBuilder.setInputData(inputDataBuilder.build());

      workMgr.enqueue(workRequestBuilder.build());
    }
    // [END android_profiling_manager_trace_upload_job_java]
  }
}