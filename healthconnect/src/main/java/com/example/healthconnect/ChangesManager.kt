package com.example.healthconnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.changes.DeletionChange
import androidx.health.connect.client.changes.UpsertionChange
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.records.WeightRecord
import android.content.Context

class ChangesManager(private val healthConnectClient: HealthConnectClient, private val context: Context) {

    suspend fun getChangesToken(): String {
        // [START health_connect_get_changes_token]
        val changesToken = healthConnectClient.getChangesToken(
            ChangesTokenRequest(recordTypes = setOf(WeightRecord::class))
        )
        // [END health_connect_get_changes_token]
        return changesToken
    }

    // [START health_connect_process_changes]
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
    // [END health_connect_process_changes]
}