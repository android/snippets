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

package com.example.compose.snippets.connectivity

import android.net.TrafficStats
import androidx.annotation.WorkerThread
import java.net.Socket

// [START android_connectivity_troubleshoot_network_traffic_constants]
const val USER_INITIATED = 0x1000
const val APP_INITIATED = 0x2000
const val SERVER_INITIATED = 0x3000
// [END android_connectivity_troubleshoot_network_traffic_constants]

private fun tagNetworkRequests(outputSocket: Socket) {
    // [START android_connectivity_troubleshoot_network_tag_requests]
    TrafficStats.setThreadStatsTag(USER_INITIATED)
    TrafficStats.tagSocket(outputSocket)
    // Transfer data using socket
    TrafficStats.untagSocket(outputSocket)
    // [END android_connectivity_troubleshoot_network_tag_requests]
}

// [START android_connectivity_troubleshoot_network_identify_transfer_spike]
class IdentifyTransferSpikeTask {
    @WorkerThread
    fun request(url: String) {
        TrafficStats.setThreadStatsTag(APP_INITIATED)
        // Make network request using HttpURLConnection.connect()
        // ...
        TrafficStats.clearThreadStatsTag()
    }
}
// [END android_connectivity_troubleshoot_network_identify_transfer_spike]
