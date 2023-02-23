package com.example.compose.snippets.semantics

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import org.junit.Rule
import org.junit.Test

@Suppress("TestFunctionName")
// [START android_compose_semantics_logging]
class MyComposeTest {

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
        // Log the full semantics tree
        composeTestRule.onRoot().printToLog("MY TAG")
    }
}
// [END android_compose_semantics_logging]

class Test2 {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSwitch() {
        // [START android_compose_semantics_test_switch]
        val mySwitch = SemanticsMatcher.expectValue(
            SemanticsProperties.Role, Role.Switch
        )
        composeTestRule.onNode(mySwitch)
            .performClick()
            .assertIsOff()
        // [END android_compose_semantics_test_switch]
    }
}

class Test3 {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLog() {
        // [START android_compose_semantics_print_log]
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("MY TAG")
        // [END android_compose_semantics_print_log]
        // [START android_compose_semantics_match_text]
        composeTestRule.onNodeWithText("Like").performClick()
        // [END android_compose_semantics_match_text]
    }
}

@Suppress("TestFunctionName")
@Composable
private fun MyTheme(
    content: @Composable () -> Unit
) {
    content()
}
