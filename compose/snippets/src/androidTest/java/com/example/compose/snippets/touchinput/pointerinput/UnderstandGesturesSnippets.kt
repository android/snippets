package com.example.compose.snippets.touchinput.pointerinput

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import com.example.compose.snippets.designsystems.MyTheme
import org.junit.Rule
import org.junit.Test

@Suppress("TestFunctionName")
class GesturesTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun MyTest() {
    // Start the app
    composeTestRule.setContent {
      MyTheme {
        Text("Hello world!")
      }
    }

    // [START android_compose_touchinput_pointerinput_test]
    composeTestRule.onNodeWithTag("MyList").performTouchInput {
      swipeUp()
      swipeDown()
      click()
    }
    // [END android_compose_touchinput_pointerinput_test]
  }
}

@Suppress("TestFunctionName")
@Composable
private fun MyTheme(
  content: @Composable () -> Unit
) {
  content()
}
