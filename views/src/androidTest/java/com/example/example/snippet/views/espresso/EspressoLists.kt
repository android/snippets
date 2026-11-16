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

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.example.snippet.views.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.junit.Test

private object EspressoListsAdapterSnippets {
    fun clickRowWithOnData() {
        // [START android_testing_espresso_lists_on_data_map]
        onData(
            allOf(
                `is`(instanceOf(Map::class.java)),
                hasEntry(equalTo("STR"), `is`("item: 50"))
            )
        ).perform(click())
        // [END android_testing_espresso_lists_on_data_map]
    }

    fun narrowSearchWithHasEntry() {
        // [START android_testing_espresso_lists_has_entry]
        hasEntry(equalTo("STR"), `is`("item: 50"))
        // [END android_testing_espresso_lists_has_entry]
    }

    fun withItemContent(itemTextMatcher: Matcher<String>): Matcher<Any> {
        // [START android_testing_espresso_lists_bounded_matcher]
        return object : BoundedMatcher<Any, Map<*, *>>(Map::class.java) {
            override fun matchesSafely(map: Map<*, *>): Boolean {
                return hasEntry(equalTo("STR"), itemTextMatcher).matches(map)
            }

            override fun describeTo(description: Description) {
                description.appendText("with item content: ")
                itemTextMatcher.describeTo(description)
            }
        }
        // [END android_testing_espresso_lists_bounded_matcher]
    }

    // [START android_testing_espresso_lists_with_item_content]
    fun withItemContent(expectedText: String): Matcher<Any> {
        checkNotNull(expectedText)
        return withItemContent(equalTo(expectedText))
    }
    // [END android_testing_espresso_lists_with_item_content]

    fun clickWithCustomMatcher() {
        // [START android_testing_espresso_lists_click_with_item_content]
        onData(withItemContent("item: 50")).perform(click())
        // [END android_testing_espresso_lists_click_with_item_content]
    }

    fun clickChildViewInRow() {
        // [START android_testing_espresso_lists_on_child_view]
        onData(withItemContent("item: 60"))
            .onChildView(withId(R.id.item_size))
            .perform(click())
        // [END android_testing_espresso_lists_on_child_view]
    }
}

private class EspressoRecyclerViewTestSnippets {
    // [START android_testing_espresso_lists_item_does_not_exist]
    @Test(expected = PerformException::class)
    fun itemWithText_doesNotExist() {
        // Attempt to scroll to an item that contains the special text.
        onView(ViewMatchers.withId(R.id.recyclerView))
            .perform(
                // scrollTo will fail the test if no item matches.
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText("not in the list"))
                )
            )
    }
    // [END android_testing_espresso_lists_item_does_not_exist]
}
