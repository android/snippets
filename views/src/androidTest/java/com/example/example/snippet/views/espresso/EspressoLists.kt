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
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.example.example.snippet.views.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test

private const val ITEM_BELOW_THE_FOLD = 40

private fun clickRowWithOnData() {
    // [START android_testing_espresso_lists_on_data_map]
    onData(
        allOf(
            `is`(instanceOf(Map::class.java)),
            hasEntry(equalTo("STR"), `is`("item: 50"))
        )
    ).perform(click())
    // [END android_testing_espresso_lists_on_data_map]
}

private fun narrowSearchWithHasEntry() {
    // [START android_testing_espresso_lists_has_entry]
    hasEntry(equalTo("STR"), `is`("item: 50"))
    // [END android_testing_espresso_lists_has_entry]
}

private fun withItemContent(itemTextMatcher: Matcher<String>): Matcher<Any> {
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

private fun clickWithCustomMatcher() {
    // [START android_testing_espresso_lists_click_with_item_content]
    onData(withItemContent("item: 50")).perform(click())
    // [END android_testing_espresso_lists_click_with_item_content]
}

private fun clickChildViewInRow() {
    // [START android_testing_espresso_lists_on_child_view]
    onData(withItemContent("item: 60"))
        .onChildView(withId(R.id.item_size))
        .perform(click())
    // [END android_testing_espresso_lists_on_child_view]
}

@Suppress("DEPRECATION")
private class EspressoRecyclerViewTestSnippets {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    private fun isInTheMiddle(): Matcher<RecyclerView.ViewHolder> {
        return object : TypeSafeMatcher<RecyclerView.ViewHolder>() {
            override fun matchesSafely(item: RecyclerView.ViewHolder): Boolean = true
            override fun describeTo(description: Description) {
                description.appendText("item in the middle")
            }
        }
    }

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

    // [START android_testing_espresso_lists_scroll_below_fold]
    @Test fun scrollToItemBelowFold_checkItsText() {
        // First, scroll to the position that needs to be matched and click on it.
        onView(ViewMatchers.withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    ITEM_BELOW_THE_FOLD,
                    click()
                )
            )

        // Match the text in an item below the fold and check that it's displayed.
        val itemElementText = "${activityRule.activity.resources
            .getString(R.string.item_element_text)} ${ITEM_BELOW_THE_FOLD}"
        onView(withText(itemElementText)).check(matches(isDisplayed()))
    }
    // [END android_testing_espresso_lists_scroll_below_fold]

    // [START android_testing_espresso_lists_item_in_middle]
    @Test fun itemInMiddleOfList_hasSpecialText() {
        // First, scroll to the view holder using the isInTheMiddle() matcher.
        onView(ViewMatchers.withId(R.id.recyclerView))
            .perform(RecyclerViewActions.scrollToHolder(isInTheMiddle()))

        // Check that the item has the special text.
        val middleElementText = activityRule.activity.resources
            .getString(R.string.middle)
        onView(withText(middleElementText)).check(matches(isDisplayed()))
    }
    // [END android_testing_espresso_lists_item_in_middle]
}
