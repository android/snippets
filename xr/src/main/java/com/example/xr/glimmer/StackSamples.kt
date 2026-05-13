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

package com.example.xr.glimmer

import androidx.compose.runtime.Composable
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.stack.VerticalStack

// [START xr_glimmer_stack]
@Composable
fun VerticalStackSample() {
    VerticalStack {
        item {
            Card { Text("Top Card") }
        }
        item {
            Card { Text("Second Card") }
        }
        item {
            Card { Text("Third Card") }
        }
    }
}
// [END xr_glimmer_stack]
