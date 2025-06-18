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

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.dynamicColorScheme
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.compose.ambient.AmbientAware
import com.google.android.horologist.compose.ambient.AmbientState
import kotlinx.coroutines.delay

class AlwaysOnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent { WearApp() }
    }
}

@Composable
fun ElapsedTime(ambientState: AmbientState) {
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

    val timeText = if (ambientState.isAmbient) {
        // Show "mm:--" format in ambient mode
        "%02d:--".format(minutes)
    } else {
        // Show full "mm:ss" format in interactive mode
        "%02d:%02d".format(minutes, seconds)
    }

    Text(
        text = timeText,
        style = MaterialTheme.typography.numeralMedium,
    )
}

@Preview(
    device = WearDevices.LARGE_ROUND,
    backgroundColor = 0xff000000,
    showBackground = true,
    group = "Devices - Large Round",
    showSystemUi = true,
)
@Composable
fun WearApp() {
    MaterialTheme(
        colorScheme = dynamicColorScheme(LocalContext.current) ?: MaterialTheme.colorScheme
    ) {
        AmbientAware { ambientState ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Elapsed Time", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    ElapsedTime(ambientState = ambientState)
                }
            }
        }
    }
}
