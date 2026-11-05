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
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.truth.content.IntentSubject.assertThat
import com.example.example.snippet.views.R
import org.junit.Rule
import org.junit.Test

// In a real app, MyActivity lives in src/main/java.
class MyActivity : ComponentActivity()

@Suppress("DEPRECATION")
private class EspressoIntentsRuleExample {
    // [START android_testing_espresso_intents_rule]
    @get:Rule
    val intentsTestRule = IntentsTestRule(MyActivity::class.java)
    // [END android_testing_espresso_intents_rule]
}

private fun validateIntentWithTruth(intent: Intent) {
    // [START android_testing_espresso_intents_assert_that]
    assertThat(intent).hasAction(Intent.ACTION_VIEW)
    assertThat(intent).categories().containsExactly(Intent.CATEGORY_BROWSABLE)
    assertThat(intent).hasData(Uri.parse("www.google.com"))
    assertThat(intent).extras().containsKey("key1")
    assertThat(intent).extras().string("key1").isEqualTo("value1")
    assertThat(intent).extras().containsKey("key2")
    assertThat(intent).extras().string("key2").isEqualTo("value2")
    // [END android_testing_espresso_intents_assert_that]
}

private class EspressoIntentsValidationExample {
    private object user {
        fun clickOnView(view: View) {}
    }

    private object system {
        fun getView(id: Int): View = View(ApplicationProvider.getApplicationContext())
    }

    // [START android_testing_espresso_intents_validate_package]
    @Test fun validateIntentSentToPackage() {
        // User action that results in an external "phone" activity being launched.
        user.clickOnView(system.getView(R.id.callButton))

        // Using a canned RecordedIntentMatcher to validate that an intent resolving
        // to the "phone" activity has been sent.
        intended(toPackage("com.android.phone"))
    }
    // [END android_testing_espresso_intents_validate_package]
}

private fun stubbingWalkthrough() {
    // [START android_testing_espresso_intents_build_result]
    val resultData = Intent()
    val phoneNumber = "123-345-6789"
    resultData.putExtra("phone", phoneNumber)
    val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
    // [END android_testing_espresso_intents_build_result]

    // [START android_testing_espresso_intents_intending]
    intending(toPackage("com.android.contacts")).respondWith(result)
    // [END android_testing_espresso_intents_intending]

    // [START android_testing_espresso_intents_verify_result]
    onView(withId(R.id.pickButton)).perform(click())
    onView(withId(R.id.phoneNumber)).check(matches(withText(phoneNumber)))
    // [END android_testing_espresso_intents_verify_result]
}

private class EspressoIntentsStubbingTestExample {
    // [START android_testing_espresso_intents_activity_result_test]
    @Test fun activityResult_DisplaysContactsPhoneNumber() {
        // Build the result to return when the activity is launched.
        val resultData = Intent()
        val phoneNumber = "123-345-6789"
        resultData.putExtra("phone", phoneNumber)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)

        // Set up result stubbing when an intent sent to "contacts" is seen.
        intending(toPackage("com.android.contacts")).respondWith(result)

        // User action that results in "contacts" activity being launched.
        // Launching activity expects phoneNumber to be returned and displayed.
        onView(withId(R.id.pickButton)).perform(click())

        // Assert that the data we set up above is shown.
        onView(withId(R.id.phoneNumber)).check(matches(withText(phoneNumber)))
    }
    // [END android_testing_espresso_intents_activity_result_test]
}
