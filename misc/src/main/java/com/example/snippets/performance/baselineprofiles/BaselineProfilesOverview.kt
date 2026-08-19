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

package com.example.snippets.performance.baselineprofiles

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.Direction
import org.junit.Rule
import org.junit.Test

private const val PACKAGE_NAME = "com.example.app"

// [START android_performance_baselineprofiles_overview_generator]
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun appStartupAndUserJourneys() {
        baselineProfileRule.collect(packageName = PACKAGE_NAME) {
            uiAutomator {
                // App startup journey.
                startApp(PACKAGE_NAME)

                // Find and click elements using the new DSL
                onElement { textAsString() == "COMPOSE LAZYLIST" }.click()
                onElement { viewIdResourceName == "myLazyColumn" }.also {
                    it.fling(Direction.DOWN)
                    it.fling(Direction.UP)
                }
                pressBack()
            }
        }
    }
}
// [END android_performance_baselineprofiles_overview_generator]

private class OverviewElementScope {
    val viewIdResourceName: String = ""
    fun textAsString(): String = ""
}

private class OverviewElement {
    fun click() {}
    fun fling(direction: Direction) {}
}

private class OverviewUiAutomatorScope {
    fun startApp(packageName: String) {}
    fun onElement(block: OverviewElementScope.() -> Boolean): OverviewElement = OverviewElement()
    fun pressBack() {}
}

private fun MacrobenchmarkScope.uiAutomator(block: OverviewUiAutomatorScope.() -> Unit) {
    OverviewUiAutomatorScope().block()
}
