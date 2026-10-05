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

package com.example.compose.snippets.architecture

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.compose.rememberLifecycleOwner

private object LifecycleComposeSnippet1 {
    @Composable
    fun CurrentStateFlowExample() {
        // [START android_architecture_lifecycle_current_state_flow]
        val lifecycleOwner = LocalLifecycleOwner.current
        val stateFlow = lifecycleOwner.lifecycle.currentStateFlow
        // ...
        val currentLifecycleState by stateFlow.collectAsState()
        // [END android_architecture_lifecycle_current_state_flow]
    }
}

private object LifecycleComposeSnippet2 {
    @Composable
    fun CurrentStateAsStateExample() {
        // [START android_architecture_lifecycle_current_state_as_state]
        val lifecycleOwner = LocalLifecycleOwner.current
        val currentLifecycleState = lifecycleOwner.lifecycle.currentStateAsState()
        // [END android_architecture_lifecycle_current_state_as_state]
    }
}

private object LifecycleComposeSnippet3 {
    object Analytics {
        fun logView(screenName: String) {}
    }

    // [START android_architecture_lifecycle_event_effect]
    @Composable
    fun AnalyticsTracker(screenName: String) {
        // Log an event when the app receives ON_RESUME (e.g. comes to foreground)
        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            Analytics.logView(screenName)
        }
    }
    // [END android_architecture_lifecycle_event_effect]
}

private object LifecycleComposeSnippet4 {
    fun interface LocationListener {
        fun onLocationChanged(location: Any)
    }

    class LocationManager {
        fun requestLocationUpdates(listener: LocationListener) {}
        fun removeUpdates(listener: LocationListener) {}
    }

    // [START android_architecture_lifecycle_start_effect]
    @Composable
    fun LocationMonitor(locationManager: LocationManager) {
        // Starts monitoring when ON_START is dispatched
        // Stops monitoring when ON_STOP is dispatched
        //   (or the composable leaves the screen)
        LifecycleStartEffect(locationManager) {
            val listener = LocationListener { location ->
                /* update UI */
            }
            locationManager.requestLocationUpdates(listener)
            // The cleanup block automatically runs on ON_STOP or on disposal
            onStopOrDispose {
                locationManager.removeUpdates(listener)
            }
        }
    }
    // [END android_architecture_lifecycle_start_effect]
}

private object LifecycleComposeSnippet5 {
    class CameraController {
        fun startPreview() {}
        fun stopPreview() {}
    }

    // [START android_architecture_lifecycle_resume_effect]
    @Composable
    fun CameraPreview(cameraController: CameraController) {
        LifecycleResumeEffect(cameraController) {
            cameraController.startPreview()

            onPauseOrDispose {
                cameraController.stopPreview()
            }
        }
    }
    // [END android_architecture_lifecycle_resume_effect]
}

private object LifecycleComposeSnippet6 {
    @Composable
    fun LocalOwnerExample() {
        // [START android_architecture_lifecycle_local_owner]
        val lifecycleOwner = LocalLifecycleOwner.current
        // [END android_architecture_lifecycle_local_owner]
    }
}

private object LifecycleComposeSnippet7 {
    @Composable
    fun PagerRememberOwnerExample() {
        // [START android_architecture_lifecycle_pager_remember_owner]
        val pagerState = rememberPagerState(pageCount = { 10 })

        HorizontalPager(state = pagerState) { pageNum ->
            val pageLifecycleOwner = rememberLifecycleOwner(
                maxLifecycle = if (pagerState.settledPage == pageNum) {
                    Lifecycle.State.RESUMED
                } else {
                    Lifecycle.State.STARTED
                }
            )

            CompositionLocalProvider(LocalLifecycleOwner provides pageLifecycleOwner) {
                // Your pages here. Their lifecycle-aware components respect the
                // custom maxState defined above.
            }
        }
        // [END android_architecture_lifecycle_pager_remember_owner]
    }
}
