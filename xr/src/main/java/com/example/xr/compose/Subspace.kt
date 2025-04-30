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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialPanel

private class SubspaceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // [START androidxr_compose_SubspaceSetContent]
        setContent {
            // This is a top-level subspace
            Subspace {
                SpatialPanel {
                    MyComposable()
                }
            }
        }
        // [END androidxr_compose_SubspaceSetContent]
    }
}

// [START androidxr_compose_SubspaceComponents]
@Composable
private fun MyComposable() {
    Row {
        PrimaryPane()
        SecondaryPane()
    }
}

@Composable
private fun PrimaryPane() {
    // This is a nested subspace, because PrimaryPane is in a SpatialPanel
    // and that SpatialPanel is in a top-level Subspace
    Subspace {
        ObjectInAVolume(true)
    }
}
// [END androidxr_compose_SubspaceComponents]

@Composable
private fun SecondaryPane() {}
