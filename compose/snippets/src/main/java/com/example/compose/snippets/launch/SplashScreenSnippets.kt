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

package com.example.compose.snippets.launch

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()
}

@Composable
private fun MyApp() {
    Text("App content")
}

class KeepOnScreenActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    // [START android_compose_splash_screen_keep_on_screen]
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen before calling super.onCreate().
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep the splash screen on-screen until the initial data is ready.
        // The condition is checked each time the app is about to draw a frame.
        splashScreen.setKeepOnScreenCondition { !viewModel.isReady.value }

        setContent {
            MyApp()
        }
    }
    // [END android_compose_splash_screen_keep_on_screen]
}

class ExitAnimationActivity : ComponentActivity() {
    // [START android_compose_splash_screen_exit_animation]
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Add a callback that's called when the splash screen is animating to the
        // app content.
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val splashScreenView = splashScreenViewProvider.view
            // Create your custom animation.
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.height.toFloat()
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 200L

            // Call SplashScreenViewProvider.remove at the end of your custom
            // animation.
            slideUp.doOnEnd { splashScreenViewProvider.remove() }

            // Run your animation.
            slideUp.start()
        }

        setContent {
            MyApp()
        }
    }
    // [END android_compose_splash_screen_exit_animation]

    fun calculateRemainingDuration(splashScreenViewProvider: SplashScreenViewProvider): Long {
        // [START android_compose_splash_screen_remaining_duration]
        // Get the duration of the animated vector drawable.
        val animationDuration = splashScreenViewProvider.iconAnimationDurationMillis
        // Get the start time of the animation, in milliseconds since the epoch.
        val animationStart = splashScreenViewProvider.iconAnimationStartMillis
        // Calculate the remaining duration of the animation.
        val remainingDuration =
            (animationDuration - (System.currentTimeMillis() - animationStart))
                .coerceAtLeast(0L)
        // [END android_compose_splash_screen_remaining_duration]
        return remainingDuration
    }
}
