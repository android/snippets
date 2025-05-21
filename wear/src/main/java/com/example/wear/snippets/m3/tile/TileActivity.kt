/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.wear.snippets.m3.tile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.navDeepLink
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.TileService
import com.example.wear.R
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

class TileActivity : ComponentActivity() {
// [START android_wear_m3_interactions_launchaction_activity]
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val clickableId = intent.getStringExtra(TileService.EXTRA_CLICKABLE_ID)
    // clickableId will be "foo" when launched from the tile
    // InteractionLaunchAction

    setContent { navigation() }
  }
}
// [END android_wear_m3_interactions_launchaction_activity]

@Composable
fun navigation() {
  AppScaffold {
    val navController = rememberSwipeDismissableNavController()
    SwipeDismissableNavHost(
      navController = navController,
      startDestination = "message_list",
    ) {
      composable(
        route = "message_list",
        deepLinks =
          listOf(
            navDeepLink {
              uriPattern = "googleandroidsnippets://app/message_list"
            }
          ),
      ) {
        MessageList(
          onMessageClick = { id ->
            navController.navigate("message_detail/$id")
          }
        )
      }
      composable(
        route = "message_detail/{id}",
        deepLinks =
          listOf(
            navDeepLink {
              uriPattern = "googleandroidsnippets://app/message_detail/{id}"
            }
          ),
      ) {
        val id = it.arguments?.getString("id") ?: "0"
        MessageDetails(details = "message $id")
      }
    }
  }
}

// Implementation of one of the screens in the navigation
@Composable
fun MessageDetails(details: String) {
  val scrollState = rememberTransformingLazyColumnState()

  val padding = rememberResponsiveColumnPadding(first = ColumnItemType.BodyText)

  ScreenScaffold(scrollState = scrollState, contentPadding = padding) {
    scaffoldPaddingValues ->
    TransformingLazyColumn(
      state = scrollState,
      contentPadding = scaffoldPaddingValues,
    ) {
      item {
          ListHeader() { Text(text = stringResource(R.string.message_detail)) }
      }
      item {
        Text(
          text = details,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxSize(),
        )
      }
    }
  }
}

@Composable
fun MessageList(onMessageClick: (String) -> Unit) {
  val scrollState = rememberTransformingLazyColumnState()

  val padding =
    rememberResponsiveColumnPadding(
      first = ColumnItemType.ListHeader,
      last = ColumnItemType.Button,
    )

  ScreenScaffold(scrollState = scrollState, contentPadding = padding) {
    contentPadding ->
    TransformingLazyColumn(
      state = scrollState,
      contentPadding = contentPadding,
    ) {
      item {
        ListHeader() { Text(text = stringResource(R.string.message_list)) }
      }
      item {
        Button(
          onClick = { onMessageClick("message1") },
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(text = "Message 1")
        }
      }
      item {
        Button(
          onClick = { onMessageClick("message2") },
          modifier = Modifier.fillMaxWidth(),
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
  MessageDetails("message 7")
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun MessageListPreview() {
  MessageList(onMessageClick = {})
}
