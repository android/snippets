/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("unused")

package com.example.compose.snippets.touchinput.gestures

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.scrollableArea
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.annotation.FrequentlyChangingValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Preview
// [START android_compose_touchinput_gestures_clickable]
@Composable
private fun ClickableSample() {
    val count = remember { mutableIntStateOf(0) }
    // content that you want to make clickable
    Text(
        text = count.value.toString(),
        modifier = Modifier.clickable { count.value += 1 }
    )
}
// [END android_compose_touchinput_gestures_clickable]

@Preview
@Composable
private fun WithPointerInput() {
    val count = remember { mutableIntStateOf(0) }
    // content that you want to make clickable
    Text(
        text = count.value.toString(),
        modifier =
        // [START android_compose_touchinput_gestures_pointerinput]
        Modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = { /* Called when the gesture starts */ },
                onDoubleTap = { /* Called on Double Tap */ },
                onLongPress = { /* Called on Long Press */ },
                onTap = { /* Called on Tap */ }
            )
        }
        // [END android_compose_touchinput_gestures_pointerinput]
    )
}

@Preview
// [START android_compose_touchinput_gestures_vertical_scroll]
@Composable
private fun ScrollBoxes() {
    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .size(100.dp)
            .verticalScroll(rememberScrollState())
    ) {
        repeat(10) {
            Text("Item $it", modifier = Modifier.padding(2.dp))
        }
    }
}
// [END android_compose_touchinput_gestures_vertical_scroll]

@Preview
// [START android_compose_touchinput_gestures_smooth_scroll]
@Composable
private fun ScrollBoxesSmooth() {
    // Smoothly scroll 100px on first composition
    val state = rememberScrollState()
    LaunchedEffect(Unit) { state.animateScrollTo(100) }

    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .size(100.dp)
            .padding(horizontal = 8.dp)
            .verticalScroll(state)
    ) {
        repeat(10) {
            Text("Item $it", modifier = Modifier.padding(2.dp))
        }
    }
}
// [END android_compose_touchinput_gestures_smooth_scroll]

@Preview
// [START android_compose_touchinput_gestures_scrollable]
@Composable
private fun ScrollableSample() {
    // actual composable state
    var offset by remember { mutableFloatStateOf(0f) }
    Box(
        Modifier
            .size(150.dp)
            .scrollable(
                orientation = Orientation.Vertical,
                // Scrollable state: describes how to consume
                // scrolling delta and update offset
                state = rememberScrollableState { delta ->
                    offset += delta
                    delta
                }
            )
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(offset.toString())
    }
}
// [END android_compose_touchinput_gestures_scrollable]

@Preview
// [START android_compose_touchinput_gestures_scrollableArea]
@Composable
private fun ScrollableAreaSample() {
    // [START_EXCLUDE]
    // rememberScrollableAreaSampleScrollState() holds the scroll position and other relevant
    // information. It implements the ScrollableState interface, making it compatible with the
    // scrollableArea modifier
    val scrollState = rememberScrollableAreaSampleScrollState()
    // [END_EXCLUDE]
    Layout(
        modifier =
            Modifier
                .size(150.dp)
                .scrollableArea(scrollState, Orientation.Vertical)
                .background(Color.LightGray),
        // [START_EXCLUDE]
        content = {
            repeat(40) {
                Text(
                    modifier = Modifier.padding(vertical = 2.dp),
                    text = "Item $it",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                )
            }
        },
        // [END_EXCLUDE]
    ) { measurables, constraints ->
        // [START_EXCLUDE]
        var totalHeight = 0

        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables =
            measurables.map { measurable ->
                val placeable = measurable.measure(childConstraints)
                totalHeight += placeable.height
                placeable
            }

        val viewportHeight = constraints.maxHeight
        // [END_EXCLUDE]
        // Update the maximum scroll value to not scroll beyond limits and stop when scroll
        // reaches the end.
        scrollState.maxValue = (totalHeight - viewportHeight).coerceAtLeast(0)

        // Position the children within the layout.
        layout(constraints.maxWidth, viewportHeight) {
            // The current vertical scroll position, in pixels.
            val scrollY = scrollState.value
            val viewportCenterY = scrollY + viewportHeight / 2

            var placeableLayoutPositionY = 0
            placeables.forEach { placeable ->
                // This sample applies a scaling effect to items based on their distance
                // from the center, creating a wheel-like effect.
                // [START_EXCLUDE]
                val itemCenterY = placeableLayoutPositionY + placeable.height / 2
                val distanceFromCenter = abs(itemCenterY - viewportCenterY)
                val normalizedDistance =
                    (distanceFromCenter / (viewportHeight / 2f)).fastCoerceIn(0f, 1f)

                // Items scale between 0.4 at the edges of the viewport and 1 at the center.
                val scaleFactor = 1f - (normalizedDistance * 0.6f)
                // [END_EXCLUDE]
                // Place the item horizontally centered with a layer transformation for
                // scaling to achieve wheel-like effect.
                placeable.placeRelativeWithLayer(
                    x = constraints.maxWidth / 2 - placeable.width / 2,
                    // Offset y by the scroll position to make placeable visible in the viewport.
                    y = placeableLayoutPositionY - scrollY,
                ) {
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
                // Move to the next item's vertical position.
                placeableLayoutPositionY += placeable.height
            }
        }
    }
}
// [START_EXCLUDE]
/*
 * A custom implementation of ScrollableState that manages a scroll position and its maximum allowed
 * value.
 *
 * This is a simplified version of the `ScrollState` used by `Modifier.verticalScroll` and
 * `Modifier.horizontalScroll`, demonstrating how to implement a custom state for custom scrollable
 * containers.
 */
private class ScrollableAreaSampleScrollState(initial: Int) : ScrollableState {

    // The current integer scroll position in pixels.
    // This is backed by a mutableStateOf to trigger recomposition when it changes.
    @get:FrequentlyChangingValue
    var value by mutableIntStateOf(initial)
        private set

    // The maximum scroll position allowed. This is typically derived from the content size minus
    // viewport size.
    var maxValue: Int
        get() = _maxValueState.intValue
        set(newMax) {
            _maxValueState.intValue = newMax
            Snapshot.withoutReadObservation {
                if (value > newMax) {
                    value = newMax
                }
            }
        }

    private var _maxValueState = mutableIntStateOf(Int.MAX_VALUE)

    // Accumulates sub-pixel scroll deltas. This ensures that even small, fractional scroll
    // movements are accounted for and contribute to the total scroll position over time, preventing
    // loss of precision.
    private var accumulator: Float = 0f

    // The underlying ScrollableState that handles the actual scroll consumption logic. This lambda
    // is invoked when a scroll delta is received.
    private val scrollableState = ScrollableState {
        val absolute = (value + it + accumulator)
        val newValue = absolute.coerceIn(0f, maxValue.toFloat())
        val changed = absolute != newValue
        val consumed = newValue - value
        val consumedInt = consumed.fastRoundToInt()
        value += consumedInt
        accumulator = consumed - consumedInt

        if (changed) consumed else it
    }

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit,
    ): Unit = scrollableState.scroll(scrollPriority, block)

    override fun dispatchRawDelta(delta: Float): Float = scrollableState.dispatchRawDelta(delta)

    override val isScrollInProgress: Boolean
        get() = scrollableState.isScrollInProgress

    override val canScrollForward: Boolean by derivedStateOf { value < maxValue }

    override val canScrollBackward: Boolean by derivedStateOf { value > 0 }

    override val lastScrolledForward: Boolean
        get() = scrollableState.lastScrolledForward

    override val lastScrolledBackward: Boolean
        get() = scrollableState.lastScrolledBackward

    companion object {
        // Saver for CustomSampleScrollState, allowing it to be saved and restored across
        // process death or configuration changes. Only the current scroll 'value' is saved.
        val Saver: Saver<ScrollableAreaSampleScrollState, *> =
            Saver(save = { it.value }, restore = { ScrollableAreaSampleScrollState(it) })
    }
}

@Composable
private fun rememberScrollableAreaSampleScrollState(
    initial: Int = 0
): ScrollableAreaSampleScrollState {
    return rememberSaveable(saver = ScrollableAreaSampleScrollState.Saver) {
        ScrollableAreaSampleScrollState(initial = initial)
    }
}
// [END_EXCLUDE]
// [END android_compose_touchinput_gestures_scrollableArea]

// [START android_compose_touchinput_gestures_nested_scroll]
@Composable
private fun AutomaticNestedScroll() {
    val gradient = Brush.verticalGradient(0f to Color.Gray, 1000f to Color.White)
    Box(
        modifier = Modifier
            .background(Color.LightGray)
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
    ) {
        Column {
            repeat(6) {
                Box(
                    modifier = Modifier
                        .height(128.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Scroll here",
                        modifier = Modifier
                            .border(12.dp, Color.DarkGray)
                            .background(brush = gradient)
                            .padding(24.dp)
                            .height(150.dp)
                    )
                }
            }
        }
    }
}
// [END android_compose_touchinput_gestures_nested_scroll]

private object NestedScrollInterop {
    // [START android_compose_touchinput_gestures_nested_scroll_interop_activity]
    open class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            findViewById<ComposeView>(R.id.compose_view).apply {
                setContent {
                    val nestedScrollInterop = rememberNestedScrollInteropConnection()
                    // Add the nested scroll connection to your top level @Composable element
                    // using the nestedScroll modifier.
                    LazyColumn(modifier = Modifier.nestedScroll(nestedScrollInterop)) {
                        items(20) { item ->
                            Box(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .height(56.dp)
                                    .fillMaxWidth()
                                    .background(Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.toString())
                            }
                        }
                    }
                }
            }
        }
    }
    // [END android_compose_touchinput_gestures_nested_scroll_interop_activity]

    object R {
        object id {
            val compose_view = 1
        }

        object layout {
            val activity_main = 0
        }
    }
}

// [START android_compose_touchinput_gestures_draggable]
@Composable
private fun DraggableText() {
    var offsetX by remember { mutableFloatStateOf(0f) }
    Text(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    offsetX += delta
                }
            ),
        text = "Drag me!"
    )
}
// [END android_compose_touchinput_gestures_draggable]

// [START android_compose_touchinput_gestures_draggable_pointerinput]
@Composable
private fun DraggableTextLowLevel() {
    Box(modifier = Modifier.fillMaxSize()) {
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }

        Box(
            Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .background(Color.Blue)
                .size(50.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
        )
    }
}
// [END android_compose_touchinput_gestures_draggable_pointerinput]

// [START android_compose_touchinput_gestures_swipeable]
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeableSample() {
    val width = 96.dp
    val squareSize = 48.dp

    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1) // Maps anchor points (in px) to states

    Box(
        modifier = Modifier
            .width(width)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
            .background(Color.LightGray)
    ) {
        Box(
            Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .size(squareSize)
                .background(Color.DarkGray)
        )
    }
}
// [END android_compose_touchinput_gestures_swipeable]

// [START android_compose_touchinput_gestures_anchored_draggable_swipetodismiss_enum]
private enum class AnchoredDraggableSwipeDismissValue { DismissedStart, Resting }
// [END android_compose_touchinput_gestures_anchored_draggable_swipetodismiss_enum]

// Custom SwipeToDismiss component that allows dragging/swiping an item away
@Composable
private fun SwipeToDismiss(
    state: AnchoredDraggableState<AnchoredDraggableSwipeDismissValue>,
    modifier: Modifier = Modifier,
    background: @Composable () -> Unit,
    dismissContent: @Composable () -> Unit
) {
    // [START android_compose_touchinput_gestures_anchored_draggable_swipetodismiss_overscroll]
    val overscrollEffect = rememberOverscrollEffect()
    Box(
        modifier.fillMaxWidth()
    ) {
        Box(Modifier.fillMaxWidth()) {
            background()
        }
        // [START android_compose_touchinput_gestures_anchored_draggable_swipetodismiss_layout]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Read state.requireOffset() inside the offset lambda to defer reading to the placement phase
                .offset { IntOffset(state.requireOffset().roundToInt(), 0) }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val anchors = DraggableAnchors {
                        AnchoredDraggableSwipeDismissValue.DismissedStart at -placeable.width.toFloat()
                        AnchoredDraggableSwipeDismissValue.Resting at 0f
                    }
                    state.updateAnchors(anchors)
                    layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
                }
                // Pass overscrollEffect to anchoredDraggable so deltas beyond the bounds (min and max anchor) are dispatched to overscrollEffect, and render the visual effect with Modifier.overscroll.
                .overscroll(overscrollEffect)
                .anchoredDraggable(
                    state,
                    Orientation.Horizontal,
                    overscrollEffect = overscrollEffect
                )
        ) {
            dismissContent()
        }
        // [END android_compose_touchinput_gestures_anchored_draggable_swipetodismiss_layout]
    }
    // [END android_compose_touchinput_gestures_anchored_draggable_swipetodismiss_overscroll]
}

@Preview
@Composable
fun AnchoredDraggableSwipeToDismissSample() {
    // Use rememberSaveable with AnchoredDraggableState's saver to persist and restore the current settled value across configuration changes. For example, if a user drags the item to DismissedStart and then changes the screen orientation or battery saver gets enabled, the item will still be set to DismissedStart
    // [START android_compose_touchinput_gestures_anchored_draggable_swipetodismiss_state]
    val state =
        rememberSaveable(saver = AnchoredDraggableState.Saver()) {
            AnchoredDraggableState(initialValue = AnchoredDraggableSwipeDismissValue.Resting)
        }
    // [END android_compose_touchinput_gestures_anchored_draggable_swipetodismiss_state]
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SwipeToDismiss(
            state = state,
            modifier = Modifier.height(56.dp),
            background = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Red), contentAlignment = Alignment.Center
                ) {
                    val scope = rememberCoroutineScope()
                    Button(
                        onClick = {
                            scope.launch { state.animateTo(AnchoredDraggableSwipeDismissValue.Resting) }
                        }
                    ) {
                        Text("Click to animate the state back to center")
                    }
                }
            },
            dismissContent = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .shadow(1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText("Drag me to dismiss!")
                }
            }
        )
    }
}

// [START android_compose_touchinput_gestures_anchored_draggable_drawer_anchors]
enum class DrawerState {
    Open,
    Closed
}

// [START_EXCLUDE silent]
@Suppress("UNUSED_VARIABLE")
@Composable
private fun AnchoredDraggableDrawerAnchorsSample() {
    val density = LocalDensity.current
    val drawerWidth = with(density) { 260.dp.toPx() }
// [END_EXCLUDE]
    val anchors = DraggableAnchors {
        DrawerState.Open at drawerWidth
        DrawerState.Closed at 0f
    }
    val state = remember {
        AnchoredDraggableState(
            initialValue = DrawerState.Closed,
            anchors = anchors,
        )
    }
// [START_EXCLUDE silent]
}
// [END_EXCLUDE]
// [END android_compose_touchinput_gestures_anchored_draggable_drawer_anchors]

// [START android_compose_touchinput_gestures_anchored_draggable_drawer_toggle]
fun toggleDrawerState(
    state: AnchoredDraggableState<DrawerState>,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        if (state.currentValue == DrawerState.Open) {
            state.animateTo(DrawerState.Closed)
        } else {
            state.animateTo(DrawerState.Open)
        }
    }
}
// [END android_compose_touchinput_gestures_anchored_draggable_drawer_toggle]

// [START android_compose_touchinput_gestures_anchored_draggable_drawer_on_click]
@Composable
private fun AnchoredDraggableDrawerOnClickSample(
    modifier: Modifier = Modifier,
    state: AnchoredDraggableState<DrawerState>,
    coroutineScope: CoroutineScope
) {
    var screenState by remember { mutableStateOf(ScreenState.Home) }

    Box(modifier = modifier.fillMaxSize()) {
        HomeScreenDrawer(
            selectedScreen = screenState,
            onScreenSelected = { screen ->
                screenState = screen
                // Dismiss drawer when a destination is selected
                coroutineScope.launch { state.animateTo(DrawerState.Closed) }
            }
        )

        // ScreenContents(...)
    }
}
// [END android_compose_touchinput_gestures_anchored_draggable_drawer_on_click]

// [START android_compose_touchinput_gestures_anchored_draggable_drawer_graphics_layer]
@Composable
private fun AnchoredDraggableDrawerGraphicsLayerSample(
    state: AnchoredDraggableState<DrawerState>,
    drawerWidth: Float,
    selectedScreen: ScreenState,
    onDrawerClicked: () -> Unit
) {
    ScreenContents(
        selectedScreen = selectedScreen,
        onDrawerClicked = onDrawerClicked,
        modifier = Modifier
            .graphicsLayer {
                this.translationX = state.requireOffset()
                val scale = lerp(1f, 0.8f, state.requireOffset() / drawerWidth)
                this.scaleX = scale
                this.scaleY = scale
                val cornerRadius = lerp(0f, 32f, state.requireOffset() / drawerWidth)
                this.shape = RoundedCornerShape(cornerRadius.dp)
                this.clip = true
            }
            .anchoredDraggable(state, Orientation.Horizontal)
    )
}
// [END android_compose_touchinput_gestures_anchored_draggable_drawer_graphics_layer]

enum class ScreenState {
    Home,
    Sleep,
    Leaderboard,
    Settings
}

@Preview
@Composable
fun JetLaggedNavigationDrawer(
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val drawerWidth = with(density) { 260.dp.toPx() }
    val coroutineScope = rememberCoroutineScope()
    var screenState by remember { mutableStateOf(ScreenState.Home) }

    val anchors = DraggableAnchors {
        DrawerState.Open at drawerWidth
        DrawerState.Closed at 0f
    }
    val state = remember {
        AnchoredDraggableState(
            initialValue = DrawerState.Closed,
            anchors = anchors,
        )
    }

    fun toggleDrawerState() {
        coroutineScope.launch {
            if (state.currentValue == DrawerState.Open) {
                state.animateTo(DrawerState.Closed)
            } else {
                state.animateTo(DrawerState.Open)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        HomeScreenDrawer(
            selectedScreen = screenState,
            onScreenSelected = { screen ->
                screenState = screen
                coroutineScope.launch { state.animateTo(DrawerState.Closed) }
            }
        )

        ScreenContents(
            selectedScreen = screenState,
            onDrawerClicked = ::toggleDrawerState,
            modifier = Modifier
                .graphicsLayer {
                    this.translationX = state.requireOffset()
                    val scale = lerp(1f, 0.8f, state.requireOffset() / drawerWidth)
                    this.scaleX = scale
                    this.scaleY = scale
                    val cornerRadius = lerp(0f, 32f, state.requireOffset() / drawerWidth)
                    this.shape = RoundedCornerShape(cornerRadius.dp)
                    this.clip = true
                }
                .anchoredDraggable(state, Orientation.Horizontal)
        )
    }
}

@Composable
fun HomeScreenDrawer(
    modifier: Modifier = Modifier,
    selectedScreen: ScreenState = ScreenState.Home,
    onScreenSelected: (ScreenState) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F0))
            .padding(top = 48.dp, start = 24.dp, end = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        DrawerItem(
            icon = Icons.Default.Home,
            label = "Home",
            selected = selectedScreen == ScreenState.Home,
            onClick = { onScreenSelected(ScreenState.Home) }
        )
        DrawerItem(
            icon = Icons.Default.Bedtime,
            label = "Sleep",
            selected = selectedScreen == ScreenState.Sleep,
            onClick = { onScreenSelected(ScreenState.Sleep) }
        )
        DrawerItem(
            icon = Icons.Default.Leaderboard,
            label = "Leaderboard",
            selected = selectedScreen == ScreenState.Leaderboard,
            onClick = { onScreenSelected(ScreenState.Leaderboard) }
        )
        DrawerItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            selected = selectedScreen == ScreenState.Settings,
            onClick = { onScreenSelected(ScreenState.Settings) }
        )
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .padding(vertical = 8.dp)
            .background(
                color = if (selected) Color(0xFFFFD54F) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF2E2E2E),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color(0xFF2E2E2E),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ScreenContents(
    modifier: Modifier = Modifier,
    selectedScreen: ScreenState = ScreenState.Home,
    onDrawerClicked: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onDrawerClicked) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Toggle Drawer"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "JetLagged",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Current Screen: ${selectedScreen.name}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
        }
    }
}

// [START android_compose_touchinput_gestures_transformable]
@Composable
private fun TransformableSample() {
    // set up all transformation states
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    Box(
        Modifier
            // apply other transformations like rotation and zoom
            // on the pizza slice emoji
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            // add transformable to listen to multitouch transformation events
            // after offset
            .transformable(state = state)
            .background(Color.Blue)
            .fillMaxSize()
    )
}
// [END android_compose_touchinput_gestures_transformable]

@Composable
fun NestedScrollSample() {

    // [START android_compose_touchinput_gestures_nestedscrollconnection]
    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            println("Received onPreScroll callback.")
            return Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            println("Received onPostScroll callback.")
            return Offset.Zero
        }
    }
    // [END android_compose_touchinput_gestures_nestedscrollconnection]

    // [START android_compose_touchinput_gestures_nestedscrolldisabled]
    val disabledNestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return if (source == NestedScrollSource.SideEffect) {
                    available
                } else {
                    Offset.Zero
                }
            }
        }
    }
    // [END android_compose_touchinput_gestures_nestedscrolldisabled]
}
