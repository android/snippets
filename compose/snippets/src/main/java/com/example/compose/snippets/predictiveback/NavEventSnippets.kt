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

import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
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

// [START android_compose_predictiveback_navevent_NavigationEvent_dispatcher_owner]
class MyComponent: NavigationEventDispatcherOwner {
    override val navigationEventDispatcher: NavigationEventDispatcher =
        NavigationEventDispatcher()
}
// [END android_compose_predictiveback_navevent_NavigationEvent_dispatcher_owner]


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
