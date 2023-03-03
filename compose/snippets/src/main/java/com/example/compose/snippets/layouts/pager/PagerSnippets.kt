@file:OptIn(
    ExperimentalFoundationApi::class
)
package com.example.compose.snippets.layouts.pager

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/*
* Copyright 2023 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

@Preview
@Composable
fun HorizontalPagerSample() {
    // [START android_compose_layouts_pager_horizontal_basic]
    // Display 10 items
    HorizontalPager(pageCount = 10) { page ->
        // Our page content
        Text(
            text = "Page: $page",
            modifier = Modifier.fillMaxWidth()
        )
    }
    // [END android_compose_layouts_pager_horizontal_basic]
}

@Preview
@Composable
fun VerticalPagerSample() {
    // [START android_compose_layouts_pager_vertical_basic]
    // Display 10 items
    VerticalPager(pageCount = 10) { page ->
        // Our page content
        Text(
            text = "Page: $page",
            modifier = Modifier.fillMaxWidth()
        )
    }
    // [END android_compose_layouts_pager_vertical_basic]
}

@Preview
@Composable
fun PagerScrollToItem() {
    Box {
        // [START android_compose_layouts_pager_scroll]
        val pagerState = rememberPagerState()

        HorizontalPager(pageCount = 10, state = pagerState) { page ->
            // Our page content
            Text(
                text = "Page: $page",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
        }

        // scroll to page
        val coroutineScope = rememberCoroutineScope()
        Button(onClick = {
            coroutineScope.launch {
                // Call scroll to on pagerState
                pagerState.scrollToPage(5)
            }
        }, modifier = Modifier.align(Alignment.BottomCenter)) {
            Text("Jump to Page 5")
        }
        // [END android_compose_layouts_pager_scroll]
    }
}

@Preview
@Composable
fun PagerAnimateToItem() {
    Box {
        // [START android_compose_layouts_pager_scroll_animate]
        val pagerState = rememberPagerState()

        HorizontalPager(pageCount = 10, state = pagerState) { page ->
            // Our page content
            Text(
                text = "Page: $page",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
        }

        // scroll to page
        val coroutineScope = rememberCoroutineScope()
        Button(onClick = {
            coroutineScope.launch {
                // Call scroll to on pagerState
                pagerState.animateScrollToPage(5)
            }
        }, modifier = Modifier.align(Alignment.BottomCenter)) {
            Text("Jump to Page 5")
        }
        // [END android_compose_layouts_pager_scroll_animate]
    }
}


@Preview
@Composable
fun PageChangesSample() {
    // [START android_compose_layouts_pager_notify_page_changes]
    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Do something with each page change, for example:
            // viewModel.sendPageSelectedEvent(page)
            Log.d("Page change", "Page changed to $page")
        }
    }

    VerticalPager(
        pageCount = 10,
        state = pagerState,
    ) { page ->
        Text(text = "Page: $page")
    }
    // [END android_compose_layouts_pager_notify_page_changes]
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun PagerWithTabsExample() {
    val pages = listOf("Movies", "Books", "Shows", "Fun")
    // [START android_compose_layouts_pager_tabs]
    val pagerState = rememberPagerState()

    TabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        /*// Override the indicator, using the provided pagerTabIndicatorOffset modifier
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }*/
    ) {
        // Add tabs for all of our pages
        pages.forEachIndexed { index, title ->
            Tab(
                text = { Text(title) },
                selected = pagerState.currentPage == index,
                onClick = { },
            )
        }
    }

    HorizontalPager(
        pageCount = pages.size,
        state = pagerState,
    ) { page ->
        Text("Page: ${pages[page]}")
    }
    // [END android_compose_layouts_pager_tabs]
}

@Preview
@Composable
fun PagerWithEffect() {
    // [START android_compose_layouts_pager_transformation]
    val pagerState = rememberPagerState()
    HorizontalPager(pageCount = 4, state = pagerState) { page ->
        Card(
            Modifier
                .size(200.dp)
                .graphicsLayer {
                    // Calculate the absolute offset for the current page from the
                    // scroll position. We use the absolute value which allows us to mirror
                    // any effects for both directions
                    val pageOffset = ((pagerState.currentPage - page) + pagerState
                        .currentPageOffsetFraction).absoluteValue

                    // We animate the alpha, between 50% and 100%
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
        ) {
            // Card content
        }
    }
    // [END android_compose_layouts_pager_transformation]
}

@Composable
@Preview
fun PagerStartPadding() {
    // [START android_compose_layouts_pager_padding_start]
    HorizontalPager(
        pageCount = 4,
        contentPadding = PaddingValues(start = 64.dp),
    ) { page ->
        // page content
    }
    // [END android_compose_layouts_pager_padding_start]
}

@Preview
@Composable
fun PagerHorizontalPadding() {
    // [START android_compose_layouts_pager_padding_horizontal]
    HorizontalPager(
        pageCount = 4,
        contentPadding = PaddingValues(horizontal = 32.dp),
    ) { page ->
        // page content
    }
    // [END android_compose_layouts_pager_padding_horizontal]
}

@Preview
@Composable
fun PagerEndPadding() {
    // [START android_compose_layouts_pager_padding_end]
    HorizontalPager(
        pageCount = 4,
        contentPadding = PaddingValues(end = 64.dp),
    ) { page ->
        // page content
    }
    // [END android_compose_layouts_pager_padding_end]
}

@Preview
@Composable
fun PagerCustomSizes() {
    // [START android_compose_layouts_pager_custom_size]
    HorizontalPager(
        pageCount = 4,
        pageSize = PageSize.Fixed(100.dp)
    ) { page ->
        // page content
    }
    // [END android_compose_layouts_pager_custom_size]
}

@Preview
@Composable
fun PagerWithTabs() {
    // [START android_compose_layouts_pager_with_tabs]
    HorizontalPager(
        pageCount = 4
    ) { page ->
        // page content
    }
    // [END android_compose_layouts_pager_with_tabs]
}
@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PagerIndicator() {
    Box {
        // [START android_compose_pager_indicator]
        val pageCount = 10
        val pagerState = rememberPagerState()

        HorizontalPager(
            pageCount = pageCount,
            state = pagerState
        ) { page ->
            // Our page content
            Text(
                text = "Page: $page",
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(20.dp)

                )
            }
        }
        // [END android_compose_pager_indicator]
    }
}
/*

@Preview
@Composable
fun HorizontalPagerIndicatorSample() {
    // [START android_compose_layouts_pager_horizontal_indicator]
    val pagerState = rememberPagerState()
    val pageCount = 10
    Column {
        // Display 10 items
        HorizontalPager(pageCount = pageCount) { page ->
            // Our page content
            Text(
                text = "Page: $page",
                modifier = Modifier.fillMaxWidth()
            )
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(16.dp),
            pageCount = pageCount
        )
    }
    // [END android_compose_layouts_pager_horizontal_indicator]
}*/
