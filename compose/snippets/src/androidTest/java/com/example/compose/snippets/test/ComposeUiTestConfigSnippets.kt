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

package com.example.compose.snippets.test

import androidx.compose.ui.input.InputMode
import androidx.compose.ui.test.ComposeUiTestConfig
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds
import org.junit.Rule
import org.junit.Test

@Suppress("DEPRECATION")
private class DeprecatedConfigSnippets {
    // [START android_compose_test_config_deprecated]
    @get:Rule
    val rule = createComposeRule(
        effectContext = EmptyCoroutineContext,
        testTimeout = 30.seconds
    )

    // OR

    // [START_EXCLUDE silent]
    val runTest1 =
        // [END_EXCLUDE]
        runComposeUiTest(
            effectContext = EmptyCoroutineContext,
            runTestContext = EmptyCoroutineContext,
            testTimeout = 30.seconds
        ) {}

    // OR

    // [START_EXCLUDE silent]
    val runTest2 =
        // [END_EXCLUDE]
        runComposeUiTest(runTestContext = EmptyCoroutineContext) {}

    // or any other combination of parameters.
    // [END android_compose_test_config_deprecated]
}

private class RecommendedConfigSnippets {
    // [START android_compose_test_config_recommended]
    val testConfig = ComposeUiTestConfig(
        effectContext = EmptyCoroutineContext,
        runTestContext = EmptyCoroutineContext,
        testTimeout = 30.seconds
    )

    @get:Rule
    val rule = createComposeRule(config = testConfig)

    // OR

    // [START_EXCLUDE silent]
    val runTest =
        // [END_EXCLUDE]
        runComposeUiTest(config = testConfig) {}
    // [END android_compose_test_config_recommended]
}

// [START android_compose_test_input_mode_rule]
class FocusTest {
    @get:Rule
    val rule = createComposeRule(
        config = ComposeUiTestConfig(inputMode = InputMode.Keyboard)
    )

    @Test
    fun testFocus() {}
}
// [END android_compose_test_input_mode_rule]

private fun focusTestRunComposeScope() {
    // [START android_compose_test_input_mode_run_compose_ui_test]
    class FocusTest {
        @Test
        fun testTouchMode() = runComposeUiTest {
            // Runs with the default InputMode.Touch
        }

        @Test
        fun testKeyboardMode() = runComposeUiTest(
            ComposeUiTestConfig(inputMode = InputMode.Keyboard)
        ) {
            // Runs with InputMode.Keyboard
        }
    }
    // [END android_compose_test_input_mode_run_compose_ui_test]
}
