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

package com.example.compose.snippets.glance

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.testing.unit.runGlanceAppWidgetUnitTest
import androidx.glance.layout.Row
import androidx.glance.semantics.semantics
import androidx.glance.semantics.testTag
import androidx.glance.testing.unit.assertHasText
import androidx.glance.testing.unit.hasTestTag
import androidx.glance.text.Text
import org.junit.Test

// [START android_compose_glance_unit_test]
private const val FAKE_HEADLINE = "EXTRA! EXTRA! READ ALL ABOUT IT!"

class MyGlanceComposableTest {
    @Test
    fun myNewsItemComposable_largeSize_hasHeadline() = runGlanceAppWidgetUnitTest {
        // Set the composable to test
        provideComposable {
            MyNewsItemComposable(FAKE_HEADLINE)
        }

        // Perform assertions
        onNode(hasTestTag("headline"))
            .assertHasText(FAKE_HEADLINE)
    }


    @Composable
    fun MyNewsItemComposable(headline: String) {
        Row {
            Text(
                text = headline,
                modifier = GlanceModifier.semantics { testTag = "headline" },
            )
        }
    }
}
// [END android_compose_glance_unit_test]