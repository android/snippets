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