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

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextMotion
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.LocalTextStyle
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.pager.GlimmerHorizontalPager
import androidx.xr.glimmer.pager.GlimmerPagerState
import androidx.xr.glimmer.pager.rememberGlimmerPagerState

@Composable
fun GlimmerHorizontalPagerSample() {
    // [START androidxr_glimmer_pager_basic]
    // Hoist the pager state, specifying the total page count with a lambda.
    val pagerState = rememberGlimmerPagerState(pageCount = { 10 })

    GlimmerHorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
    ) { page ->
        // Use Glimmer components like Card and Text for optimized glasses styling.
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Page: $page",
                // Recommended: use TextMotion.Animated for smooth transitions in a pager.
                style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated),
            )
        }
    }
    // [END androidxr_glimmer_pager_basic]
}

@Composable
fun TextMotion() {
    val page = null
    // [START androidxr_glimmer_text_motion]
    Text(
        text = "Page: $page",
        style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated),
    )
    // [END androidxr_glimmer_text_motion]
}

@Composable
fun GlimmerHorizontalPageIndicatorSample() {
    val pagerState = rememberGlimmerPagerState(pageCount = { 10 })
    // [START androidxr_glimmer_page_indicator]
    GlimmerHorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        // Use a page numbers instead of the default dot-indicator
        pageIndicator = {
            Text(
                text = "${pagerState.currentPage + 1} / ${pagerState.pageCount}",
                style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated),
            )
        }
    )
    // [END androidxr_glimmer_page_indicator]
    {
        // Content inside the pager
    }
}

@Composable
fun GlimmerPagerStateAnimateScrollToPageSample() {
    val state = rememberGlimmerPagerState(initialPage = 5, pageCount = { 10 })

    // [START androidxr_glimmer_animate_scroll_to_page]
    LaunchedEffect(Unit) {
        state.animateScrollToPage(1)
    }

    GlimmerHorizontalPager(modifier = Modifier.fillMaxSize(), state = state) { page ->
        Card {
            Text(
                text = "Page: $page",
                style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated),
            )
        }
    }
    // [END androidxr_glimmer_animate_scroll_to_page]
}

@Composable
fun GlimmerPagerStateScrollToPageSample() {
    val state = rememberGlimmerPagerState(initialPage = 5, pageCount = { 10 })

    // [START androidxr_glimmer_scroll_to_page]

    LaunchedEffect(Unit) {
        state.scrollToPage(1)
    }

    GlimmerHorizontalPager(modifier = Modifier.fillMaxSize(), state = state) { page ->
        Card {
            Text(
                text = "Page: $page",
                style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated),
            )
        }
    }
    // [END androidxr_glimmer_scroll_to_page]
}

@Composable
fun GlimmerPagerStateCustomAnimateScrollToPageSample() {

    // [START androidxr_glimmer_custom_animated_scroll_to_page]
    suspend fun GlimmerPagerState.customAnimateScrollToPage(page: Int) {
        val preJumpPosition =
            if (page > currentPage) {
                (page - 1).coerceAtLeast(0)
            } else {
                (page + 1).coerceAtMost(pageCount - 1)
            }
        scroll {
            // Update the target page
            updateTargetPage(page)

            // pre-jump to 1 page before our target page
            updateCurrentPage(preJumpPosition, pageOffsetFraction = 0.0f)
            val targetPageDiff = page - currentPage
            val distance = targetPageDiff * layoutInfo.pageSize.toFloat()
            var previousValue = 0.0f
            animate(0f, distance) { currentValue, _ ->
                previousValue += scrollBy(currentValue - previousValue)
            }
        }
    }

    val state = rememberGlimmerPagerState(initialPage = 5, pageCount = { 10 })

    LaunchedEffect(Unit) {
        state.customAnimateScrollToPage(1)
    }

    GlimmerHorizontalPager(modifier = Modifier.fillMaxSize(), state = state) { page ->
        Card {
            Text(
                text = "Page: $page",
                style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated),
            )
        }
    }
    // [END androidxr_glimmer_custom_animated_scroll_to_page]
}

