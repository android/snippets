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

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import com.example.healthconnect.ui.theme.SnippetsTheme
import com.google.android.gms.fitness.FitnessLocal
import com.google.android.gms.fitness.data.LocalDataType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.Duration

class HealthConnectActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SnippetsTheme {
                HealthConnectScreen(Modifier.fillMaxSize())
            }
        }
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun subscribeToSteps(context: Context, onResult: (String) -> Unit) {
        // [START android_healthconnect_subscribe_fitness_data]
        val localRecordingClient = FitnessLocal.getLocalRecordingClient(context)
        localRecordingClient.subscribe(LocalDataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener {
                Log.i("HealthConnectManager", "Successfully subscribed!")
                onResult("Successfully subscribed!")
            }
            .addOnFailureListener { e ->
                Log.w("HealthConnectManager", "There was a problem subscribing.", e)
                onResult("Subscription failed: ${e.message}")
            }
        // [END android_healthconnect_subscribe_fitness_data]
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthConnectScreen(modifier: Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // [START android_healthconnect_get_client]
    val availabilityStatus = HealthConnectClient.getSdkStatus(context)
    if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
        Box(modifier = modifier.padding(16.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "Health Connect is not available on this device. Please ensure it is installed and updated.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val healthConnectClient = remember {
        if (availabilityStatus == HealthConnectClient.SDK_AVAILABLE) {
            HealthConnectClient.getOrCreate(context)
        } else {
            null
        }
    }
    // [END android_healthconnect_get_client]

    // Initialize our snippet manager
    val manager = remember(healthConnectClient) {
        healthConnectClient?.let { HealthConnectManager(it) }
    }

    // [START android_healthconnect_check_permission_launcher]
    val permissions = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getWritePermission(HeartRateRecord::class)
        )

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions ->
        if (grantedPermissions.containsAll(permissions)) {
            coroutineScope.launch { snackbarHostState.showSnackbar("Permissions granted!") }
        } else {
            coroutineScope.launch { snackbarHostState.showSnackbar("Permissions denied.") }
        }
    }
    // [END android_healthconnect_check_permission_launcher]

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Health Connect Snippets") }) },
    ) { innerPadding ->
        LazyColumn(Modifier.padding(innerPadding).padding(horizontal = 16.dp)) {
            item {
                Text(
                    text = if (healthConnectClient != null)
                        "Health Connect is available"
                    else
                        "Health Connect is not available",
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            if (manager != null) {
                item {
                    Button(onClick = {
                        // Starts on Main thread by default
                        coroutineScope.launch {
                            // 1. Check permissions
                            val granted = healthConnectClient?.permissionController?.getGrantedPermissions()

                            if (granted?.containsAll(permissions) == true) {
                                val startTime = Instant.now().minusSeconds(3600)
                                val endTime = Instant.now()

                                // 2. Insert data on IO thread
                                withContext(Dispatchers.IO) {
                                    manager.insertSteps(startTime, endTime)
                                }

                                // 3. Safely back on Main thread automatically
                                snackbarHostState.showSnackbar("Steps inserted!")
                            } else {
                                // 4. Safely on Main thread to launch UI activity
                                requestPermissionsLauncher.launch(permissions)
                            }
                        }
                    }) {
                        Text("Run: Insert Steps")
                    }
                }

                item {
                    Button(
                        modifier = Modifier.padding(top = 8.dp),
                        onClick = {
                            coroutineScope.launch {
                                // Check permissions
                                val granted = healthConnectClient?.permissionController?.getGrantedPermissions()

                                if (granted?.containsAll(permissions) == true) {
                                    val startTime = Instant.now().minus(Duration.ofDays(1))
                                    val endTime = Instant.now()

                                    // Read data on the I/O thread pool
                                    val total = withContext(Dispatchers.IO) {
                                        manager.readStepsAggregate(startTime, endTime)
                                    }

                                    // Safely update the UI on the Main thread
                                    snackbarHostState.showSnackbar("Total Steps: $total")
                                } else {
                                    // Safely launch permission UI on the Main thread
                                    requestPermissionsLauncher.launch(permissions)
                                }
                            }
                        }
                    ) {
                        Text("Run: Read Steps Aggregate")
                    }
                }

                item {
                    Button(
                        modifier = Modifier.padding(top = 8.dp),
                        onClick = {
                            coroutineScope.launch {
                                // Check permissions on the I/O thread pool
                                val granted = withContext(Dispatchers.IO) {
                                    healthConnectClient?.permissionController?.getGrantedPermissions()
                                }

                                if (granted?.containsAll(permissions) == true) {
                                    val startTime = Instant.now().minus(Duration.ofDays(1))
                                    val endTime = Instant.now()

                                    // Read data on the I/O thread pool
                                    val total = withContext(Dispatchers.IO) {
                                        manager.readDistanceAggregate(startTime, endTime)
                                    }

                                    // Safely update the UI on the Main thread
                                    snackbarHostState.showSnackbar("Total Distance: $total")
                                } else {
                                    // Safely launch permission UI on the Main thread
                                    requestPermissionsLauncher.launch(permissions)
                                }
                            }
                        }
                    ) {
                        Text("Run: Read Distance Aggregate")
                    }
                }
            }
        }
    }
}