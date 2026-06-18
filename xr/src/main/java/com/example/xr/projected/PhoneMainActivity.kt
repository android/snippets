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

package com.example.xr.projected

import android.content.Context
import android.content.Intent
import android.media.AudioDeviceInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.xr.projected.BatteryState
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.ProjectedDeviceController
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import kotlinx.coroutines.launch

class PhoneMainActivity : ComponentActivity() {

    // [START androidxr_projected_monitor_battery]
    @OptIn(ExperimentalProjectedApi::class)
    private var deviceController: ProjectedDeviceController? = null

    private val batteryListener = { batteryState: BatteryState ->
        val batteryLevel = batteryState.batteryLevel
        val isCharging = batteryState.isCharging

        if (batteryLevel <= 15 && !isCharging) {
            switchToLowPowerMode()
        }

        updateBatteryUi(batteryLevel, isCharging)
    }
    // [START_EXCLUDE]
    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                ConnectionScreen()
            }
        }
    }
    // [END_EXCLUDE]

    @OptIn(ExperimentalProjectedApi::class)
    private fun monitorBatteryStatus() {
        lifecycleScope.launch {
            try {
                deviceController = ProjectedDeviceController.create(this@PhoneMainActivity)

                deviceController?.addBatteryStateChangedListener(coroutineContext, batteryListener)

            } catch (e: IllegalStateException) {
                Log.e("BatteryMonitor", "Device controller state error: ${e.message}")
            } catch (e: Exception) {
                Log.e("BatteryMonitor", "Unexpected error: ${e.message}")
            }
        }
    }
    // [END androidxr_projected_monitor_battery]

    @OptIn(ExperimentalProjectedApi::class)
    override fun onResume() {
        super.onResume()
        monitorBatteryStatus()
    }

    // [START androidxr_projected_monitor_battery_remove_listener]

    @OptIn(ExperimentalProjectedApi::class)
    override fun onPause() {
        super.onPause()
        // Explicitly unregister the listener when it is no longer needed
        deviceController?.removeBatteryStateChangedListener(batteryListener)
    }
    // [END androidxr_projected_monitor_battery_remove_listener]

    // [START androidxr_projected_close_device_controller]

    @OptIn(ExperimentalProjectedApi::class)
    override fun onDestroy() {
        super.onDestroy()
        // Unregisters the active listeners
        deviceController?.close()
        deviceController = null
    }
    // [END androidxr_projected_close_device_controller]

    // [START androidxr_projected_get_projected_audio_device]
    @OptIn(ExperimentalProjectedApi::class)
    suspend fun getProjectedAudioDevices(context: Context): List<AudioDeviceInfo> {
        val deviceController = ProjectedDeviceController.create(context)
        return try {
            // Returns a list of AudioDeviceInfo objects associated with the projected device.
            deviceController.audioDevices
        } catch (e: Exception) {
            Log.e("ProjectedAudioDevices", "Failed to get projected audio devices", e)
            emptyList()
        }
    }
    // [END androidxr_projected_get_projected_audio_device]

    // [START androidxr_projected_is_projected_audio_device]
    suspend fun isProjectedAudioDevice(
        context: Context,
        systemAudioDeviceInfo: AudioDeviceInfo
    ): Boolean {
        // Fetch the list of the projected audio devices
        val projectedAudioDevicesInfo = getProjectedAudioDevices(context)

        // Return true if the current system device ID matches any projected device ID
        return projectedAudioDevicesInfo.any { projectedAudioDevice ->
            projectedAudioDevice.id == systemAudioDeviceInfo.id
        }
    }
    // [END androidxr_projected_is_projected_audio_device]
    private fun updateBatteryUi(batteryLevel: Int, charging: Boolean) {}
    private fun switchToLowPowerMode() {}
}

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@OptIn(ExperimentalProjectedApi::class)
@Composable
fun ConnectionScreen() {
    val context = LocalContext.current
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hello AI Glasses",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            val scope = rememberCoroutineScope()
            val areGlassesConnected by ProjectedContext.isProjectedDeviceConnected(
                context,
                scope.coroutineContext
            ).collectAsStateWithLifecycle(initialValue = false)
            Button(
                onClick = {
                    // [START androidxr_projected_start_glasses_activity]

                    val options = ProjectedContext.createProjectedActivityOptions(context)
                    val intent = Intent(context, GlassesMainActivity::class.java)
                    context.startActivity(intent, options.toBundle())

                    // [END androidxr_projected_start_glasses_activity]
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (areGlassesConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                ),
                enabled = areGlassesConnected
            ) {
                Text(
                    text = "Launch",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Status: " + if (areGlassesConnected) "Connected" else "Disconnected",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
