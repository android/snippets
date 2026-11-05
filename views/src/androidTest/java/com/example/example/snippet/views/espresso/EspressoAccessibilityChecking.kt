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

package com.example.example.snippet.views.espresso

import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.example.snippet.views.R
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils.matchesCheck
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils.matchesViews
import com.google.android.apps.common.testing.accessibility.framework.checks.TextContrastCheck
import org.hamcrest.Matchers.allOf
import org.junit.runner.RunWith

// [START android_testing_espresso_a11y_enable]
// import androidx.test.espresso.accessibility.AccessibilityChecks

@RunWith(AndroidJUnit4::class)
@LargeTest
class MyWelcomeWorkflowIntegrationTest {
    init {
        AccessibilityChecks.enable()
    }
}
// [END android_testing_espresso_a11y_enable]

private fun enableFromRootView() {
    // [START android_testing_espresso_a11y_run_from_root]
    AccessibilityChecks.enable().setRunChecksFromRootView(true)
    // [END android_testing_espresso_a11y_run_from_root]
}

private fun suppressSubsetOfResults() {
    // [START android_testing_espresso_a11y_suppress_results]
    AccessibilityChecks.enable().apply {
        setSuppressingResultMatcher(
            allOf(
                matchesCheck(TextContrastCheck::class.java),
                matchesViews(withId(R.id.countTV))
            )
        )
    }
    // [END android_testing_espresso_a11y_suppress_results]
}
