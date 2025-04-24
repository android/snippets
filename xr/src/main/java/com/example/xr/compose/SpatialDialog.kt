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

package com.example.xr.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.xr.compose.spatial.SpatialDialog
import androidx.xr.compose.spatial.SpatialDialogProperties
import kotlinx.coroutines.delay

// [START androidxr_compose_DelayedDialog]
@Composable
fun DelayedDialog() {
    var showDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(3000)
        showDialog = true
    }
    if (showDialog) {
        SpatialDialog(
            onDismissRequest = { showDialog = false },
            SpatialDialogProperties(
                dismissOnBackPress = true
            )
        ) {
            Box(
                Modifier
                    .height(150.dp)
                    .width(150.dp)
            ) {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        }
    }
}
// [END androidxr_compose_DelayedDialog]

@Composable
private fun MyDialogContent() {}
@Composable
private fun SpatialDialogComparison() {
    val onDismissRequest: () -> Unit = {}
    // [START androidxr_compose_spatialdialog_comparison]
    // Previous approach
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        MyDialogContent()
    }

    // New XR differentiated approach
    SpatialDialog(
        onDismissRequest = onDismissRequest
    ) {
        MyDialogContent()
    }
    // [END androidxr_compose_spatialdialog_comparison]
}
