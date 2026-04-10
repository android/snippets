package com.example.healthconnect

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectFeatures
import androidx.health.connect.client.feature.ExperimentalFeatureAvailabilityApi
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

// [START android_background_read_worker]
class ScheduleWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val healthConnectClient = HealthConnectClient.getOrCreate(applicationContext)
        // Perform background read logic here
        return Result.success()
    }
}
// [END android_background_read_worker]

// [START android_background_read_workmanager]
@OptIn(ExperimentalFeatureAvailabilityApi::class)
fun enqueueBackgroundReadWorker(context: Context, healthConnectClient: HealthConnectClient) {
    if (healthConnectClient
            .features
            .getFeatureStatus(
                HealthConnectFeatures.FEATURE_READ_HEALTH_DATA_IN_BACKGROUND
            ) == HealthConnectFeatures.FEATURE_STATUS_AVAILABLE) {

        val periodicWorkRequest = PeriodicWorkRequestBuilder<ScheduleWorker>(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "read_health_connect",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
}
// [END android_background_read_workmanager]