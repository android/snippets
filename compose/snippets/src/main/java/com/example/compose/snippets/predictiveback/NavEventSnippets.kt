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

@file:Suppress("unused", "UNUSED_VARIABLE")

package com.example.compose.snippets.predictiveback

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent.EDGE_LEFT
import android.view.MotionEvent.EDGE_RIGHT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.MainThread
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.NavigationEventHandler
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventInput
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationEventState
import androidx.navigationevent.compose.rememberNavigationEventState

private fun handlingBackEvents() {
    val navigationEventDispatcher = NavigationEventDispatcher()
    // [START android_compose_predictiveback_navevent_handler]
    val myHandler = object: NavigationEventHandler<NavigationEventInfo>(
        initialInfo = NavigationEventInfo.None,
        isBackEnabled = true
    ) {
        override fun onBackStarted(event: NavigationEvent) {
            // Prepare for the back event
        }

        override fun onBackProgressed(event: NavigationEvent) {
            // Use event.progress for predictive animations
        }

        // This is the required method for final event handling
        override fun onBackCompleted() {
            // Complete the back event
        }

        override fun onBackCancelled() {
            // Cancel the back event
        }
    }
    // [END android_compose_predictiveback_navevent_handler]

    // [START android_compose_predictiveback_navevent_handler_register]
    navigationEventDispatcher.addHandler(myHandler)
    // [END android_compose_predictiveback_navevent_handler_register]

    // [START android_compose_predictiveback_navevent_handler_remove]
    myHandler.remove()
    // [END android_compose_predictiveback_navevent_handler_remove]
}

// [START android_compose_predictiveback_navevent_NavigationEventHandler]
@Composable
public fun NavigationBackHandler(
    state: NavigationEventState<out NavigationEventInfo>,
    isBackEnabled: Boolean = true,
    onBackCancelled: () -> Unit = {},
    onBackCompleted: () -> Unit,
){

}
// [END android_compose_predictiveback_navevent_NavigationEventHandler]

// [START android_compose_predictiveback_navevent_transitionstate_with_backhandler]
@Composable
fun HandlingBackWithTransitionState(
    onNavigateUp: () -> Unit
) {
    val navigationState = rememberNavigationEventState(
        currentInfo = NavigationEventInfo.None
    )
    val transitionState = navigationState.transitionState
    // React to predictive back transition updates
    when (transitionState) {
        is NavigationEventTransitionState.InProgress -> {
            val progress = transitionState.latestEvent.progress
            // Use progress (0f..1f) to update UI during the gesture
        }
        is NavigationEventTransitionState.Idle -> {
            // Reset any temporary UI state if the gesture is cancelled
        }
    }
    NavigationBackHandler(
        state = navigationState,
        onBackCancelled = {
            // Called if the back gesture is cancelled
        },
        onBackCompleted = {
            // Called when the back gesture fully completes
            onNavigateUp()
        }
    )
}
// [END android_compose_predictiveback_navevent_transitionstate_with_backhandler]


// [START android_compose_predictiveback_navevent_animation]

object Routes {
    const val SCREEN_A = "Screen A"
    const val SCREEN_B = "Screen B"
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var state by remember { mutableStateOf(Routes.SCREEN_A) }
            val backEventState = rememberNavigationEventState<NavigationEventInfo>(currentInfo = NavigationEventInfo.None)
            when (state) {
                Routes.SCREEN_A -> {
                    ScreenA(onNavigate = { state = Routes.SCREEN_B })
                }
                else -> {
                    if (backEventState.transitionState is NavigationEventTransitionState.InProgress) {
                        ScreenA(onNavigate = { })
                    }
                    ScreenB(
                        backEventState = backEventState,
                        onBackCompleted = { state = Routes.SCREEN_A }
                    )
                }
            }
        }
    }
}
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ScreenB(
    backEventState: NavigationEventState<NavigationEventInfo>,
    onBackCompleted: () -> Unit = {},
) {
    val transitionState = backEventState.transitionState
    val latestEvent =
        (transitionState as? NavigationEventTransitionState.InProgress)
            ?.latestEvent
    val backProgress = latestEvent?.progress ?: 0f
    val swipeEdge = latestEvent?.swipeEdge ?: NavigationEvent.EDGE_LEFT
    if (transitionState is NavigationEventTransitionState.InProgress) {
        Log.d("BackGesture", "Progress: ${transitionState.latestEvent.progress}")
    } else if (transitionState is NavigationEventTransitionState.Idle) {
        Log.d("BackGesture", "Idle")
    }
    val animatedScale by animateFloatAsState(
        targetValue = 1f - (backProgress * 0.1f),
        label = "ScaleAnimation"
    )
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val maxShift = remember(windowInfo, density) {
        val widthDp = with(density) { windowInfo.containerSize.width.toDp() }
        (widthDp.value / 20f) - 8
    }
    val offsetX = when (swipeEdge) {
        EDGE_LEFT -> (backProgress * maxShift).dp
        EDGE_RIGHT -> (-backProgress * maxShift).dp
        else -> 0.dp
    }
    NavigationBackHandler(
        state = backEventState,
        onBackCompleted = onBackCompleted,
        isBackEnabled = true
    )
    Box(
        modifier = Modifier
            .offset(x = offsetX)
            .scale(animatedScale)
    ){
        // Rest of UI
    }
}
// [END android_compose_predictiveback_navevent_animation]

@Composable
fun ScreenA(onNavigate: () -> Unit) {
    // Basic ScreenA implementation for snippet
}

// [START android_compose_predictiveback_navevent_NavigationEvent_dispatcher_owner]
class MyComponent: NavigationEventDispatcherOwner {
    override val navigationEventDispatcher: NavigationEventDispatcher =
        NavigationEventDispatcher()
}
// [END android_compose_predictiveback_navevent_NavigationEvent_dispatcher_owner]

// [START android_compose_predictiveback_navevent_activity_own_dispatcher]
class MyCustomActivity : ComponentActivity() {
    fun addMyHandler() {
        // navigationEventDispatcher provided from the ComponentActivity
        navigationEventDispatcher.addHandler(TODO())
    }
}
// [END android_compose_predictiveback_navevent_activity_own_dispatcher]

// [START android_compose_predictiveback_navevent_navigation_event_input]
public class MyInput : NavigationEventInput() {
    @MainThread
    public fun backStarted(event: NavigationEvent) {
        dispatchOnBackStarted(event)
    }

    @MainThread
    public fun backProgressed(event: NavigationEvent) {
        dispatchOnBackProgressed(event)
    }

    @MainThread
    public fun backCancelled() {
        dispatchOnBackCancelled()
    }

    @MainThread
    public fun backCompleted() {
        dispatchOnBackCompleted()
    }
}
// [END android_compose_predictiveback_navevent_navigation_event_input]

private fun provideInputToDispatcher() {
    val navigationEventDispatcher = NavigationEventDispatcher()
    // [START android_compose_predictiveback_navevent_add_input]
    navigationEventDispatcher.addInput(MyInput())
    // [END android_compose_predictiveback_navevent_add_input]
}

private fun disposeDispatcher() {
    val navigationEventDispatcher = NavigationEventDispatcher()
    // [START android_compose_predictiveback_navevent_dispose]
    navigationEventDispatcher.dispose()
    // [END android_compose_predictiveback_navevent_dispose]
}