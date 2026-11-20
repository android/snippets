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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.example.snippet.views.R
import org.junit.Test

private class EspressoIndexSnippets {
    // [START android_testing_espresso_greeter_says_hello]
    @Test
    fun greeterSaysHello() {
        onView(withId(R.id.name_field)).perform(typeText("Steve"))
        onView(withId(R.id.greet_button)).perform(click())
        onView(withText("Hello Steve!")).check(matches(isDisplayed()))
    }
    // [END android_testing_espresso_greeter_says_hello]
}
