package com.example.compose.snippets.predictiveback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Dialog
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventState

// 1. Activity Dispatcher Setup
private object ActivityDispatcherRight {
    @Composable
    private fun MyApplicationContent() {}

    // [START android_navigation_event_activity_dispatcher_right]
    // RIGHT
    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                MyApplicationContent()
            }
        }
    }
    // [END android_navigation_event_activity_dispatcher_right]
}

private object ActivityDispatcherWrong {
    @Composable
    private fun MyApplicationContent() {}

    // [START android_navigation_event_activity_dispatcher_wrong]
    // WRONG
    class MainActivity : ComponentActivity(), NavigationEventDispatcherOwner {
        override val navigationEventDispatcher = NavigationEventDispatcher() // Shadow loop crash
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                MyApplicationContent()
            }
        }
    }
    // [END android_navigation_event_activity_dispatcher_wrong]
}

// 2. Floating Window and Dialog Scoping
private object DialogScopingRight {
    // [START android_navigation_event_dialog_scoping_right]
    // RIGHT
    @Composable
    fun MyDialog(onDismiss: () -> Unit) {
        Dialog(onDismissRequest = onDismiss) {
            val navigationState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
            NavigationBackHandler(
                state = navigationState,
                onBackCompleted = onDismiss
            )
        }
    }
    // [END android_navigation_event_dialog_scoping_right]
}

private object DialogScopingWrong {
    // [START android_navigation_event_dialog_scoping_wrong]
    // WRONG
    @Composable
    fun MyDialog(onDismiss: () -> Unit) {
        val dispatcherOwner = LocalNavigationEventDispatcherOwner.current!!
        Dialog(onDismissRequest = onDismiss) {
            // Redundant: ComponentDialog provides NavigationEventDispatcherOwner automatically
            CompositionLocalProvider( LocalNavigationEventDispatcherOwner provides dispatcherOwner) {
                val navigationState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
                NavigationBackHandler(
                    state = navigationState,
                    onBackCompleted = onDismiss
                )
            }
        }
    }
    // [END android_navigation_event_dialog_scoping_wrong]

}

// 3. Parent-Child Dispatcher Hierarchy
private object ParentChildHierarchyRight {
    // [START android_navigation_event_parent_child_right]
    // RIGHT: Scoping child navigation in a ViewPager or Tab interface
    @Composable
    fun TabPage(isSelected: Boolean) {
        val childOwner = rememberNavigationEventDispatcherOwner(enabled = isSelected)
        CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides childOwner) {
            val navigationState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
            NavigationBackHandler(
                state = navigationState,
                onBackCompleted = { /* Handle page back navigation */ }
            )
            // Page content
        }
    }
    // [END android_navigation_event_parent_child_right]
}

private object ParentChildHierarchyWrong {
    // Extension helper to allow compilation of the WRONG illustrative example
    private fun NavigationEventDispatcher.addChild(child: NavigationEventDispatcher) {}

    // [START android_navigation_event_parent_child_wrong]
    // WRONG
    @Composable
    fun TabPage(isSelected: Boolean) {
        val parentDispatcher = LocalNavigationEventDispatcherOwner.current?.navigationEventDispatcher
        val childDispatcher = NavigationEventDispatcher() // Unlinked and not remembered across recompositions
        // WRONG: Method does not exist
        parentDispatcher?.addChild(childDispatcher)
    }
    // [END android_navigation_event_parent_child_wrong]
}

// 4. Compose Multi-Handler Registration
private object MultiHandlerRight {
    @Composable
    fun Example(
        hasUnsavedChanges: Boolean,
        showDiscardDialog: () -> Unit,
        onNavigateUp: () -> Unit
    ) {
        // [START android_navigation_event_multi_handler_right]
        // RIGHT
        val navigationState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
        NavigationBackHandler(
            state = navigationState,
            isBackEnabled = true,
            onBackCompleted = {
                if (hasUnsavedChanges) {
                    showDiscardDialog()
                } else {
                    onNavigateUp()
                }
            }
        )
        // [END android_navigation_event_multi_handler_right]
    }
}

private object MultiHandlerWrong {
    @Composable
    fun Example(hasUnsavedChanges: Boolean) {
        // [START android_navigation_event_multi_handler_wrong]
        // WRONG
        val navigationState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
        NavigationBackHandler(
            state = navigationState,
            isBackEnabled = hasUnsavedChanges,
            onBackCompleted = { /* Discard changes */ }
        )
        NavigationBackHandler(
            state = navigationState,
            isBackEnabled = !hasUnsavedChanges,
            onBackCompleted = { /* Navigate up */ }
        )
        // [END android_navigation_event_multi_handler_wrong]
    }
}