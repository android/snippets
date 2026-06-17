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

package com.example.healthconnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.changes.DeletionChange
import androidx.health.connect.client.changes.UpsertionChange
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ChangesTokenRequest

class ChangesManager(private val healthConnectClient: HealthConnectClient) {

    suspend fun getChangesToken(): String {
        // [START android_healthconnect_get_changes_token]
        val changesToken = healthConnectClient.getChangesToken(
            ChangesTokenRequest(recordTypes = setOf(WeightRecord::class))
        )
        // [END android_healthconnect_get_changes_token]
        return changesToken
    }

    // [START android_healthconnect_process_changes]
    suspend fun processChanges(token: String): String {
        var nextChangesToken = token
        do {
            val response = healthConnectClient.getChanges(nextChangesToken)
            response.changes.forEach { change ->
                when (change) {
                    is UpsertionChange -> { /* Process Upsert */ }
                    is DeletionChange -> { /* Process Deletion */ }
                }
            }
            nextChangesToken = response.nextChangesToken
        } while (response.hasMore)
        return nextChangesToken
    }
    // [END android_healthconnect_process_changes]
}
