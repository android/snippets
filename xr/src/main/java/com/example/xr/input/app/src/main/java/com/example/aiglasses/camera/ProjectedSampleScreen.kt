package com.example.aiglasses.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import androidx.xr.glimmer.onIndirectPointerGesture
import kotlinx.coroutines.delay

/**
 * Reusable Glimmer UI component for all AI Glasses projected samples.
 *
 * Encapsulates:
 * 1. Requesting focus via [FocusRequester] and [focusTarget] so Glimmer can receive indirect pointer events.
 * 2. Intercepting indirect pointer touchpad events ([onIndirectPointerGesture]) (`onClick`, `onSwipeForward`,
 *    `onSwipeBackward`) and routing them to [onGestureAction].
 * 3. Auto-dismissing active notifications after 3.5 seconds.
 * 4. Rendering standard additive UI chips (`TitleChip` over `Color.Black` transparent background).
 */
@Composable
fun ProjectedSampleScreen(
    activity: BaseProjectedActivity,
    subtitle: String? = null,
    consumeSwipeBackward: Boolean = true,
    onGestureAction: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val notificationText by remember { activity.notificationText }
    val gestureSource by remember { activity.gestureSourceText }
    val feedbackEventCount by remember { activity.feedbackEventCount }
    val isDisplayOn by remember { activity.isDisplayOn }

    var isShowingFeedback by remember { mutableStateOf(false) }

    LaunchedEffect(feedbackEventCount) {
        if (feedbackEventCount > 0) {
            isShowingFeedback = true
            delay(3500L)
            isShowingFeedback = false
        }
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black) // Black is 100% transparent on additive AI glasses displays
            .focusRequester(focusRequester)
            .focusTarget()
            .let { m ->
                if (consumeSwipeBackward) {
                    m.onIndirectPointerGesture(
                        enabled = true,
                        onClick = { onGestureAction("Glimmer UI: Touchpad Tap") },
                        onSwipeForward = { onGestureAction("Glimmer UI: Swipe Forward") },
                        onSwipeBackward = { onGestureAction("Glimmer UI: Swipe Backward") }
                    )
                } else {
                    m.onIndirectPointerGesture(
                        enabled = true,
                        onClick = { onGestureAction("Glimmer UI: Touchpad Tap") },
                        onSwipeForward = { onGestureAction("Glimmer UI: Swipe Forward") }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        TitleChip {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = if (isShowingFeedback) "Input detected" else "No action has happened",
                    style = GlimmerTheme.typography.bodyMedium,
                    color = if (isShowingFeedback) GlimmerTheme.colors.positive else GlimmerTheme.colors.primary
                )
                val subtext = if (isShowingFeedback) gestureSource else subtitle
                if (!subtext.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtext,
                        style = GlimmerTheme.typography.bodySmall,
                        color = if (isShowingFeedback) GlimmerTheme.colors.primary else Color.LightGray
                    )
                }
            }
        }
    }
}
