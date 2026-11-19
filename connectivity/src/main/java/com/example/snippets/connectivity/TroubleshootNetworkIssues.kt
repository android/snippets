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

package com.example.snippets.connectivity

import android.net.TrafficStats
import androidx.annotation.WorkerThread
import java.net.HttpURLConnection
import java.net.Socket
import java.net.URL

// Wraps the page's top-level declarations so the constants and the task class don't leak
// into the package.
private object TroubleshootNetworkIssuesSnippets {
    // [START android_connectivity_troubleshoot_network_traffic_constants]
    const val USER_INITIATED = 0x1000
    const val APP_INITIATED = 0x2000
    const val SERVER_INITIATED = 0x3000
    // [END android_connectivity_troubleshoot_network_traffic_constants]

    // The socket comes from the caller, so the snippet only shows the tagging calls.
    fun tagNetworkRequests(outputSocket: Socket) {
        // [START android_connectivity_troubleshoot_network_tag_requests]
        TrafficStats.setThreadStatsTag(USER_INITIATED)
        try {
            TrafficStats.tagSocket(outputSocket)
            try {
                // Transfer data using socket.
            } finally {
                TrafficStats.untagSocket(outputSocket)
            }
        } finally {
            TrafficStats.clearThreadStatsTag()
        }
        // [END android_connectivity_troubleshoot_network_tag_requests]
    }

    // [START android_connectivity_troubleshoot_network_identify_transfer_spike]
    class IdentifyTransferSpikeTask {
        @WorkerThread
        fun request(url: String) {
            TrafficStats.setThreadStatsTag(APP_INITIATED)
            try {
                // Make network request using HttpURLConnection.connect().
                val connection = URL(url).openConnection() as HttpURLConnection
                try {
                    connection.connect()
                    // ...
                } finally {
                    connection.disconnect()
                }
            } finally {
                TrafficStats.clearThreadStatsTag()
            }
        }
    }
    // [END android_connectivity_troubleshoot_network_identify_transfer_spike]
}
