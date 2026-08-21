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

package com.example.crossdeviceinstallprompt

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.play.core.crossdeviceprompt.CrossDevicePromptException
import com.google.android.play.core.crossdeviceprompt.CrossDevicePromptManagerFactory
import com.google.android.play.core.crossdeviceprompt.model.CrossDevicePromptInstallationRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

const val TAG = "CrossDeviceInstallPrompt"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                scope.launch {
                    launchCrossDeviceInstallPrompt(activity!!)
                }
            }) {
                Text(text = "Launch prompt!")
            }
        }
    }
}

private suspend fun launchCrossDeviceInstallPrompt(activity: Activity) {
    // [START android_installprompt_launch]
    val crossDevicePromptManager = CrossDevicePromptManagerFactory.create(activity)
    val request = CrossDevicePromptInstallationRequest.create()

    try {
        val info = crossDevicePromptManager.requestInstallationPromptFlow(request).await()
        crossDevicePromptManager.launchPromptFlow(activity, info).await()
    } catch (e: CrossDevicePromptException) {
        Log.e(TAG, "Cross-device prompt failed with error: ${e.errorCode}", e)
    }
    // [END android_installprompt_launch]
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    MaterialTheme {
        App()
    }
}
