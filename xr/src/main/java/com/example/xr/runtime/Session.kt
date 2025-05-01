/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.xr.runtime

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.xr.compose.platform.LocalSession
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionCreatePermissionsNotGranted
import androidx.xr.runtime.SessionCreateSuccess
import androidx.xr.runtime.SessionResumePermissionsNotGranted
import androidx.xr.runtime.SessionResumeSuccess

// [START androidxr_localsession]
@Composable
fun ComposableUsingSession() {
    val session = LocalSession.current
}
// [END androidxr_localsession]

fun Activity.createSession() {
    // [START androidxr_session_create]
    when (val result = Session.create(this)) {
        is SessionCreateSuccess -> {
            val xrSession = result.session
            // ...
        }
        is SessionCreatePermissionsNotGranted ->
            TODO(/* The required permissions in result.permissions have not been granted. */)
    }
    // [END androidxr_session_create]
}

fun sessionResume(session: Session) {
    // [START androidxr_session_resume]
    when (val result = session.resume()) {
        is SessionResumeSuccess -> {
            // Session has been created successfully.
            // Attach any successful handlers here.
        }

        is SessionResumePermissionsNotGranted -> {
            // Request permissions in `result.permissions`.
        }
    }
    // [END androidxr_session_resume]
}