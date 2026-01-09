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

package com.example.xr.misc

import androidx.compose.runtime.Composable
import androidx.xr.compose.platform.LocalSession
import androidx.xr.runtime.Session
import androidx.xr.scenecore.scene

@Composable
fun modeTransitionCompose() {
    // [START androidxr_misc_modeTransitionCompose]
    val session = LocalSession.current ?: return
    session.scene.requestHomeSpaceMode()
    // or
    session.scene.requestFullSpaceMode()
    // [END androidxr_misc_modeTransitionCompose]
}

fun modeTransitionScenecore(xrSession: Session) {
    // [START androidxr_misc_modeTransitionScenecore]
    xrSession.scene.requestHomeSpaceMode()
    // [END androidxr_misc_modeTransitionScenecore]
}
