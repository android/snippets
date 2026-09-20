package com.example.compose.snippets.test

import androidx.compose.ui.test.ComposeUiTestConfig
import androidx.compose.ui.test.FailureArtifact
import androidx.compose.ui.test.TestFailureHandler
import androidx.compose.ui.test.TestFailurePolicy
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.v2.runComposeUiTest
import org.junit.Rule
import org.junit.Test

// [START android_compose_test_config_with_test_policy]
class MyComposeTest {
    private val customConfig = ComposeUiTestConfig(
        failurePolicy = TestFailurePolicy(
            screenshotCaptureMode = TestFailurePolicy.CaptureMode.Enabled,
            uiHierarchyCaptureMode = TestFailurePolicy.CaptureMode.Enabled
        )
    )

    @Test
    fun myFirstTest() = runComposeUiTest(config = customConfig) {
        // ...
    }
}
// [END android_compose_test_config_with_test_policy]

// [START android_compose_test_config_with_handler]
class MyComposeTestWithHandler {
    private val customConfig = ComposeUiTestConfig(
        failurePolicy = TestFailurePolicy(
            screenshotCaptureMode = TestFailurePolicy.CaptureMode.Enabled,
            failureHandlers = listOf(
                TestFailureHandler { context ->
                    val screenshot = context.artifacts.firstOrNull {
                        it.type == FailureArtifact.Type.Screenshot
                    }
                    // ...
                }
            )
        )
    )

    @get:Rule
    val rule = createComposeRule(config = customConfig)

    // ...
}
// [END android_compose_test_config_with_handler]

