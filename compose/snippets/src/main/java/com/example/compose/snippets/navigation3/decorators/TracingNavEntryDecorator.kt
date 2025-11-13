package com.example.compose.snippets.navigation3.decorators

import android.os.SystemClock
import android.os.Trace
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable


/**
 * Decorator that automatically applies performance tracking and lifecycle tracing to every
 * [NavEntry] it wraps.
 *
 * This is a powerful mechanism for managing cross-cutting concerns like logging and tracing
 * without cluttering individual screen composables.
 *
 * Features:
 * 1. **System Tracing**: Uses [Trace.beginSection] and [Trace.endSection] within a [DisposableEffect]
 * to mark the exact lifespan of the screen in composition. This is viewable in the
 * Android Studio Profiler (System Trace).
 * 2. **Render Timing**: Uses a [SideEffect] to calculate the duration from the start of the
 * composition until the first successful layout, providing an estimate for Time-to-First-Frame (TTFF).
 * 3. **Jank State Reporting**: Provides a callback to an external system to signal when the
 * current screen becomes active or inactive, which is essential for contextualizing Jank data.
 *
 * @param logTag The tag used for logging performance metrics (TTFF estimate).
 * @param onReportJankState Callback to report screen visibility for external performance tooling.
 */

// [START android_compose_navigation3_custom_decorator_1]
class TracingNavEntryDecorator<T: Any>(
    private val logTag: String,
    private val onReportJankState: (String, Boolean) -> Unit
): NavEntryDecorator<T>(
    onPop = { key->
        // Your onPop logic goes in here
        // [START_EXCLUDE]
        Log.v(logTag, "${key} popped. Cleaning up performance markers.")
        // [END_EXCLUDE]
    },
    decorate = {entry ->
        // Your decorate logic goes in here
        val name = entry.contentKey.toString()
        // [START_EXCLUDE]

        DisposableEffect(entry.contentKey) {
            try {
                Trace.beginSection(name)
            } catch (e: Exception) {
                Log.e(logTag, "Ran into an $e exception")
            }

            onReportJankState(name,true)

            onDispose {
                Trace.endSection()
                onReportJankState(name, false)
            }
        }

        val startTime = remember(entry.contentKey) { SystemClock.uptimeMillis() }

        SideEffect {
            val duration = SystemClock.uptimeMillis() - startTime

            if (duration > 0) {
                Log.d(logTag, "$name Composition to Layout took $duration ms" )
            }
        }
        // [END_EXCLUDE]

        entry.Content()
    }
)

// [START_EXCLUDE]
/**
 * Creates and remembers a [TracingNavEntryDecorator] instance.
 *
 * This function handles creating the decorator and hoisting the [onReportJankState] lambda
 * using [rememberUpdatedState] to ensure the latest callback is always used,
 * preventing unnecessary recreation of the decorator class itself.
 *
 * @param logTag The tag used for Logcat output when reporting render timings and cleanup.
 * The default value is "NavTrace".
 * @param onReportJankState A callback used to report the entry's visibility state to an
 * external performance monitoring system (like JankStats or a custom analytics engine).
 * The first parameter is the screen's [contentKey] (as a String), and the second is its visibility (`true` for visible, `false` for inactive/disposed).
 * @return A new or remembered instance of [TracingNavEntryDecorator].
 */

// [END_EXCLUDE]

@Composable
fun <T: Any> rememberTracingNavEntryDecorator(
    logTag: String = "NavPerformance",
    reportJankState: (screenName: String, isVisible: Boolean) -> Unit = { _, _ -> },
): TracingNavEntryDecorator<T> {

    val currentReportJankState = rememberUpdatedState(reportJankState)

    return remember(logTag, currentReportJankState) {
        TracingNavEntryDecorator(
            // [START_EXCLUDE]
            logTag = logTag,
            onReportJankState = { name, visible ->
                currentReportJankState.value.invoke(name, visible)
            }
        )
    }
}

@Serializable
data object FirstScreen : NavKey

@Composable
fun CustomDecorators() {

    val backStack = rememberNavBackStack(FirstScreen)
    // [END_EXCLUDE]
    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberTracingNavEntryDecorator()
        ),
        // [START_EXCLUDE]
        backStack = backStack,
        entryProvider = entryProvider { },
        // [END_EXCLUDE]
    )
    // [END android_compose_navigation3_custom_decorator_1]
}



