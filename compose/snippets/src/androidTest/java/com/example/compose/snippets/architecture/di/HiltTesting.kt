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

package com.example.compose.snippets.architecture.di

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.runner.AndroidJUnitRunner
import com.example.compose.snippets.architecture.di.HiltTestingAnalyticsModuleSnippet.AnalyticsModule
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.mockito.Mock

private class HiltTestActivity : ComponentActivity()

private open class BaseApplication : Application()

private class FakeAnalyticsService @Inject constructor() : AnalyticsService {
    override fun analyticsMethods() {}
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
private annotation class ExampleQualifier

private class ExampleCustomType

@Composable
private fun SettingsScreen() {
    Text("Settings")
}

private class SomeOtherRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement = base
}

private class SettingsScreenTestRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement = base
}

private object HiltTestingUiTestSetupSnippet {
    // [START android_di_hilt_testing_ui_test_setup]
    @HiltAndroidTest
    class SettingsScreenTest {

        @get:Rule(order = 0)
        val hiltRule = HiltAndroidRule(this)

        @get:Rule(order = 1)
        val composeRule = createAndroidComposeRule<HiltTestActivity>()

        // Compose UI tests here.
    }
    // [END android_di_hilt_testing_ui_test_setup]
}

private object HiltTestingCustomTestRunnerSnippet {
    // [START android_di_hilt_testing_custom_test_runner]
    // A custom runner to set up the instrumented application class for tests.
    class CustomTestRunner : AndroidJUnitRunner() {

        override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
            return super.newApplication(cl, HiltTestApplication::class.java.name, context)
        }
    }
    // [END android_di_hilt_testing_custom_test_runner]
}

private object HiltTestingInjectInTestsSnippet {
    // [START android_di_hilt_testing_inject_in_tests]
    @HiltAndroidTest
    class SettingsScreenTest {

        @get:Rule(order = 0)
        val hiltRule = HiltAndroidRule(this)

        @get:Rule(order = 1)
        val composeRule = createAndroidComposeRule<HiltTestActivity>()

        @Inject
        lateinit var analyticsAdapter: AnalyticsAdapter

        @Before
        fun init() {
            hiltRule.inject()
        }

        @Test
        fun settingsScreen_showsTitle() {
            composeRule.setContent {
                SettingsScreen()
            }
            composeRule.onNodeWithText("Settings").assertIsDisplayed()
            // analyticsRepository is available here.
        }
    }
    // [END android_di_hilt_testing_inject_in_tests]
}

private object HiltTestingAnalyticsModuleSnippet {
    // Note: AnalyticsModule would normally live in the main source set.
    // [START android_di_hilt_testing_analytics_module]
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class AnalyticsModule {

        @Singleton
        @Binds
        abstract fun bindAnalyticsService(
            analyticsServiceImpl: AnalyticsServiceImpl
        ): AnalyticsService
    }
    // [END android_di_hilt_testing_analytics_module]

    // [START android_di_hilt_testing_test_install_in]
    @Module
    @TestInstallIn(
        components = [SingletonComponent::class],
        replaces = [AnalyticsModule::class]
    )
    abstract class FakeAnalyticsModule {

        @Singleton
        @Binds
        abstract fun bindAnalyticsService(
            fakeAnalyticsService: FakeAnalyticsService
        ): AnalyticsService
    }
    // [END android_di_hilt_testing_test_install_in]
}

private object HiltTestingUninstallModulesSnippet {
    // [START android_di_hilt_testing_uninstall_modules]
    @UninstallModules(AnalyticsModule::class)
    @HiltAndroidTest
    class SettingsScreenTest { /* ... */ }
    // [END android_di_hilt_testing_uninstall_modules]
}

private object HiltTestingTestModuleSnippet {
    // [START android_di_hilt_testing_test_module]
    @UninstallModules(AnalyticsModule::class)
    @HiltAndroidTest
    class SettingsScreenTest {

        @Module
        @InstallIn(SingletonComponent::class)
        abstract class TestModule {

            @Singleton
            @Binds
            abstract fun bindAnalyticsService(
                fakeAnalyticsService: FakeAnalyticsService
            ): AnalyticsService
        }

        // ...
    }
    // [END android_di_hilt_testing_test_module]
}

private object HiltTestingBindValueSnippet {
    // [START android_di_hilt_testing_bind_value]
    @UninstallModules(AnalyticsModule::class)
    @HiltAndroidTest
    class SettingsScreenTest {

        @BindValue @JvmField
        val analyticsService: AnalyticsService = FakeAnalyticsService()

        // ...
    }
    // [END android_di_hilt_testing_bind_value]
}

private object HiltTestingBindValueMockSnippet {
    // [START android_di_hilt_testing_bind_value_mock]
    // ...
    class SettingsScreenTest {
        // ...

        @BindValue @ExampleQualifier @Mock
        lateinit var qualifiedVariable: ExampleCustomType

        // Robolectric tests here.
    }
    // [END android_di_hilt_testing_bind_value_mock]
}

private object HiltTestingCustomTestApplicationSnippet {
    // [START android_di_hilt_testing_custom_test_application]
    @CustomTestApplication(BaseApplication::class)
    interface HiltTestApplication
    // [END android_di_hilt_testing_custom_test_application]
}

private object HiltTestingMultipleTestRulesSnippet {
    // [START android_di_hilt_testing_multiple_test_rules]
    @HiltAndroidTest
    class SettingsScreenTest {

        @get:Rule(order = 0)
        var hiltRule = HiltAndroidRule(this)

        @get:Rule(order = 1)
        val composeRule = createAndroidComposeRule<HiltTestActivity>()

        @get:Rule(order = 2)
        val otherRule = SomeOtherRule()

        // UI tests here.
    }
    // [END android_di_hilt_testing_multiple_test_rules]
}

private object HiltTestingRuleChainSnippet {
    // [START android_di_hilt_testing_rule_chain]
    @HiltAndroidTest
    class SettingsScreenTest {

        @get:Rule
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        var rule = RuleChain.outerRule(HiltAndroidRule(this)).
            around(SettingsScreenTestRule(...))
        // [START_EXCLUDE silent]
        */
        var rule = RuleChain.outerRule(HiltAndroidRule(this)).around(SettingsScreenTestRule())
        // [END_EXCLUDE]

        // UI tests here.
    }
    // [END android_di_hilt_testing_rule_chain]
}
