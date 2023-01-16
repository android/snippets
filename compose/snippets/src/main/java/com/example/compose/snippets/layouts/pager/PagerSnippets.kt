package com.example.compose.snippets.layouts.pager

import android.inputmethodservice.Keyboard
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.viewpager.widget.PagerAdapter
import com.example.compose.snippets.R
import kotlinx.coroutines.launch

/*
* Copyright 2022 The Android Open Source Project
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
@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun HorizontalPagerExample() {
    // [START android_compose_layouts_pager_horizontal_basic]
    HorizontalPager(pageCount = 10) { page ->
        // The individual page content
        Text(
            text = "Page: $page",
            modifier = Modifier.fillMaxSize()
        )
    }
    // [END android_compose_layouts_pager_horizontal_basic]
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun VerticalPagerExample() {
    // [START android_compose_layouts_pager_vertical_basic]
    VerticalPager(pageCount = 10) { page ->
        // The individual page content
        Text(
            text = "Page: $page",
            modifier = Modifier.fillMaxSize()
        )
    }
    // [END android_compose_layouts_pager_vertical_basic]
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PagerJumpToPage() {
    // [START android_compose_layouts_pager_jump_to_page]
    Box {
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

        val coroutineScope = rememberCoroutineScope()
        Button(onClick = {
            coroutineScope.launch {
                pagerState.scrollToPage(5)
            }
        }, modifier = Modifier.align(Alignment.BottomCenter)) {
            Text("Jump to Page 5")
        }
    }
    // [END android_compose_layouts_pager_jump_to_page]
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PagerAnimateToPage() {

    Box {
        // [START android_compose_layouts_pager_animate_to_page]
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

        val coroutineScope = rememberCoroutineScope()
        Button(onClick = {
            coroutineScope.launch {
                pagerState.animateScrollToPage(5,
                    animationSpec = tween(1000))
            }
        }, modifier = Modifier.align(Alignment.BottomCenter)) {
            Text("Animate to Page 5")
        }
        // [END android_compose_layouts_pager_animate_to_page]
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PagerContentPadding() {
    // [START android_compose_layouts_pager_content_padding]
    HorizontalPager(pageCount = 10,
        contentPadding = PaddingValues(16.dp)
    ) { page ->
        // Our page content
        Text(
            text = "Page: $page",
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFA5D6A7))
        )
    }
    // [END android_compose_layouts_pager_content_padding]
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PagerNotifiedPageChanges() {
    Box {
        // [START android_compose_pager_notified_changes]
        val pagerState = rememberPagerState()

        LaunchedEffect(pagerState) {
            // Collect from the pager state a snapshotFlow reading the currentPage
            snapshotFlow { pagerState.currentPage }.collect { page ->
                // Do something with each page change, for example:
                // viewModel.sendPageSelectedEvent(page)
                Log.d("Page change", "Page changed to $page")
            }
        }

        HorizontalPager(pageCount = 10,
            state = pagerState
        ) { page ->
            // Our page content
            Text(
                text = "Page: $page",
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        // [END android_compose_pager_notified_changes]
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PagerIndicator() {
    Box {
        // [START android_compose_pager_indicator]
        val pageCount = 10
        val pagerState = rememberPagerState()

        HorizontalPager(pageCount = pageCount,
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
                Box(modifier = Modifier
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

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PagerTransformContent() {
    val pageCount = 10
    val pagerState = rememberPagerState()

    HorizontalPager(pageCount = pageCount,
        state = pagerState
    ) { page ->
        // Our page content
        PagerItem(index = page)
    }
}

@Composable
fun PagerItem(index: Int) {
    Text(
        text = "Page: $index",
        modifier = Modifier
            .fillMaxSize()
    )
}


@Composable
fun LazyListEx() {
    LazyColumn(content = {

    },)
}
