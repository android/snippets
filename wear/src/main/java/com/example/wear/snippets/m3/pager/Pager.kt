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

package com.example.wear.snippets.m3.pager

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.AnimatedPage
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.HorizontalPagerScaffold
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ListHeaderDefaults
import androidx.wear.compose.material3.PagerScaffoldDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.VerticalPagerScaffold
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight

@Composable
fun HorizontalPager() {
    // [START android_wear_horizontal_pager]
    AppScaffold {
        val pagerState = rememberPagerState(pageCount = { 10 })

        HorizontalPagerScaffold(pagerState = pagerState) {
            HorizontalPager(
                state = pagerState,
                flingBehavior =
                    PagerScaffoldDefaults.snapWithSpringFlingBehavior(
                        state = pagerState
                    ),
            ) { page ->
                AnimatedPage(pageIndex = page, pagerState = pagerState) {
                    val columnState = rememberTransformingLazyColumnState()
                    val transformationSpec = rememberTransformationSpec()

                    ScreenScaffold(
                        scrollState = columnState,
                    ) { contentPadding ->
                        TransformingLazyColumn(
                            state = columnState,
                            contentPadding = contentPadding,
                        ) {
                            item {
                                ListHeader(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .transformedHeight(this, transformationSpec)
                                        .minimumVerticalContentPadding(
                                            ListHeaderDefaults.minimumTopListContentPadding
                                        ),
                                    transformation = SurfaceTransformation(transformationSpec),
                                ) {
                                    Text(text = "Pager sample")
                                }
                            }
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .transformedHeight(this, transformationSpec)
                                        .minimumVerticalContentPadding(
                                            CardDefaults.minimumVerticalListContentPadding
                                        ),
                                    transformation = SurfaceTransformation(transformationSpec),
                                ) {
                                    if (page == 0) {
                                        Text(text = "Page #$page. Swipe right")
                                    } else {
                                        Text(text = "Page #$page. Swipe left and right")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // [END android_wear_horizontal_pager]
}

@Composable
fun verticalPager() {
    // [START android_wear_vertical_pager]
    AppScaffold {
        val pagerState = rememberPagerState(pageCount = { 10 })

        VerticalPagerScaffold(pagerState = pagerState) {
            VerticalPager(
                state = pagerState,
                flingBehavior =
                    PagerScaffoldDefaults.snapWithSpringFlingBehavior(
                        state = pagerState
                    ),
            ) { page ->
                AnimatedPage(pageIndex = page, pagerState = pagerState) {
                    ScreenScaffold {
                        // ...
                    }
                }
            }
        }
    }
    // [END android_wear_vertical_pager]
}