// Copyright 2026 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.snippets.privacyandsecurity

import android.content.Context
import android.content.Intent
import androidx.security.state.UpdateInfo
import androidx.security.state.provider.UpdateCheckTelemetry
import androidx.security.state.provider.UpdateInfoManager
import androidx.security.state.provider.UpdateInfoService
import java.util.concurrent.TimeUnit

private val context: Context = TODO()
private val jsonString: String = ""
private val securityPatchLevel: String = ""
private val publishedDate: Long = 0

// [START android_privacyandsecurity_securitystate_init]
import androidx.security.state.SecurityPatchState
val securityPatchState = SecurityPatchState(context)
// [END android_privacyandsecurity_securitystate_init]

private fun fetchSpls() {
    // [START android_privacyandsecurity_securitystate_fetch]
    val deviceSpl = securityPatchState.getDeviceSecurityPatchLevel(SecurityPatchState.COMPONENT_SYSTEM)
    val publishedSpl = securityPatchState.getPublishedSecurityPatchLevel(SecurityPatchState.COMPONENT_SYSTEM)
    val availableSpl = securityPatchState.fetchAvailableSecurityPatchLevel(SecurityPatchState.COMPONENT_SYSTEM)

    // Check if the device is fully up to date
    val isDeviceFullyUpdated = securityPatchState.isDeviceFullyUpdated()
    // [END android_privacyandsecurity_securitystate_fetch]
}

private fun loadReports(deviceSpl: String) {
    // [START android_privacyandsecurity_securitystate_load_report]
    // Load vulnerability report
    val reportUrl = SecurityPatchState.getVulnerabilityReportUrl()
    // ... download JSON string from reportUrl ...
    securityPatchState.loadVulnerabilityReport(jsonString)

    // Query updates and check CVEs
    val updateCheckResults = securityPatchState.queryAllAvailableUpdates()
    val cves = listOf("CVE-2019-9501", "CVE-2020-3699", "CVE-2024-0016")
    val isPatched = securityPatchState.areCvesPatched(cves)

    // Get a list of all patched CVEs for a specific component and SPL
    val patchedSystemCVEs = securityPatchState.getPatchedCves(SecurityPatchState.COMPONENT_SYSTEM, deviceSpl)
    // [END android_privacyandsecurity_securitystate_load_report]
}

private object MyBackendClient {
    fun fetchSecurityUpdates(): MyBackendResponse = MyBackendResponse()
}

private class MyBackendResponse {
    val updates: List<UpdateItem> = emptyList()
}

private class UpdateItem(
    val componentName: String = "",
    val securityPatchLevel: String = ""
)

// [START android_privacyandsecurity_securitystate_service]
class MyUpdateInfoService : UpdateInfoService() {
    override suspend fun fetchUpdates(): List<UpdateInfo> {
        // 1. Perform the actual network request (Blocking/Suspend)
        val response = MyBackendClient.fetchSecurityUpdates()
        
        // 2. Map response to UpdateInfo objects
        return response.updates.map {
            UpdateInfo.Builder()
                .setComponent(it.componentName) // e.g., COMPONENT_SYSTEM
                .setSecurityPatchLevel(it.securityPatchLevel) // e.g., "2026-01-01"
                .setPublishedDateMillis(System.currentTimeMillis())
                .build()
        }
    }

    override fun shouldFetchUpdates(): Boolean {
        // Example: Override default 1-hour cache to enforce a 4-hour cache
        val lastCheckMillis = updateInfoManager.getLastCheckTimeMillis()
        val age = System.currentTimeMillis() - lastCheckMillis
        return age > TimeUnit.HOURS.toMillis(4)
    }
}
// [END android_privacyandsecurity_securitystate_service]

private fun registerUpdate() {
    // [START android_privacyandsecurity_securitystate_register]
    val updateInfoManager = UpdateInfoManager(context)
    val updateInfo = UpdateInfo(SecurityPatchState.COMPONENT_SYSTEM, securityPatchLevel, publishedDate)

    // Register the update info to make it visible to the library
    updateInfoManager.registerUpdate(updateInfo)

    // Update the timestamp of the last successful update check (e.g. after a backend sync)
    updateInfoManager.setLastCheckTimeMillis(System.currentTimeMillis())

    // Check the timestamp of the last successful check
    val lastCheck = updateInfoManager.getLastCheckTimeMillis()

    // Unregister when no longer applicable
    updateInfoManager.unregisterUpdate(updateInfo)
    // [END android_privacyandsecurity_securitystate_register]
}

private object MyAnalytics {
    fun logEvent(event: String): MyAnalyticsEvent = MyAnalyticsEvent()
}

private class MyAnalyticsEvent {
    fun addParam(key: String, value: Any): MyAnalyticsEvent = this
    fun send() {}
}

private object MyServiceBroker {
    fun getCurrentClientUid(): Int? = null
}

private object MyCrashReporter {
    fun recordException(e: Exception) {}
}

class MyUpdateInfoServiceTelemetry : UpdateInfoService() {
    // [START android_privacyandsecurity_securitystate_telemetry]
    override fun onRequestCompleted(telemetry: UpdateCheckTelemetry) {
        MyAnalytics.logEvent("SECURITY_UPDATE_CHECK")
            .addParam("outcome", telemetry.outcome.name)
            .addParam("latency_total", telemetry.totalLatencyMillis)
            .send()
    }

    override fun onClientConnected(intent: Intent) {
        // Track session counts or client adoption
        val clientPackage = intent.data?.schemeSpecificPart ?: "unknown"
    }

    override fun getCallerUid(): Int {
        // Advanced: Override to return the logical client UID if using a Proxy architecture
        return MyServiceBroker.getCurrentClientUid() ?: super.getCallerUid()
    }

    override fun onFetchFailed(e: Exception) {
        MyCrashReporter.recordException(e)
    }
    // [END android_privacyandsecurity_securitystate_telemetry]
}
