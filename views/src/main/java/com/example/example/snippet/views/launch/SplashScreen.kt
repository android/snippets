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

package com.example.example.snippet.views.launch

import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import android.window.SplashScreenView
import androidx.core.animation.doOnEnd
import com.example.example.snippet.views.R
import java.time.Duration
import java.time.Instant

class MyViewModel {
    val isReady: Boolean = true
}

class SuspendDrawingActivity : Activity() {
    private val viewModel = MyViewModel()

    // [START android_views_splash_screen_suspend_drawing]
    // Create a new event for the activity.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout for the content view.
        setContentView(R.layout.main_activity)

        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check whether the initial data is ready.
                    return if (viewModel.isReady) {
                        // The content is ready. Start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content isn't ready. Suspend.
                        false
                    }
                }
            }
        )
    }
    // [END android_views_splash_screen_suspend_drawing]
}

class ExitAnimationActivity : Activity() {
    // [START android_views_splash_screen_exit_animation]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START_EXCLUDE silent]
        setContentView(R.layout.main_activity)
        // [END_EXCLUDE]
        // ...

        // Add a callback that's called when the splash screen is animating to the
        // app content.
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // Create your custom animation.
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.height.toFloat()
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 200L

            // Call SplashScreenView.remove at the end of your custom animation.
            slideUp.doOnEnd { splashScreenView.remove() }

            // Run your animation.
            slideUp.start()
        }
    }
    // [END android_views_splash_screen_exit_animation]

    fun calculateRemainingDuration(splashScreenView: SplashScreenView): Long {
        // [START android_views_splash_screen_remaining_duration]
        // Get the duration of the animated vector drawable.
        val animationDuration = splashScreenView.iconAnimationDuration
        // Get the start time of the animation.
        val animationStart = splashScreenView.iconAnimationStart
        // Calculate the remaining duration of the animation.
        val remainingDuration = if (animationDuration != null && animationStart != null) {
            (animationDuration - Duration.between(animationStart, Instant.now()))
                .toMillis()
                .coerceAtLeast(0L)
        } else {
            0L
        }
        // [END android_views_splash_screen_remaining_duration]
        return remainingDuration
    }
}
