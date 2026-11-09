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

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.test.uiautomator.ResultsReporter
import androidx.test.uiautomator.activeWindow
import androidx.test.uiautomator.onElement
import androidx.test.uiautomator.saveToFile
import androidx.test.uiautomator.takeScreenshot
import androidx.test.uiautomator.textAsString
import androidx.test.uiautomator.uiAutomator
import androidx.test.uiautomator.waitForStable
import androidx.test.uiautomator.watcher.PermissionDialog
import java.io.File
import org.junit.Test

private class UiAutomatorSnippets {
    private val myIntent = Intent()

    fun scopeExample() {
        // [START android_testing_other_components_ui_automator_scope]
        uiAutomator {
            // All your UI Automator actions go here.
            startApp("com.example.targetapp")
            onElement { textAsString() == "Hello, World!" }.click()
        }
        // [END android_testing_other_components_ui_automator_scope]
    }

    fun findUiElements() = uiAutomator {
        // [START android_testing_other_components_ui_automator_on_element]
        // Find a button with the text "Submit" and click it.
        onElement { textAsString() == "Submit" }.click()

        // Find a UI element by its resource ID.
        onElement { viewIdResourceName == "my_button_id" }.click()

        // Allow a permission request.
        watchFor(PermissionDialog) {
            clickAllow()
        }
        // [END android_testing_other_components_ui_automator_on_element]

        // [START android_testing_other_components_ui_automator_on_element_or_null]
        val optionalButton = onElementOrNull { textAsString() == "Skip" }
        optionalButton?.click() // Click only if the button exists.
        // [END android_testing_other_components_ui_automator_on_element_or_null]

        // [START android_testing_other_components_ui_automator_on_elements]
        // Get all items in a list Ui element.
        val listItems = onElements { className == "android.widget.TextView" && isClickable }
        listItems.forEach { it.click() }
        // [END android_testing_other_components_ui_automator_on_elements]

        // [START android_testing_other_components_ui_automator_chained]
        // Find a parent Ui element with ID "first", then its child with ID "second",
        // then its grandchild with ID "third", and click it.
        onElement { viewIdResourceName == "first" }
            .onElement { viewIdResourceName == "second" }
            .onElement { viewIdResourceName == "third" }
            .click()
        // [END android_testing_other_components_ui_automator_chained]

        // [START android_testing_other_components_ui_automator_timeout]
        // Find a Ui element with a zero timeout (instant check).
        onElement(0) { viewIdResourceName == "something" }.click()

        // Find a Ui element with a custom timeout of 10 seconds.
        onElement(10_000) { textAsString() == "Long loading text" }.click()
        // [END android_testing_other_components_ui_automator_timeout]
    }

    fun interactWithElements() = uiAutomator {
        // [START android_testing_other_components_ui_automator_interact]
        // Click a Ui element.
        onElement { textAsString() == "Tap Me" }.click()

        // Set text in an editable field.
        onElement { className == "android.widget.EditText" }.setText("My input text")

        // Perform a long click.
        onElement { contentDescription == "Context Menu" }.longClick()
        // [END android_testing_other_components_ui_automator_interact]
    }

    fun appLifecycle() = uiAutomator {
        // [START android_testing_other_components_ui_automator_lifecycle]
        // Start a specific app by package name. Used for benchmarking and other
        // self-instrumenting tests.
        startApp("com.example.targetapp")

        // Start a specific activity within the target app.
        startActivity(SomeActivity::class.java)

        // Start an intent.
        startActivityIntent(myIntent)

        // Clear the app's data (resets it to a fresh state).
        clearAppData()
        // [END android_testing_other_components_ui_automator_lifecycle]
    }

    // [START android_testing_other_components_ui_automator_permission_watcher]
    // import androidx.test.uiautomator.PermissionDialog

    @Test
    fun myTestWithPermissionHandling() = uiAutomator {
        startActivity(MainActivity::class.java)

        // Register a watcher to click "Allow" if a permission dialog appears.
        watchFor(PermissionDialog) { clickAllow() }

        // Your test steps that might trigger a permission dialog.
        onElement { textAsString() == "Request Permissions" }.click()

        // Example: You can register a different watcher later if needed.
        clearAppData()

        // Now deny permissions.
        startApp("com.example.targetapp")
        watchFor(PermissionDialog) { clickDeny() }
        onElement { textAsString() == "Request Permissions" }.click()
    }
    // [END android_testing_other_components_ui_automator_permission_watcher]

    fun waitForAppVisibilityAndStability() = uiAutomator {
        // [START android_testing_other_components_ui_automator_wait_visible]
        // Wait for the app to be visible after launching it.
        startApp("com.example.targetapp")
        waitForAppToBeVisible("com.example.targetapp")
        // [END android_testing_other_components_ui_automator_wait_visible]

        // [START android_testing_other_components_ui_automator_wait_stable]
        // Wait for the entire active window to become stable.
        device.activeWindow().waitForStable()

        // Wait for a specific Ui element to become stable (e.g., after a loading animation).
        onElement { viewIdResourceName == "my_loading_indicator" }.waitForStable()
        // [END android_testing_other_components_ui_automator_wait_stable]
    }

    fun pipWindowInteraction() = uiAutomator {
        // [START android_testing_other_components_ui_automator_pip_window]
        // Find the first window that is in Picture-in-Picture mode.
        val pipWindow = windows()
            .first { it.isInPictureInPictureMode == true }

        // Now you can interact with elements within that specific window.
        pipWindow.onElement { textAsString() == "Play" }.click()
        // [END android_testing_other_components_ui_automator_pip_window]
    }

    fun screenshotsExample() {
        // [START android_testing_other_components_ui_automator_screenshots]
        uiAutomator {
            // Take a screenshot of the entire active window.
            val fullScreenBitmap: Bitmap = device.activeWindow().takeScreenshot()
            fullScreenBitmap.saveToFile(File("/sdcard/Download/full_screen.png"))

            // Take a screenshot of a specific UI element (e.g., a button).
            val buttonBitmap: Bitmap = onElement { viewIdResourceName == "my_button" }.takeScreenshot()
            buttonBitmap.saveToFile(File("/sdcard/Download/my_button_screenshot.png"))

            // Example: Take a screenshot of a PiP window.
            val pipWindowScreenshot = windows()
                .first { it.isInPictureInPictureMode == true }
                .takeScreenshot()
            pipWindowScreenshot.saveToFile(File("/sdcard/Download/pip_screenshot.png"))
        }
        // [END android_testing_other_components_ui_automator_screenshots]
    }

    fun resultsReporterExample() {
        // [START android_testing_other_components_ui_automator_results_reporter]
        uiAutomator {
            startApp("com.example.targetapp")

            val reporter = ResultsReporter("MyTestArtifacts") // Name for this set of results.
            val file = reporter.addNewFile(
                filename = "my_screenshot",
                title = "Accessible button image" // Title that appears in Android Studio test results.
            )

            // Take a screenshot of an element and save it using the reporter.
            onElement { textAsString() == "Accessible button" }
                .takeScreenshot()
                .saveToFile(file)

            // Report the artifacts to instrumentation, making them visible in Android Studio.
            reporter.reportToInstrumentation()
        }
        // [END android_testing_other_components_ui_automator_results_reporter]
    }
}

private class SomeActivity : Activity()

private class MainActivity : Activity()
