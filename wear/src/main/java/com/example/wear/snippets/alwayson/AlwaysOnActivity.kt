/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.wear.snippets.alwayson

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.SwitchButton
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.dynamicColorScheme
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.compose.ambient.AmbientAware
import com.google.android.horologist.compose.ambient.AmbientState
import kotlinx.coroutines.delay

class AlwaysOnActivity : ComponentActivity() {
    companion object {
        private const val TAG = "AlwaysOnActivity"
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "POST_NOTIFICATIONS permission granted")
            } else {
                Log.w(TAG, "POST_NOTIFICATIONS permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Activity created")

        setTheme(android.R.style.Theme_DeviceDefault)

        // Check and request notification permission
        checkAndRequestNotificationPermission()

        setContent { WearApp() }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "POST_NOTIFICATIONS permission already granted")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Log.d(TAG, "Should show permission rationale")
                    // You could show a dialog here explaining why the permission is needed
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    Log.d(TAG, "Requesting POST_NOTIFICATIONS permission")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}

@Composable
// [START android_wear_ongoing_activity_elapsedtime]
fun ElapsedTime(ambientState: AmbientState) {
    // [START_EXCLUDE]
    val startTimeMs = rememberSaveable { SystemClock.elapsedRealtime() }

    val elapsedMs by
    produceState(initialValue = 0L, key1 = startTimeMs) {
        while (true) {
            value = SystemClock.elapsedRealtime() - startTimeMs
            // In ambient mode, update every minute instead of every second
            val updateInterval = if (ambientState.isAmbient) 60_000L else 1_000L
            delay(updateInterval - (value % updateInterval))
        }
    }

    val totalSeconds = elapsedMs / 1_000L
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    // [END_EXCLUDE]
    val timeText =
        if (ambientState.isAmbient) {
            // Show "mm:--" format in ambient mode
            "%02d:--".format(minutes)
        } else {
            // Show full "mm:ss" format in interactive mode
            "%02d:%02d".format(minutes, seconds)
        }

    Text(text = timeText, style = MaterialTheme.typography.numeralMedium)
}
// [END android_wear_ongoing_activity_elapsedtime]

@Preview(
    device = WearDevices.LARGE_ROUND,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Large Round",
    showSystemUi = true,
)
@Composable
fun WearApp() {
    val context = LocalContext.current
    var isOngoingActivity by rememberSaveable { mutableStateOf(AlwaysOnService.isRunning) }
    MaterialTheme(
        colorScheme = dynamicColorScheme(LocalContext.current) ?: MaterialTheme.colorScheme
    ) {
        // [START android_wear_ongoing_activity_ambientaware]
        AmbientAware { ambientState ->
            // [START_EXCLUDE]
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Elapsed Time", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    // [END_EXCLUDE]
                    ElapsedTime(ambientState = ambientState)
                    // [START_EXCLUDE]
                    Spacer(modifier = Modifier.height(8.dp))
                    SwitchButton(
                        checked = isOngoingActivity,
                        onCheckedChange = { newState ->
                            Log.d("AlwaysOnActivity", "Switch button changed: $newState")
                            isOngoingActivity = newState

                            if (newState) {
                                Log.d("AlwaysOnActivity", "Starting AlwaysOnService")
                                AlwaysOnService.startService(context)
                            } else {
                                Log.d("AlwaysOnActivity", "Stopping AlwaysOnService")
                                AlwaysOnService.stopService(context)
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "Ongoing Activity",
                            style = MaterialTheme.typography.bodyExtraSmall,
                        )
                    }
                }
            }
            // [END_EXCLUDE]
        }
        // [END android_wear_ongoing_activity_ambientaware]
    }

}
