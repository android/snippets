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

package com.example.compose.snippets.test.differentscreens

import android.graphics.Rect
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.device.DeviceInteraction.Companion.setDisplaySize
import androidx.test.espresso.device.DeviceInteraction.Companion.setTabletopMode
import androidx.test.espresso.device.EspressoDevice.Companion.onDevice
import androidx.test.espresso.device.action.ScreenOrientation
import androidx.test.espresso.device.controller.DeviceMode.BOOK
import androidx.test.espresso.device.controller.DeviceMode.TABLETOP
import androidx.test.espresso.device.filter.RequiresDeviceMode
import androidx.test.espresso.device.filter.RequiresDisplay
import androidx.test.espresso.device.rules.DisplaySizeRule
import androidx.test.espresso.device.rules.ScreenOrientationRule
import androidx.test.espresso.device.sizeclass.HeightSizeClass
import androidx.test.espresso.device.sizeclass.HeightSizeClass.Companion.HeightSizeClassEnum
import androidx.test.espresso.device.sizeclass.HeightSizeClass.Companion.HeightSizeClassEnum.COMPACT
import androidx.test.espresso.device.sizeclass.WidthSizeClass
import androidx.test.espresso.device.sizeclass.WidthSizeClass.Companion.WidthSizeClassEnum
import androidx.test.espresso.device.sizeclass.WidthSizeClass.Companion.WidthSizeClassEnum.EXPANDED
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature.Orientation
import androidx.window.layout.FoldingFeature.Orientation.Companion.VERTICAL
import androidx.window.layout.FoldingFeature.State
import androidx.window.layout.FoldingFeature.State.Companion.HALF_OPENED
import androidx.window.testing.layout.FoldingFeature
import androidx.window.testing.layout.TestWindowLayoutInfo
import androidx.window.testing.layout.WindowLayoutInfoPublisherRule
import com.example.compose.snippets.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Composable
private fun DeviceConfigurationOverrideForcedSize() {
    // [START android_compose_testing_different_screens_device_configuration_override]
    DeviceConfigurationOverride(
        DeviceConfigurationOverride.ForcedSize(DpSize(1280.dp, 800.dp))
    ) {
        MyScreen() // Will be rendered in the space for 1280dp by 800dp without clipping.
    }
    // [END android_compose_testing_different_screens_device_configuration_override]
}

// [START android_compose_testing_different_screens_robolectric_config]
@RunWith(RobolectricTestRunner::class)
// Configure Robolectric to use a very large screen size that can fit all of the test sizes.
// This allows enough room to render the content under test without clipping or scaling.
@Config(qualifiers = "w1000dp-h1000dp-480dpi")
// [START_EXCLUDE silent]
/*
// [END_EXCLUDE]
class NiaAppScreenSizesScreenshotTests { ... }
// [START_EXCLUDE silent]
*/
class NiaAppScreenSizesScreenshotTests
// [END_EXCLUDE]
// [END android_compose_testing_different_screens_robolectric_config]

private fun setQualifiersFromSpecs() {
    // [START android_compose_testing_different_screens_robolectric_set_qualifiers]
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val (width, height, dpi) = ...
    // [START_EXCLUDE silent]
     */
    val (width, height, dpi) = Triple(1000, 1000, 480)
    // [END_EXCLUDE]

    // Set qualifiers from specs.
    RuntimeEnvironment.setQualifiers("w${width}dp-h${height}dp-${dpi}dpi")
    // [END android_compose_testing_different_screens_robolectric_set_qualifiers]
}

// [START android_compose_testing_different_screens_width_annotations]
annotation class TestExpandedWidth
annotation class TestCompactWidth
// [END android_compose_testing_different_screens_width_annotations]

// [START android_compose_testing_different_screens_filtered_test_class]
class MyTestClass {

    @Test
    @TestExpandedWidth
    fun myExample_worksOnTablet() {
        // ...
    }

    @Test
    @TestCompactWidth
    fun myExample_worksOnPortraitPhone() {
        // ...
    }
}
// [END android_compose_testing_different_screens_filtered_test_class]

// [START android_compose_testing_different_screens_espresso_device_tabletop]
@RunWith(AndroidJUnit4::class)
class OnDeviceTest {

    @get:Rule(order = 1) val activityScenarioRule = activityScenarioRule<MainActivity>()

    @get:Rule(order = 2) val screenOrientationRule: ScreenOrientationRule =
        ScreenOrientationRule(ScreenOrientation.PORTRAIT)

    @Test
    fun tabletopMode_playerIsDisplayed() {
        // Set the device to tabletop mode.
        onDevice().setTabletopMode()
        onView(withId(R.id.player)).check(matches(isDisplayed()))
    }
}
// [END android_compose_testing_different_screens_espresso_device_tabletop]

private object RequiresDeviceModeExample {
    // [START android_compose_testing_different_screens_requires_device_mode]
    class OnDeviceTest {
        // ...
        @Test
        @RequiresDeviceMode(TABLETOP)
        @RequiresDeviceMode(BOOK)
        fun tabletopMode_playerIdDisplayed() {
            // Set the device to tabletop mode.
            onDevice().setTabletopMode()
            onView(withId(R.id.player)).check(matches(isDisplayed()))
        }
    }
    // [END android_compose_testing_different_screens_requires_device_mode]
}

private object RequiresDisplayExample {
    // [START android_compose_testing_different_screens_requires_display]
    class OnDeviceTest {
        // ...
        @Test
        @RequiresDisplay(EXPANDED, COMPACT)
        fun myScreen_expandedWidthCompactHeight() {
            // ...
        }
    }
    // [END android_compose_testing_different_screens_requires_display]
}

// [START android_compose_testing_different_screens_resize_display_compact]
@RunWith(AndroidJUnit4::class)
class ResizeDisplayTest {

    @get:Rule(order = 1) val activityScenarioRule = activityScenarioRule<MainActivity>()

    // Test rule for restoring device to its starting display size when a test case finishes.
    @get:Rule(order = 2) val displaySizeRule: DisplaySizeRule = DisplaySizeRule()

    @Test
    fun resizeWindow_compact() {
        onDevice().setDisplaySize(
            widthSizeClass = WidthSizeClass.COMPACT,
            heightSizeClass = HeightSizeClass.COMPACT
        )
        // Verify visual attributes or state restoration.
    }
}
// [END android_compose_testing_different_screens_resize_display_compact]

private object ResizeDisplayExpandedExample {
    // [START android_compose_testing_different_screens_resize_display_expanded]
    @RunWith(AndroidJUnit4::class)
    class ResizeDisplayTest {

        @get:Rule(order = 1) var activityScenarioRule = activityScenarioRule<MainActivity>()

        // Test rule for restoring device to its starting display size when a test case finishes.
        @get:Rule(order = 2) var displaySizeRule: DisplaySizeRule = DisplaySizeRule()

        /**
         * Setting the display size to EXPANDED would fail in small devices, so the [RequiresDisplay]
         * annotation prevents this test from being run on devices outside the EXPANDED buckets.
         */
        @RequiresDisplay(
            widthSizeClass = WidthSizeClassEnum.EXPANDED,
            heightSizeClass = HeightSizeClassEnum.EXPANDED
        )
        @Test
        fun resizeWindow_expanded() {
            onDevice().setDisplaySize(
                widthSizeClass = WidthSizeClass.EXPANDED,
                heightSizeClass = HeightSizeClass.EXPANDED
            )
            // Verify visual attributes or state restoration.
        }
    }
    // [END android_compose_testing_different_screens_resize_display_expanded]
}

private class StateRestorationTesterExample {
    @get:Rule
    val composeTestRule = createComposeRule()

    // [START android_compose_testing_different_screens_state_restoration_tester]
    @Test
    fun compactDevice_selectedEmailEmailRetained_afterConfigChange() {
        val stateRestorationTester = StateRestorationTester(composeTestRule)

        // Set content through the StateRestorationTester object.
        stateRestorationTester.setContent {
            MyApp()
        }

        // Simulate a config change.
        stateRestorationTester.emulateSavedInstanceStateRestore()
    }
    // [END android_compose_testing_different_screens_state_restoration_tester]
}

// [START android_compose_testing_different_screens_foldable_preview]
@Preview(showBackground = true, widthDp = 480, heightDp = 480)
@Composable private fun FoldablePreview() =
    MyApplicationTheme {
        ExampleScreen(
            displayFeatures = listOf(FoldingFeature(Rect(0, 240, 480, 240)))
        )
    }
// [END android_compose_testing_different_screens_foldable_preview]

// [START android_compose_testing_different_screens_window_testing_compose]
// import androidx.window.layout.FoldingFeature.Orientation.Companion.VERTICAL
// import androidx.window.layout.FoldingFeature.State.Companion.HALF_OPENED
// import androidx.window.testing.layout.FoldingFeature
// import androidx.window.testing.layout.TestWindowLayoutInfo
// import androidx.window.testing.layout.WindowLayoutInfoPublisherRule

@RunWith(AndroidJUnit4::class)
class MediaControlsFoldingFeatureTest {

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule(order = 2)
    val windowLayoutInfoPublisherRule = WindowLayoutInfoPublisherRule()

    @Test
    fun foldedWithHinge_foldableUiDisplayed() {
        composeTestRule.setContent {
            MediaPlayerScreen()
        }

        val hinge = FoldingFeature(
            activity = composeTestRule.activity,
            state = HALF_OPENED,
            orientation = VERTICAL,
            size = 2
        )

        val expected = TestWindowLayoutInfo(listOf(hinge))
        windowLayoutInfoPublisherRule.overrideWindowLayoutInfo(expected)

        composeTestRule.waitForIdle()

        // Verify that the folding feature is detected and media controls shown.
        composeTestRule.onNodeWithTag("MEDIA_CONTROLS").assertExists()
    }
}
// [END android_compose_testing_different_screens_window_testing_compose]

private object WindowTestingViewsExample {
    // [START android_compose_testing_different_screens_window_testing_views]
    // import androidx.window.layout.FoldingFeature.Orientation
    // import androidx.window.layout.FoldingFeature.State
    // import androidx.window.testing.layout.FoldingFeature
    // import androidx.window.testing.layout.TestWindowLayoutInfo
    // import androidx.window.testing.layout.WindowLayoutInfoPublisherRule

    @RunWith(AndroidJUnit4::class)
    class MediaControlsFoldingFeatureTest {

        @get:Rule(order = 1)
        val activityRule = ActivityScenarioRule(MediaPlayerActivity::class.java)

        @get:Rule(order = 2)
        val windowLayoutInfoPublisherRule = WindowLayoutInfoPublisherRule()

        @Test
        fun foldedWithHinge_foldableUiDisplayed() {
            activityRule.scenario.onActivity { activity ->
                val feature = FoldingFeature(
                    activity = activity,
                    state = State.HALF_OPENED,
                    orientation = Orientation.VERTICAL
                )
                val expected = TestWindowLayoutInfo(listOf(feature))
                windowLayoutInfoPublisherRule.overrideWindowLayoutInfo(expected)
            }

            // Verify that the folding feature is detected and media controls shown.
            onView(withId(R.id.media_controls)).check(matches(isDisplayed()))
        }
    }
    // [END android_compose_testing_different_screens_window_testing_views]
}

// Activities and composables under test normally live in the main source set.
class MainActivity : ComponentActivity()

class MediaPlayerActivity : ComponentActivity()

@Composable
private fun MyScreen() {}

@Composable
private fun MyApp() {}

@Composable
private fun MyApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}

@Composable
private fun ExampleScreen(displayFeatures: List<DisplayFeature>) {}

@Composable
private fun MediaPlayerScreen() {}
