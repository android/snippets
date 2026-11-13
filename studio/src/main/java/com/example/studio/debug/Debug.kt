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

package com.example.studio.debug

import android.app.Activity
import android.os.Bundle

// [START android_studio_debug_log_state]
import android.util.Log
// ...
class MyActivity : Activity() {
    // ...
    override fun onCreate(savedInstanceState: Bundle?) {
        // [START_EXCLUDE silent]
        super.onCreate(savedInstanceState)
        // [END_EXCLUDE]
        // ...
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate() Restoring previous state")
            /* restore state */
        } else {
            Log.d(TAG, "onCreate() No saved state available")
            /* initialize app */
        }
        // ...
    }
    // ...
    companion object {
        private val TAG: String = MyActivity::class.java.simpleName
        // ...
    }
}
// [END android_studio_debug_log_state]

// [START android_studio_debug_log_exception]
fun someOtherMethod() {
    try {
        // ...
    } catch (e : SomeException) {
        Log.d(TAG, "someOtherMethod()", e)
    }
}
// [END android_studio_debug_log_exception]

private const val TAG = "TAG"

private class SomeException : Exception()
