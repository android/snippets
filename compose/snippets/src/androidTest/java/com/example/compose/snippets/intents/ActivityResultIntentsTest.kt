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

package com.example.compose.snippets.intents

import android.graphics.Bitmap
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.testing.launchFragmentInContainer
import com.google.common.truth.Truth.assertThat
import org.junit.Test

// MyFragmentTest is an instrumented test class that normally lives in the androidTest source set.
class MyFragmentTest {
    // [START android_intents_result_test_complete]
    @Test
    fun activityResultTest() {
        // Create an expected result Bitmap.
        val expectedResult = Bitmap.createBitmap(1, 1, Bitmap.Config.RGBA_F16)

        // Create the test ActivityResultRegistry.
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, expectedResult)
            }
        }

        // Use the launchFragmentInContainer method that takes a
        // lambda to construct the Fragment with the testRegistry.
        with(launchFragmentInContainer { MyFragment(testRegistry) }) {
            onFragment { fragment ->
                // Trigger the ActivityResultLauncher.
                fragment.takePicture.launch()
                // Verify the result is set.
                assertThat(fragment.thumbnailLiveData.value)
                    .isSameInstanceAs(expectedResult)
            }
        }
    }
    // [END android_intents_result_test_complete]
}
