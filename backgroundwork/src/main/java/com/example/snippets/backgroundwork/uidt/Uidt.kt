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

package com.example.snippets.backgroundwork.uidt

import android.app.Notification
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_NOT_METERED
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val NOTIFICATION_CHANNEL_ID = "uidt_channel"
private const val NOTIFICATION_ID = 1

private object JobServiceSkeleton {
    // [START android_backgroundwork_uidt_job_service_skeleton]
    class CustomTransferService : JobService() {
        // [START_EXCLUDE]
        override fun onStartJob(params: JobParameters): Boolean = false

        override fun onStopJob(params: JobParameters): Boolean = false
        // [END_EXCLUDE]
    }
    // [END android_backgroundwork_uidt_job_service_skeleton]
}

private class ScheduleJobSnippet(private val mContext: Context) {
    private val jobId = 1

    fun schedule(): JobInfo {
        // [START android_backgroundwork_uidt_schedule_job]
        val networkRequestBuilder = NetworkRequest.Builder()
            // Add or remove capabilities based on your requirements.
            // For example, this code specifies that the job won't run
            // unless there's a connection to the internet (not just a local
            // network), and the connection doesn't charge per-byte.
            .addCapability(NET_CAPABILITY_INTERNET)
            .addCapability(NET_CAPABILITY_NOT_METERED)
            .build()

        val jobInfo = JobInfo.Builder(
            jobId,
            ComponentName(mContext, CustomTransferService::class.java)
        )
            // ...
            .setUserInitiated(true)
            .setRequiredNetwork(networkRequestBuilder)
            // Provide your estimate of the network traffic here.
            .setEstimatedNetworkBytes(1024 * 1024 * 1024, 1024 * 1024 * 1024)
            // ...
            .build()
        // [END android_backgroundwork_uidt_schedule_job]
        return jobInfo
    }
}

// [START android_backgroundwork_uidt_job_service]
class CustomTransferService : JobService() {
    private val scope = CoroutineScope(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onStartJob(params: JobParameters): Boolean {
        val notification = Notification.Builder(
            applicationContext,
            NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle("My user-initiated data transfer job")
            .setSmallIcon(android.R.mipmap.sym_def_app_icon)
            .setContentText("Job is running")
            .build()

        setNotification(
            params, NOTIFICATION_ID, notification,
            JobService.JOB_END_NOTIFICATION_POLICY_DETACH
        )
        // Execute the work associated with this job asynchronously.
        scope.launch {
            doDownload(params)
        }
        return true
    }

    private suspend fun doDownload(params: JobParameters) {
        // Run the relevant async download task, then call
        // jobFinished once the task is completed.
        jobFinished(params, false)
    }

    // Called when the system stops the job.
    override fun onStopJob(params: JobParameters?): Boolean {
        // Asynchronously record job-related data, such as the
        // stop reason.
        return true // or return false if job should end entirely.
    }
}
// [END android_backgroundwork_uidt_job_service]

class DownloadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result = Result.success()
}

private class BackCompatSnippet(private val context: Context) {
    private val jobInfo: JobInfo by lazy {
        JobInfo.Builder(1, ComponentName(context, CustomTransferService::class.java))
            .setUserInitiated(true)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .build()
    }

    // [START android_backgroundwork_uidt_back_compat]
    fun beginTask() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            scheduleDownloadFGSWorker(context)
        } else {
            scheduleDownloadUIDTJob(context)
        }
    }

    private fun scheduleDownloadUIDTJob(context: Context) {
        // build jobInfo.
        val jobScheduler: JobScheduler =
            context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(jobInfo)
    }

    private fun scheduleDownloadFGSWorker(context: Context) {
        val myWorkRequest = OneTimeWorkRequest.from(DownloadWorker::class.java)
        WorkManager.getInstance(context).enqueue(myWorkRequest)
    }
    // [END android_backgroundwork_uidt_back_compat]
}
