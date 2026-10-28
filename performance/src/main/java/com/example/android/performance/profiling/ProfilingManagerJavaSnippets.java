package com.example.android.performance.profiling;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ProfilingManager;
import android.os.ProfilingResult;
import android.os.ProfilingTrigger;
import android.util.Log;
import android.view.Choreographer;
import androidx.annotation.RequiresApi;
import androidx.core.os.BufferFillPolicy;
import androidx.core.os.JavaHeapDumpRequestBuilder;
import androidx.core.os.Profiling;
import androidx.core.os.ProfilingRequest;
import androidx.core.os.SystemTraceRequestBuilder;
import androidx.tracing.Trace;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.android.performance.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

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
      // [END_EXCLUDE]
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
    // [END_EXCLUDE]

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

  private interface OomCaseStudySnippets {
    // [START android_profiling_manager_oom_case_study_register_trigger_java]
    @RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
    public static void registerOomTrigger(Context context) {
        ProfilingManager profilingManager = context.getSystemService(ProfilingManager.class);
        if (profilingManager == null) return;
        Executor callbackExecutor = Executors.newSingleThreadExecutor();

        Consumer<ProfilingResult> resultCallback =
            profilingResult -> {
                if (profilingResult.getErrorCode() == ProfilingResult.ERROR_NONE
                    && profilingResult.getTriggerType() == ProfilingTrigger.TRIGGER_TYPE_OOM) {
                    String heapDumpPath = profilingResult.getResultFilePath();
                    // Schedule a background job to compress and upload heapDumpPath
                }
            };

        profilingManager.registerForAllProfilingResults(callbackExecutor, resultCallback);

        ProfilingTrigger oomTrigger =
            new ProfilingTrigger.Builder(ProfilingTrigger.TRIGGER_TYPE_OOM)
                .build();
        profilingManager.addProfilingTriggers(Collections.singletonList(oomTrigger));
    }
    // [END android_profiling_manager_oom_case_study_register_trigger_java]

    // [START android_profiling_manager_oom_case_study_stats_reporter_java]
    public class StatsReporter {
        public interface StatsListener {
            void onReportStats(String stats);
        }

        private static StatsReporter sInstance;

        private final List<StatsListener> mListeners = new CopyOnWriteArrayList<>();
        private boolean mIsReporting = false;

        private StatsReporter() {
            startReporting();
        }

        public static StatsReporter getInstance() {
            if (sInstance == null) {
                sInstance = new StatsReporter();
            }
            return sInstance;
        }

        public void registerListener(StatsListener listener) {
            if (!mListeners.contains(listener)) {
                mListeners.add(listener);
            }
        }

        public void unregisterListener(StatsListener listener) {
            mListeners.remove(listener);
        }

        private void startReporting() {
            if (mIsReporting) return;
            mIsReporting = true;

            new Thread(() -> {
                while (true) {
                    try {
                        // Poll every 10 seconds
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }

                    for (StatsListener listener : mListeners) {
                        // Send periodic mock stats update from the background thread
                        listener.onReportStats("Update from StatsReporter background polling thread");
                    }
                }
            }, "StatsReporterPollingThread").start();
        }
    }
    // [END android_profiling_manager_oom_case_study_stats_reporter_java]

    // [START android_profiling_manager_oom_case_study_oom_sample_activity_java]
    public class OomSampleActivity extends Activity implements StatsReporter.StatsListener {
        private static final String TAG = "OomSampleActivity";

        // Allocate some memory.
        private final byte[] mHeavyData = new byte[30 * 1024 * 1024];

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_oom_sample);

            StatsReporter.getInstance().registerListener(this);
        }

        @Override
        public void onReportStats(String stats) {
            // Read from mHeavyData so R8 does not strip it out as dead code during release builds
            Log.d(TAG, "Received stats in heavy leaked activity: " + stats + " | Data size: " + mHeavyData.length);
        }
    }
    // [END android_profiling_manager_oom_case_study_oom_sample_activity_java]

    // [START android_profiling_manager_oom_case_study_oom_sample_activity_fixed_java]
    public class OomSampleActivityFixed extends Activity implements StatsReporter.StatsListener {
        private static final String TAG = "OomSampleFixed";

        // Allocate some memory.
        private final byte[] mHeavyData = new byte[30 * 1024 * 1024];

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_oom_sample);

            // Register the activity as a listener
            StatsReporter.getInstance().registerListener(this);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            // FIX: Properly unregister the listener to prevent the memory leak
            // when the Activity is destroyed (e.g., during screen rotation).
            StatsReporter.getInstance().unregisterListener(this);
        }

        @Override
        public void onReportStats(String stats) {
            // Read from mHeavyData so R8 does not strip it out as dead code during release builds
            Log.d(TAG, "Received stats in fixed heavy activity: " + stats + " | Data size: " + mHeavyData.length);
        }
    }
    // [END android_profiling_manager_oom_case_study_oom_sample_activity_fixed_java]
  }

  public class AppDrivenProfilingActivityJava extends Activity {

    // [START android_profiling_manager_app_driven_record_system_trace_java]
    void sampleRecordSystemTrace(Context context) {
      // Use the application context: requestProfiling resolves the ProfilingManager
      // system service from it, so there's no reason to hand it a short-lived Activity.
      Context appContext = context.getApplicationContext();

      // Run the orchestration off the main thread. Profiling a heavy operation
      // on the UI thread would freeze the UI (ANR) and distort the very metrics
      // you're trying to capture.
      profilingExecutor.execute(() -> {
        Executor callbackExecutor = Executors.newSingleThreadExecutor();
        Consumer<ProfilingResult> resultCallback = profilingResult -> {
          if (profilingResult.getErrorCode() == ProfilingResult.ERROR_NONE) {
            Log.d("ProfileTest", "Result file: " + profilingResult.getResultFilePath());
          } else {
            // errorMessage explains the failure (e.g., rate limiting); keep it.
            Log.e(
                "ProfileTest",
                "Profiling failed errorCode=" + profilingResult.getErrorCode()
                    + " errorMessage=" + profilingResult.getErrorMessage());
          }
        };

        CancellationSignal stopSignal = new CancellationSignal();
        SystemTraceRequestBuilder requestBuilder = new SystemTraceRequestBuilder();
        requestBuilder.setCancellationSignal(stopSignal);
        requestBuilder.setTag("FOO"); // Caller-supplied tag for identification.
        requestBuilder.setDurationMs(60000); // Hard cap: ends the session if cancel() never fires.
        requestBuilder.setBufferFillPolicy(BufferFillPolicy.RING_BUFFER);
        requestBuilder.setBufferSizeKb(32768);

        // 1. Start the session. This is asynchronous system IPC. The tracing
        //    engine takes a moment to start and allocate buffers.
        Profiling.requestProfiling(
            appContext, requestBuilder.build(), callbackExecutor, resultCallback);

        // 2. The API exposes no "profiling started" signal, so pad with a short,
        //    best-effort delay before running the code you care about. This is
        //    approximate. Increase it on slower or heavily loaded devices.
        try {
          Thread.sleep(STARTUP_PADDING_MS);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          stopSignal.cancel();
          return;
        }

        // 3. The session is already recording every thread in your app. This slice
        //    doesn't scope what's captured. It just labels this region of the
        //    timeline so heavyOperation() is easier to find. try/finally closes the
        //    section even if heavyOperation() throws.
        Trace.beginSection("MyApp:HeavyOperation");
        try {
          heavyOperation();
        } finally {
          Trace.endSection();
        }

        // 4. Stop recording. Until this fires or the setDurationMs() cap is
        //    reached (whichever comes first), the session keeps capturing app-wide
        //    activity.
        stopSignal.cancel();
      });
    }

    // Best-effort wait for the system trace engine to initialize before profiling.
    // There is no deterministic start callback; tune this for your target devices.
    private static final long STARTUP_PADDING_MS = 100L;

    // Single background thread that runs the profiling orchestration.
    private final ExecutorService profilingExecutor = Executors.newSingleThreadExecutor();

    void heavyOperation() {
      // Background computations to profile.
    }
    // [END android_profiling_manager_app_driven_record_system_trace_java]

    void recordSystemTraceWithCallstacks(
        Context context, Executor callbackExecutor, Consumer<ProfilingResult> resultCallback) {
      // [START android_profiling_manager_app_driven_stack_sampling_java]
      ProfilingManager profilingManager = context.getSystemService(ProfilingManager.class);
      if (profilingManager == null) return;

      // Pass a Bundle to customize the capture, or pass null for default settings.
      Bundle config = new Bundle();
      config.putInt("KEY_DURATION_MS", 15000); // 15 seconds
      config.putInt("KEY_SIZE_KB", 20480); // 20 MB buffer size

      // Collect sampled CPU call stacks alongside the system trace (Android 16+).
      config.putBoolean("KEY_COLLECT_STACK_SAMPLING", true);
      config.putInt("KEY_FREQUENCY_HZ", 100); // Sampling frequency in Hz
      // Optional: Set KEY_SAMPLE_BINDER_ONLY to true to sample only binder calls.

      profilingManager.requestProfiling(
          ProfilingManager.PROFILING_TYPE_SYSTEM_TRACE,
          config,
          "trace-with-callstacks",
          null,
          callbackExecutor,
          resultCallback
      );
      // [END android_profiling_manager_app_driven_stack_sampling_java]
    }

    void captureJavaHeapDump(
        Executor callbackExecutor, Consumer<ProfilingResult> resultCallback) {
      // [START android_profiling_manager_app_driven_java_heap_dump_java]
      ProfilingRequest heapDumpRequest = new JavaHeapDumpRequestBuilder()
          .setTag("memory-leak-check")
          .build();

      Profiling.requestProfiling(
          getApplicationContext(),
          heapDumpRequest,
          callbackExecutor,
          resultCallback
      );
      // [END android_profiling_manager_app_driven_java_heap_dump_java]
    }
  }
}
