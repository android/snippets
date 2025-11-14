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

package com.example.wear.snippets.m3.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState

import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme.colorScheme
import androidx.wear.compose.material3.MaterialTheme.typography
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.TimeTextDefaults
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import androidx.wear.compose.material3.timeTextCurvedText
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.example.wear.R
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

@Composable
fun navigation() {
    // [START android_wear_navigation]
    val styleArcMedium = typography.arcMedium
    val whiteTextStyle = TimeTextDefaults.timeTextStyle(color = Color.White)

    AppScaffold(timeText = {   TimeText() { time ->

            // Show only time when not paused
            timeTextCurvedText(time+"3",style = styleArcMedium.merge(whiteTextStyle))

    }}) {
        val navController = rememberSwipeDismissableNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "message_list",
            modifier = Modifier.fillMaxSize(),
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
}

// Implementation of one of the screens in the navigation
@Composable
fun MessageDetail(id: String) {
    // .. Screen level content goes here
    val scrollState = rememberTransformingLazyColumnState()

    val padding = rememberResponsiveColumnPadding(
        first = ColumnItemType.BodyText
    )

    ScreenScaffold(
        scrollState = scrollState,
        contentPadding = padding,
        edgeButton = {
            EdgeButton(
                onClick = { } ,
            ) {
            }
        },
    ) { scaffoldPaddingValues ->
        // Screen content goes here
        // [START_EXCLUDE]
        TransformingLazyColumn(
            state = scrollState,
            contentPadding = scaffoldPaddingValues
        ) {
            item {
                Text(
                    text = id,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        // [END_EXCLUDE]
        // [END android_wear_navigation]
    }
}

@Composable
fun MessageList(onMessageClick: (String) -> Unit) {
    val listState = rememberTransformingLazyColumnState()

    val padding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button
    )


    ScreenScaffold(
        scrollState = listState,
        edgeButton = {
            EdgeButton(
                onClick = { } ,
            ) {
            }
        }) { contentPadding ->
        TransformingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = contentPadding
        ) {
            item { Spacer(modifier = Modifier.size(8.dp)) }
            item {
                Box(modifier = Modifier.size(58.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_walk),
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.fillMaxSize(),
                    )

                }
            }
            item {
                ListHeader() {
                    Text(text = stringResource(R.string.message_list))
                }
            }
            item {
                Button(
                    onClick = { onMessageClick("message1") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Message 1")
                }
            }
            item {
                Button(
                    onClick = { onMessageClick("message2") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Message 2")
                }
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
