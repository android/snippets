/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.example.healthconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.healthconnect.ui.theme.SnippetsTheme
import kotlinx.coroutines.launch
import java.time.Clock
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
                        coroutineScope.launch {
                            val startTime = Instant.now().minusSeconds(3600)
                            val endTime = Instant.now()
                            manager.insertSteps(startTime, endTime)
                            snackbarHostState.showSnackbar("Steps inserted!")
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
                                val startTime = Instant.now().minus(Duration.ofDays(1))
                                val endTime = Instant.now()

                                val total = manager.readStepsAggregate(startTime, endTime)
                                snackbarHostState.showSnackbar("Total Steps: $total")
                            }
                        }) {
                        Text("Run: Read Steps Aggregate")
                    }
                }
            }
        }
    }
}