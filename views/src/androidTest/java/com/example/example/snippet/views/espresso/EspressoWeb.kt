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

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.Atoms.getCurrentUrl
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.DriverAtoms.clearElement
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.getText
import androidx.test.espresso.web.webdriver.DriverAtoms.webClick
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// In a real app, MyWebViewActivity lives in src/main/java.
private class MyWebViewActivity : ComponentActivity()

private object EspressoWebSnippets {
    fun basicWebViewInteraction() {
        // [START android_testing_espresso_web_basic_example]
        onWebView()
            .withElement(findElement(Locator.ID, "link_2")) // similar to onView(withId(...)).
            .perform(webClick()) // Similar to perform(click()).
            // Similar to check(matches(...)).
            .check(webMatches(getCurrentUrl(), containsString("navigation_2.html")))
        // [END android_testing_espresso_web_basic_example]
    }

    // [START android_testing_espresso_web_force_javascript]
    @RunWith(AndroidJUnit4::class)
    class MyTestSuite {
        @get:Rule val activityScenarioRule =
            activityScenarioRule<MyWebViewActivity>()

        @Test fun testWebViewInteraction() {
            onWebView().forceJavascriptEnabled()
        }
    }
    // [END android_testing_espresso_web_force_javascript]

    fun webWithElement() {
        // [START android_testing_espresso_web_with_element]
        onWebView().withElement(findElement(Locator.ID, "teacher"))
        // [END android_testing_espresso_web_with_element]
    }

    fun webWithContextualElement() {
        onWebView()
            // [START android_testing_espresso_web_with_contextual_element]
            .withElement(findElement(Locator.ID, "teacher"))
            .withContextualElement(findElement(Locator.ID, "person_name"))
        // [END android_testing_espresso_web_with_contextual_element]
    }

    fun webCheckMatches() {
        // [START android_testing_espresso_web_check_matches]
        onWebView()
            .withElement(findElement(Locator.ID, "teacher"))
            .withContextualElement(findElement(Locator.ID, "person_name"))
            .check(webMatches(getText(), containsString("Socrates")))
        // [END android_testing_espresso_web_check_matches]
    }

    fun webPerformClick() {
        // [START android_testing_espresso_web_perform_click]
        onWebView()
            .withElement(findElement(Locator.ID, "teacher"))
            .perform(webClick())
        // [END android_testing_espresso_web_perform_click]
    }

    // [START android_testing_espresso_web_full_example]
    const val MACCHIATO = "Macchiato"

    @RunWith(AndroidJUnit4::class)
    class MyEspressoWebTestSuite {

        @Test fun typeTextInInput_clickButton_SubmitsForm() {
            // Create an intent that displays a web form.
            val webFormIntent = Intent()
            // ...

            // Lazily launch the Activity with a custom start Intent per test.
            ActivityScenario.launch<MyWebViewActivity>(webFormIntent)

            // Selects the WebView in your layout. If you have multiple WebView
            // objects, you can also use a matcher to select a given WebView,
            // onWebView(withId(R.id.web_view)).
            onWebView()
                // Find the input element by ID.
                .withElement(findElement(Locator.ID, "text_input"))
                // Clear previous input and enter new text into the input element.
                .perform(clearElement())
                .perform(DriverAtoms.webKeys(MACCHIATO))
                // Find the "Submit" button and simulate a click using JavaScript.
                .withElement(findElement(Locator.ID, "submitBtn"))
                .perform(webClick())
                // Find the response element by ID, and verify that it contains the
                // entered text.
                .withElement(findElement(Locator.ID, "response"))
                .check(webMatches(getText(), containsString(MACCHIATO)))
        }
    }
    // [END android_testing_espresso_web_full_example]
}
