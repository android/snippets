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

package com.example.snippets.performance.benchmarking

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// [START android_performance_macrobenchmark_compose_scroll]
@LargeTest
@RunWith(AndroidJUnit4::class)
class ComposeScrollBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollLazyColumn() = benchmarkRule.measureRepeated(
        packageName = "com.example.compose.app",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM,
        setupBlock = { pressHome() }
    ) {
        startActivityAndWait()

        // Find the Compose node using the testTag defined in your app
        val lazyColumn = device.findObject(By.res("my_lazy_column"))

        // Simulate a scroll gesture to measure FrameTimingMetric
        lazyColumn.setGestureMargin(device.displayWidth / 5)
        lazyColumn.fling(Direction.DOWN)
    }
}
// [END android_performance_macrobenchmark_compose_scroll]
