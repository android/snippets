@file:Suppress("unused", "UNUSED_VARIABLE")

package com.example.compose.snippets.predictiveback

import androidx.annotation.MainThread
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventHandler
import androidx.navigationevent.NavigationEventInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.NavigationEventInput
import androidx.navigationevent.compose.NavigationEventState

@Composable
private fun HandlingBackEvents() {
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

// [START android_compose_predictiveback_navevent_register_handler]
@Composable
fun RegisterHandler(
    navigationEventDispatcher: NavigationEventDispatcher,
    myHandler: NavigationEventHandler<*>
) {
    DisposableEffect(navigationEventDispatcher, myHandler) {
        navigationEventDispatcher.addHandler(myHandler)
        onDispose {
            myHandler.remove()
        }
    }
}
// [END android_compose_predictiveback_navevent_register_handler]



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

    public fun backCancelled() {
        dispatchOnBackCancelled()
    }

    @MainThread
    public fun backCompleted() {
        dispatchOnBackCompleted()
    }
}
// [END android_compose_predictiveback_navevent_navigation_event_input]

// TODO: We then need to provide that input to our dispatcher:
// [START android_compose_predictiveback_navevent_register_input]
fun setupDispatcher() {
    val myComponent = MyComponent()
    val myInput = MyInput()

    // Register the custom input with the dispatcher
    myComponent.navigationEventDispatcher.addInput(myInput)
}
// [END android_compose_predictiveback_navevent_register_input]

// [START android_compose_predictiveback_navevent_dispose]
fun cleanupDispatcher(myComponent: MyComponent) {
    // Explicitly remove the dispatcher from the hierarchy when the component is destroyed
    myComponent.navigationEventDispatcher.dispose()
}
// [END android_compose_predictiveback_navevent_dispose]


