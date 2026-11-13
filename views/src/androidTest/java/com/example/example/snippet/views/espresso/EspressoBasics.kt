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

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.example.snippet.views.R
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not

private fun basicsOverview() {
    // [START android_testing_espresso_basics_overview]
    // withId(R.id.my_view) is a ViewMatcher.
    // click() is a ViewAction.
    // matches(isDisplayed()) is a ViewAssertion.
    onView(withId(R.id.my_view))
        .perform(click())
        .check(matches(isDisplayed()))
    // [END android_testing_espresso_basics_overview]
}

private fun findById() {
    // [START android_testing_espresso_basics_find_by_id]
    onView(withId(R.id.my_view))
    // [END android_testing_espresso_basics_find_by_id]
}

private fun combineMatchersAllOf() {
    // [START android_testing_espresso_basics_all_of]
    onView(allOf(withId(R.id.my_view), withText("Hello!")))
    // [END android_testing_espresso_basics_all_of]
}

private fun negateMatcherNot() {
    // [START android_testing_espresso_basics_not]
    onView(allOf(withId(R.id.my_view), not(withText("Unwanted"))))
    // [END android_testing_espresso_basics_not]
}

private fun findButtonSimple() {
    // [START android_testing_espresso_basics_button_find]
    onView(withId(R.id.button_simple))
    // [END android_testing_espresso_basics_button_find]
}

private fun clickButtonSimple() {
    // [START android_testing_espresso_basics_button_click]
    onView(withId(R.id.button_simple)).perform(click())
    // [END android_testing_espresso_basics_button_click]
}

private fun findTextSimple() {
    // [START android_testing_espresso_basics_text_find]
    onView(withId(R.id.text_simple))
    // [END android_testing_espresso_basics_text_find]
}

private fun verifyTextSimple() {
    // [START android_testing_espresso_basics_text_verify]
    onView(withId(R.id.text_simple)).check(matches(withText("Hello Espresso!")))
    // [END android_testing_espresso_basics_text_verify]
}

private fun clickSpinnerSimple() {
    // [START android_testing_espresso_basics_spinner_click]
    onView(withId(R.id.spinner_simple)).perform(click())
    // [END android_testing_espresso_basics_spinner_click]
}

private fun selectSpinnerAmericano() {
    // [START android_testing_espresso_basics_spinner_select]
    onData(
        allOf(
            `is`(instanceOf(String::class.java)),
            `is`("Americano")
        )
    ).perform(click())
    // [END android_testing_espresso_basics_spinner_select]
}

private fun verifySpinnerText() {
    // [START android_testing_espresso_basics_spinner_verify]
    onView(withId(R.id.spinnertext_simple))
        .check(matches(withText(containsString("Americano"))))
    // [END android_testing_espresso_basics_spinner_verify]
}
