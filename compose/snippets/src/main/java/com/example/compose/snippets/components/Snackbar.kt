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

package com.example.compose.snippets.components

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.SnackbarResult.Dismissed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
private fun DACPlaygroundTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}

private class SnackbarActionActivity : ComponentActivity() {

    // [START android_compose_components_snackbar_host]
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            DACPlaygroundTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    content = { padding ->
                        Button(
                            modifier = Modifier.padding(padding),
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "1 item removed",
                                        actionLabel = "UNDO",
                                        duration = SnackbarDuration.Short
                                    ).run {
                                        when (this) {
                                            Dismissed -> Log.d("SNACKBAR", "Dismissed")
                                            ActionPerformed -> Log.d("SNACKBAR", "UNDO CLICKED")
                                        }
                                    }
                                }
                            }
                        ) { Text("Show snackbar") }
                    }
                )
            }
        }
    }
    // [END android_compose_components_snackbar_host]
}
