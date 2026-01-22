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

package com.example.wear.snippets.m3.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.CompactButton
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.IconButton
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TextButton
import com.example.wear.R
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

@Composable
fun EdgeButton() {
    // [START android_wear_edgebutton]
    val state = rememberTransformingLazyColumnState()
    ScreenScaffold(
        scrollState = state,
        contentPadding =
            rememberResponsiveColumnPadding(
                first = ColumnItemType.ListHeader
            ),
        edgeButton = {
            EdgeButton(
                onClick = { }
            ) {
                Text(stringResource(R.string.show))
            }
        }
    ){ contentPadding ->
        TransformingLazyColumn(state = state, contentPadding = contentPadding,){
            // additional code here
        }
    }
    // [END android_wear_edgebutton]
}

@Composable
fun buttonsList() {
    // [START android_wear_buttons_list]
    //M3 Buttons
    Button(onClick = { }){}
    CompactButton(onClick = { }){}
    IconButton(onClick = { }){}
    TextButton(onClick = { }){}
    // [END android_wear_buttons_list]
}
