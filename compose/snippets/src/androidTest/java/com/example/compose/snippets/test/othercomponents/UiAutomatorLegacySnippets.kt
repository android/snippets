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

package com.example.compose.snippets.test.othercomponents

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("DEPRECATION")
private object UiAutomatorLegacySnippets {
    private lateinit var device: UiDevice
    private const val TIMEOUT = 5000L

    fun openGmail() {
        // [START android_testing_other_components_ui_automator_legacy_gmail]
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        val gmail: UiObject2 = device.findObject(By.text("Gmail"))
        // Perform a click and wait until the app is opened.
        val opened: Boolean = gmail.clickAndWait(Until.newWindow(), 3000)
        assertThat(opened).isTrue()
        // [END android_testing_other_components_ui_automator_legacy_gmail]
    }

    // [START android_testing_other_components_ui_automator_legacy_home_screen]
    // import org.junit.Before
    // import androidx.test.runner.AndroidJUnit4
    // import androidx.test.uiautomator.UiDevice
    // import androidx.test.uiautomator.By
    // import androidx.test.uiautomator.Until
    // ...

    private const val BASIC_SAMPLE_PACKAGE = "com.example.android.testing.uiautomator.BasicSample"
    private const val LAUNCH_TIMEOUT = 5000L
    private const val STRING_TO_BE_TYPED = "UiAutomator"

    @RunWith(AndroidJUnit4::class)
    @SdkSuppress(minSdkVersion = 18)
    class ChangeTextBehaviorTest2 {

        private lateinit var device: UiDevice

        @Before
        fun startMainActivityFromHomeScreen() {
            // Initialize UiDevice instance.
            device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

            // Start from the home screen.
            device.pressHome()

            // Wait for launcher.
            val launcherPackage: String = device.launcherPackageName
            assertThat(launcherPackage, notNullValue())
            device.wait(
                Until.hasObject(By.pkg(launcherPackage).depth(0)),
                LAUNCH_TIMEOUT
            )

            // Launch the app.
            val context = ApplicationProvider.getApplicationContext<Context>()
            val intent = context.packageManager.getLaunchIntentForPackage(
                BASIC_SAMPLE_PACKAGE
            )?.apply {
                // Clear out any previous instances.
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            context.startActivity(intent)

            // Wait for the app to appear.
            device.wait(
                Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT
            )
        }
    }
    // [END android_testing_other_components_ui_automator_legacy_home_screen]

    fun clickOkButton() {
        // [START android_testing_other_components_ui_automator_legacy_ok_button]
        val okButton: UiObject2 = device.findObject(
            By.text("OK").clazz("android.widget.Button")
        )

        // Simulate a user-click on the OK button, if found.
        if (okButton != null) {
            okButton.click()
        }
        // [END android_testing_other_components_ui_automator_legacy_ok_button]
    }

    fun findListView() {
        // [START android_testing_other_components_ui_automator_legacy_list_view]
        val listView: UiObject2 = device.findObject(
            By.clazz("android.widget.ListView")
                .hasChild(
                    By.text("Apps")
                )
        )
        // [END android_testing_other_components_ui_automator_legacy_list_view]
    }

    // [START android_testing_other_components_ui_automator_legacy_calculator_setup]
    fun setUp() {
        // ...

        // Launch a simple calculator app.
        val context = getInstrumentation().context
        val intent = context.packageManager.getLaunchIntentForPackage(CALC_PACKAGE)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        // Clear out any previous instances.
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(CALC_PACKAGE).depth(0)), TIMEOUT)
    }
    // [END android_testing_other_components_ui_automator_legacy_calculator_setup]

    // [START android_testing_other_components_ui_automator_legacy_calculator_test]
    private const val CALC_PACKAGE = "com.myexample.calc"

    fun testTwoPlusThreeEqualsFive() {
        // Enter an equation: 2 + 3 = ?
        device.findObject(By.res(CALC_PACKAGE, "two")).click()
        device.findObject(By.res(CALC_PACKAGE, "plus")).click()
        device.findObject(By.res(CALC_PACKAGE, "three")).click()
        device.findObject(By.res(CALC_PACKAGE, "equals")).click()

        // Verify the result = 5.
        val result: UiObject2 = device.findObject(By.res(CALC_PACKAGE, "result"))
        assertEquals("5", result.text)
    }
    // [END android_testing_other_components_ui_automator_legacy_calculator_test]

    fun interactWithSystemUi() {
        // [START android_testing_other_components_ui_automator_legacy_open_settings]
        // Opens the System Settings.
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.executeShellCommand("am start -a android.settings.SETTINGS")
        // [END android_testing_other_components_ui_automator_legacy_open_settings]

        // [START android_testing_other_components_ui_automator_legacy_open_notification]
        // Opens the notification shade.
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.openNotification()
        // [END android_testing_other_components_ui_automator_legacy_open_notification]

        // [START android_testing_other_components_ui_automator_legacy_open_quick_settings]
        // Opens the Quick Settings shade.
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.openQuickSettings()
        // [END android_testing_other_components_ui_automator_legacy_open_quick_settings]

        // [START android_testing_other_components_ui_automator_legacy_system_clock]
        // Get the system clock.
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val clock: UiObject2 = device.findObject(By.res("com.android.systemui:id/clock"))
        print(clock.getText())
        // [END android_testing_other_components_ui_automator_legacy_system_clock]
    }

    // [START android_testing_other_components_ui_automator_legacy_turn_off_dnd]
    @Test
    @SdkSuppress(minSdkVersion = 21)
    @Throws(Exception::class)
    fun turnOffDoNotDisturb() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.performActionAndWait({
            try {
                device.executeShellCommand("am start -a android.settings.SETTINGS")
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }, Until.newWindow(), 1000)
        // Check system settings has been opened.
        Assert.assertTrue(device.hasObject(By.pkg("com.android.settings")))

        // Scroll the settings to the top and find Notifications button.
        var scrollableObj: UiObject2 = device.findObject(By.scrollable(true))
        scrollableObj.scrollUntil(Direction.UP, Until.scrollFinished(Direction.UP))
        val notificationsButton = scrollableObj.findObject(By.text("Notifications"))

        // Click the Notifications button and wait until a new window is opened.
        device.performActionAndWait({ notificationsButton.click() }, Until.newWindow(), 1000)
        scrollableObj = device.findObject(By.scrollable(true))
        // Scroll down until it finds a Do Not Disturb button.
        val doNotDisturb = scrollableObj.scrollUntil(
            Direction.DOWN,
            Until.findObject(By.textContains("Do Not Disturb"))
        )
        device.performActionAndWait({ doNotDisturb.click() }, Until.newWindow(), 1000)
        // Turn off the Do Not Disturb.
        val turnOnDoNotDisturb = device.findObject(By.text("Turn on now"))
        turnOnDoNotDisturb?.click()
        Assert.assertTrue(device.wait(Until.hasObject(By.text("Turn off now")), 1000))
    }
    // [END android_testing_other_components_ui_automator_legacy_turn_off_dnd]
}
