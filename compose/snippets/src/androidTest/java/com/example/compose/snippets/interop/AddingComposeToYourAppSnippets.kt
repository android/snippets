package com.example.compose.snippets.interop

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.example.compose.snippets.MyActivity
import com.example.compose.snippets.R
import org.junit.Rule
import org.junit.Test

// [START android_compose_interop_add_compose_test_mixed]
class MyActivityTest {
  @Rule
  @JvmField
  val composeTestRule = createAndroidComposeRule<MyActivity>()

  @Test
  fun testGreeting() {
    val greeting = InstrumentationRegistry.getInstrumentation()
      .targetContext.resources.getString(R.string.greeting)

    composeTestRule.onNodeWithText(greeting).assertIsDisplayed()
  }
}
// [END android_compose_interop_add_compose_test_mixed]
