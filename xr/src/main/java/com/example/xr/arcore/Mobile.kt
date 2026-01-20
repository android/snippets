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

import androidx.xr.arcore.playservices.ArCoreRuntime
import androidx.xr.arcore.playservices.UnsupportedArCoreCompatApi
import androidx.xr.runtime.Session

@OptIn(UnsupportedArCoreCompatApi::class)
fun accessMobileRuntime(session: Session) {
    // [START androidxr_arcore_mobile_runtime]
    val arCoreRuntime = session.runtimes.firstNotNullOfOrNull { it as? ArCoreRuntime } ?: return
    val originalSession = arCoreRuntime.lifecycleManager.session()
    val originalFrame = arCoreRuntime.perceptionManager.lastFrame()
    // [END androidxr_arcore_mobile_runtime]
}
