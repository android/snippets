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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextMotion
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.LocalTextStyle
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.pager.GlimmerHorizontalPager
import androidx.xr.glimmer.pager.GlimmerHorizontalPagerDefaults
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
    // [START androidxr_glimmer_page_indicator]
    val pagerState = rememberGlimmerPagerState(pageCount = { 10 })
    GlimmerHorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        pageIndicator = { GlimmerHorizontalPagerDefaults.PageIndicator(pagerState) }
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Page: $it",
                style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated),
            )
        }
    }
    // [END androidxr_glimmer_page_indicator]
}
