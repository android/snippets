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

package com.example.compose.snippets.predictiveback

import android.os.Bundle
import androidx.activity.addCallback
import androidx.fragment.app.Fragment

private class CustomBackSnippets {

    // [START android_custom_back_add_callback]
    class MyFragment : Fragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // This callback will only be called when MyFragment is at least Started.
            val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
                // Handle the back button event
            }

            // The callback can be enabled or disabled here or in the lambda
        }
        // [START_EXCLUDE]
        /*
        // [END_EXCLUDE]
        ...
        // [START_EXCLUDE]
        */
        // [END_EXCLUDE]
    }
    // [END android_custom_back_add_callback]
}
