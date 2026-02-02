package com.example.compose.snippets.test

import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class RunComposeUiTest {

    @OptIn(ExperimentalTestApi::class)
// [START android_compose_test_runComposeUiTest]
    @Test
    fun testWithCoroutines() = runComposeUiTest {
        setContent {
            var status by remember { mutableStateOf("Loading...") }
            LaunchedEffect(Unit) {
                delay(1000)
                status = "Done!"
            }
            Text(text = status)
        }

        onNodeWithText("Loading...").assertIsDisplayed()
        mainClock.advanceTimeBy(1000 + 16 /* Frame buffer */)
        onNodeWithText("Done!").assertIsDisplayed()
    }
}
// [END android_compose_test_runComposeUiTest]

class Test2 {
// [START android_compose_test_v2_apis]
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testWithCoroutines() {
        composeTestRule.setContent {
            var status by remember { mutableStateOf("Loading...") }
            LaunchedEffect(Unit) {
                delay(1000)
                status = "Done!"
            }
            Text(text = status)
        }

        // NOT RECOMMENDED
        // Fails: runTest creates a new, separate scheduler.
        // Advancing time here does NOT advance the compose clock.
        // To fix this without migrating, you would need to share the scheduler
        // by passing 'composeTestRule.mainClock.scheduler' to runTest.
        runTest {
            composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
            advanceTimeBy(1000)
            composeTestRule.onNodeWithText("Done!").assertIsDisplayed()
        }
    }
// [END android_compose_test_v2_apis]
}