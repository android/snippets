/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.compose.snippets.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.accessibility.enableAccessibilityChecks
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import androidx.compose.ui.unit.dp
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult
import com.google.android.apps.common.testing.accessibility.framework.integrations.espresso.AccessibilityValidator
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class AccessibilityTest {

// [START android_compose_accessibility_testing_label]
    @Rule
    @JvmField
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun noAccessibilityLabel() {
        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .size(50.dp, 50.dp)
                    .background(color = Color.Gray)
                    .clickable { }
                    .semantics {
                        contentDescription = ""
                    }
            )
        }

        composeTestRule.enableAccessibilityChecks()

        // Any action (such as performClick) will perform accessibility checks too:
        composeTestRule.onRoot().tryPerformAccessibilityChecks()
    }
// [END android_compose_accessibility_testing_label]

// [START android_compose_accessibility_testing_click]
    @Test
    fun smallClickTarget() {
        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .size(20.dp, 20.dp)
                    .background(color = Color(0xFFFAFBFC))
                    .clickable { }
            )
        }

        composeTestRule.enableAccessibilityChecks()

        // Any action (such as performClick) will perform accessibility checks too:
        composeTestRule.onRoot().tryPerformAccessibilityChecks()
    }
// [END android_compose_accessibility_testing_click]

// [START android_compose_accessibility_testing_validator]
    @Test
    fun lowContrastScreen() {
        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFFFAFBFC)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Hello", color = Color(0xFFB0B1B2))
            }
        }

        // Optionally, set AccessibilityValidator manually
        val accessibilityValidator = AccessibilityValidator()
            .setThrowExceptionFor(
                AccessibilityCheckResult.AccessibilityCheckResultType.WARNING
            )

        composeTestRule.enableAccessibilityChecks(accessibilityValidator)

        composeTestRule.onRoot().tryPerformAccessibilityChecks()
    }
// [END android_compose_accessibility_testing_validator]

    private val nodeMatcher = SemanticsMatcher(description = "DUMMY") { it.isRoot }

    @Ignore("Dummy test")
// [START android_compose_accessibility_testing]
    @Test
    fun test() {
        composeTestRule
            .onNode(nodeMatcher)
            .assert(
                SemanticsMatcher("onClickLabel is set correctly") {
                    it.config.getOrNull(SemanticsActions.OnClick)?.label == "My Click Label"
                }
            )
    }
// [END android_compose_accessibility_testing]
}
