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

import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import javax.inject.Inject
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.robolectric.annotation.Config

private class FakeUnitAnalyticsService : AnalyticsService {
    override fun analyticsMethods() {}
}

private val fakeAnalyticsService: AnalyticsService = FakeUnitAnalyticsService()

private object HiltTestingUnitTestSnippet {
    // Note: AnalyticsAdapter would normally live in the main source set.
    // [START android_di_hilt_testing_unit_test]
    @ActivityScoped
    class AnalyticsAdapter @Inject constructor(
        private val service: AnalyticsService
    ) { /* ... */ }

    class AnalyticsAdapterTest {

        @Test
        fun `Happy path`() {
            // You don't need Hilt to create an instance of AnalyticsAdapter.
            // You can pass a fake or mock AnalyticsService.
            val adapter = AnalyticsAdapter(fakeAnalyticsService)
            // [START_EXCLUDE silent]
            /*
            // [END_EXCLUDE]
            assertEquals(...)
            // [START_EXCLUDE silent]
             */
            assertEquals(adapter, adapter)
            // [END_EXCLUDE]
        }
    }
    // [END android_di_hilt_testing_unit_test]
}

private object HiltTestingRobolectricConfigSnippet {
    // [START android_di_hilt_testing_robolectric_config]
    @HiltAndroidTest
    @Config(application = HiltTestApplication::class)
    class SettingsScreenTest {

        @get:Rule
        var hiltRule = HiltAndroidRule(this)

        // Robolectric tests here.
    }
    // [END android_di_hilt_testing_robolectric_config]
}
