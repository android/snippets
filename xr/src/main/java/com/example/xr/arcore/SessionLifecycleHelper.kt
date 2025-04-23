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

package com.example.xr.arcore

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.xr.runtime.Session

/**
 * This is a dummy version of [SessionLifecycleHelper](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:xr/arcore/integration-tests/whitebox/src/main/kotlin/androidx/xr/arcore/apps/whitebox/common/SessionLifecycleHelper.kt).
 * This will be removed when Session becomes a LifecycleOwner in cl/726643897.
 */
class SessionLifecycleHelper(
    val onCreateCallback: (Session) -> Unit,

    val onResumeCallback: (() -> Unit)? = null,
) : DefaultLifecycleObserver
