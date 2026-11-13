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

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu
import androidx.test.espresso.Espresso.setFailureHandler
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.example.snippet.views.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher

private const val FOOTER = "FOOTER"

// In a real app, LongListActivity lives in src/main/java.
private class LongListActivity : ComponentActivity() {
    companion object {
        const val FOOTER = "FOOTER"
    }
}

private class MySpecialException(cause: Throwable) : RuntimeException(cause)

private open class BaseActivityTest {
    @Throws(Exception::class)
    open fun setUp() {}

    fun getActivity(): Activity = Activity()
}

private object EspressoRecipesSnippets {
    fun matchViewBesideAnother() {
        // [START android_testing_espresso_recipes_has_sibling]
        onView(allOf(withText("7"), hasSibling(withText("item: 0"))))
            .perform(click())
        // [END android_testing_espresso_recipes_has_sibling]
    }

    // [START android_testing_espresso_recipes_click_action_bar_item]
    fun testClickActionBarItem() {
        // We make sure the contextual action bar is hidden.
        onView(withId(R.id.hide_contextual_action_bar))
            .perform(click())

        // Click on the icon - we can find it by the r.Id.
        onView(withId(R.id.action_save))
            .perform(click())

        // Verify that we have really clicked on the icon
        // by checking the TextView content.
        onView(withId(R.id.text_action_bar_result))
            .check(matches(withText("Save")))
    }
    // [END android_testing_espresso_recipes_click_action_bar_item]

    // [START android_testing_espresso_recipes_click_action_mode_item]
    fun testClickActionModeItem() {
        // Make sure we show the contextual action bar.
        onView(withId(R.id.show_contextual_action_bar))
            .perform(click())

        // Click on the icon.
        onView((withId(R.id.action_lock)))
            .perform(click())

        // Verify that we have really clicked on the icon
        // by checking the TextView content.
        onView(withId(R.id.text_action_bar_result))
            .check(matches(withText("Lock")))
    }
    // [END android_testing_espresso_recipes_click_action_mode_item]

    // [START android_testing_espresso_recipes_action_bar_overflow]
    fun testActionBarOverflow() {
        // Make sure we hide the contextual action bar.
        onView(withId(R.id.hide_contextual_action_bar))
            .perform(click())

        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext<Context>()
        )

        // Click the item.
        onView(withText("World"))
            .perform(click())

        // Verify that we have really clicked on the icon by checking
        // the TextView content.
        onView(withId(R.id.text_action_bar_result))
            .check(matches(withText("World")))
    }
    // [END android_testing_espresso_recipes_action_bar_overflow]

    // [START android_testing_espresso_recipes_action_mode_overflow]
    fun testActionModeOverflow() {
        // Show the contextual action bar.
        onView(withId(R.id.show_contextual_action_bar))
            .perform(click())

        // Open the overflow menu from contextual action mode.
        openContextualActionModeOverflowMenu()

        // Click on the item.
        onView(withText("Key"))
            .perform(click())

        // Verify that we have really clicked on the icon by
        // checking the TextView content.
        onView(withId(R.id.text_action_bar_result))
            .check(matches(withText("Key")))
    }
    // [END android_testing_espresso_recipes_action_mode_overflow]

    fun assertViewNotDisplayed() {
        // [START android_testing_espresso_recipes_not_displayed]
        // import androidx.test.espresso.Espresso.onView
        // import androidx.test.espresso.assertion.ViewAssertions.matches
        // import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
        // import androidx.test.espresso.matcher.ViewMatchers.withId
        // import org.hamcrest.Matchers.not

        onView(withId(R.id.bottom_left))
            .check(matches(not(isDisplayed())))
        // [END android_testing_espresso_recipes_not_displayed]
    }

    fun assertViewDoesNotExist() {
        // [START android_testing_espresso_recipes_does_not_exist]
        // import androidx.test.espresso.Espresso.onView
        // import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
        // import androidx.test.espresso.matcher.ViewMatchers.withId

        onView(withId(R.id.bottom_left))
            .check(doesNotExist())
        // [END android_testing_espresso_recipes_does_not_exist]
    }

    private fun withItemContent(expectedText: String): Matcher<Any> = equalTo(expectedText)

    // [START android_testing_espresso_recipes_with_adapted_data]
    private fun withAdaptedData(dataMatcher: Matcher<Any>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun describeTo(description: Description) {
                description.appendText("with class name: ")
                dataMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                if (view !is AdapterView<*>) {
                    return false
                }

                val adapter = view.adapter
                for (i in 0 until adapter.count) {
                    if (dataMatcher.matches(adapter.getItem(i))) {
                        return true
                    }
                }

                return false
            }
        }
    }
    // [END android_testing_espresso_recipes_with_adapted_data]

    // [START android_testing_espresso_recipes_data_item_not_in_adapter]
    fun testDataItemNotInAdapter() {
        onView(withId(R.id.list))
            .check(matches(not(withAdaptedData(withItemContent("item: 168")))))
    }
    // [END android_testing_espresso_recipes_data_item_not_in_adapter]

    fun addFooterViewExample(
        layoutInflater: LayoutInflater,
        listView: ListView,
        data: List<String>
    ) {
        // [START android_testing_espresso_recipes_add_footer_view]
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        const val FOOTER = "FOOTER"
        // [START_EXCLUDE]
         */
        // [END_EXCLUDE]
        val footerView = layoutInflater.inflate(R.layout.list_item, listView, false)
        footerView.findViewById<TextView>(R.id.item_content).text = "count:"
        footerView.findViewById<TextView>(R.id.item_size).text =
            data.size.toString()
        listView.addFooterView(footerView, FOOTER, true)
        // [END android_testing_espresso_recipes_add_footer_view]
    }

    // [START android_testing_espresso_recipes_is_footer]
    // import org.hamcrest.Matchers.allOf
    // import org.hamcrest.Matchers.instanceOf
    // import org.hamcrest.Matchers.`is`

    fun isFooter(): Matcher<Any> {
        return allOf(
            `is`(instanceOf(String::class.java)),
            `is`(LongListActivity.FOOTER)
        )
    }
    // [END android_testing_espresso_recipes_is_footer]

    // [START android_testing_espresso_recipes_click_footer]
    // import androidx.test.espresso.Espresso.onData
    // import androidx.test.espresso.action.ViewActions.click
    // import androidx.test.espresso.sample.LongListMatchers.isFooter

    fun testClickFooter() {
        onData(isFooter())
            .perform(click())

        // ...
    }
    // [END android_testing_espresso_recipes_click_footer]
}

// [START android_testing_espresso_recipes_custom_failure_handler]
private class CustomFailureHandler(targetContext: Context) : FailureHandler {
    private val delegate: FailureHandler

    init {
        delegate = DefaultFailureHandler(targetContext)
    }

    override fun handle(error: Throwable, viewMatcher: Matcher<View>) {
        try {
            delegate.handle(error, viewMatcher)
        } catch (e: NoMatchingViewException) {
            throw MySpecialException(e)
        }
    }
}
// [END android_testing_espresso_recipes_custom_failure_handler]

private class CustomFailureHandlerTestExample : BaseActivityTest() {
    // [START android_testing_espresso_recipes_set_failure_handler]
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        getActivity()
        setFailureHandler(
            CustomFailureHandler(
                ApplicationProvider.getApplicationContext<Context>()
            )
        )
    }
    // [END android_testing_espresso_recipes_set_failure_handler]

    fun targetNonDefaultWindow() {
        // [START android_testing_espresso_recipes_in_root]
        onView(withText("South China Sea"))
            .inRoot(withDecorView(not(`is`(getActivity().getWindow().getDecorView()))))
            .perform(click())
        // [END android_testing_espresso_recipes_in_root]
    }
}
