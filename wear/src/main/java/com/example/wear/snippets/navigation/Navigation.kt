/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.wear.snippets.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults.behavior
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.wear.R
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader

@Composable
fun navigation() {
    // [START android_wear_navigation]
    AppScaffold {
        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "message_list"
        ) {
            composable("message_list") {
                MessageList(onMessageClick = { id ->
                    navController.navigate("message_detail/$id")
                })
            }
            composable("message_detail/{id}") {
                MessageDetail(id = it.arguments?.getString("id")!!)
            }
        }
    }
    // [START_EXCLUDE]
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun MessageDetail(id: String) {
    // [END_EXCLUDE]
    // .. Screen level content goes here
    val scrollState = rememberScrollState()

    ScreenScaffold(scrollState = scrollState) {
        // Screen content goes here
        // [END android_wear_navigation]
        val padding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Text,
            last = ItemType.Text
        )()
        val focusRequester = rememberActiveFocusRequester()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .rotaryScrollable(
                    behavior = behavior(scrollableState = scrollState),
                    focusRequester = focusRequester,
                ),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = id,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun MessageList(onMessageClick: (String) -> Unit) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Text,
            last = ItemType.Chip
        )
    )

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState,
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(text = stringResource(R.string.message_list))
                }
            }
            item {
                Chip(label = "Message 1", onClick = { onMessageClick("message1") })
            }
            item {
                Chip(label = "Message 2", onClick = { onMessageClick("message2") })
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MessageDetailPreview() {
    MessageDetail("test")
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MessageListPreview() {
    MessageList(onMessageClick = {})
}
