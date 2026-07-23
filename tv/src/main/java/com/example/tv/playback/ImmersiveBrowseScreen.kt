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

package com.example.tv.playback

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.CompactCard
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.example.tv.model.Video

// [START android_compose_tv_immersive_browse]
@Composable
fun ImmersiveBrowseScreen(
    categories: Map<String, List<Video>>,
    onVideoClick: (Video) -> Unit
) {
    var focusedVideo by remember { mutableStateOf<Video?>(null) }
    var focusedCategoryIndex by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()
    val topBarFocusRequester = remember { FocusRequester() }
    val firstCardFocusRequester = remember { FocusRequester() }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = focusedVideo?.bgImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        PositionFocusedItemInLazyLayout(parentFraction = 0.35f, childFraction = 0.5f) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = 36.dp, bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { /* Search */ },
                            modifier = Modifier.focusRequester(topBarFocusRequester)
                        ) { Text("Search") }
                    }
                }

                categories.entries.forEachIndexed { catIndex, (categoryName, videos) ->
                    item {
                        Column {
                            if (catIndex == 0 && focusedCategoryIndex == 0) {
                                Column(
                                    modifier = Modifier
                                        .heightIn(min = 200.dp)
                                        .padding(horizontal = 48.dp)
                                ) {
                                    Text(
                                        text = focusedVideo?.title ?: "",
                                        style = MaterialTheme.typography.displayMedium
                                    )
                                    Text(
                                        text = focusedVideo?.description ?: "",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            } else {
                                Text(
                                    text = categoryName,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 48.dp, vertical = 8.dp)
                                )
                            }

                            LazyRow(
                                modifier = Modifier.focusRestorer(),
                                contentPadding = PaddingValues(horizontal = 48.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                itemsIndexed(videos) { vidIndex, video ->
                                    CompactCard(
                                        onClick = { onVideoClick(video) },
                                        image = {
                                            AsyncImage(
                                                model = video.cardImageUrl,
                                                contentDescription = video.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        },
                                        title = { Text(video.title) },
                                        modifier = Modifier
                                            .then(
                                                if (catIndex == 0 && vidIndex == 0) {
                                                    Modifier.focusRequester(firstCardFocusRequester)
                                                } else {
                                                    Modifier
                                                }
                                            )
                                            .onFocusChanged { focusState ->
                                                if (focusState.isFocused) {
                                                    focusedVideo = video
                                                    focusedCategoryIndex = catIndex
                                                }
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PositionFocusedItemInLazyLayout(
    parentFraction: Float = 0.35f,
    childFraction: Float = 0.5f,
    content: @Composable () -> Unit,
) {
    val bringIntoViewSpec = remember(parentFraction, childFraction) {
        object : BringIntoViewSpec {
            override fun calculateScrollDistance(
                offset: Float,
                size: Float,
                containerSize: Float
            ): Float {
                if (offset >= 0f && offset <= containerSize * 0.45f) {
                    return 0f
                }
                val initialTargetForLeadingEdge = parentFraction * containerSize - (childFraction * size)
                val targetForLeadingEdge = if (size <= containerSize && (containerSize - initialTargetForLeadingEdge) < size) {
                    containerSize - size
                } else {
                    initialTargetForLeadingEdge
                }
                return offset - targetForLeadingEdge
            }
        }
    }
    CompositionLocalProvider(LocalBringIntoViewSpec provides bringIntoViewSpec, content = content)
}
// [END android_compose_tv_immersive_browse]
