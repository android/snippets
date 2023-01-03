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

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.compose.snippets.MyActivity
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class AccessibilitySnippetsTest {
    @Rule
    @JvmField
    val composeTestRule = createAndroidComposeRule<MyActivity>()

    private val nodeMatcher = SemanticsMatcher("DUMMY") { it.isRoot }

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
