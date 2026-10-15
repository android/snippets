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

package com.example.compose.snippets.haptics

import android.annotation.SuppressLint
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInQuad
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import com.example.compose.snippets.R
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random
import kotlinx.coroutines.delay

private lateinit var vibrator: Vibrator
private val smoothTimings = longArrayOf(50, 50, 100)
private val amplitudes = intArrayOf(64, 128, 255)
private const val smoothRepeatIdx = -1
private val onOffTimings = longArrayOf(0, 100)
private const val onOffRepeatIdx = -1

private fun rampUpPatternSnippet() {
    // [START android_haptics_custom_ramp_up]
    val timings: LongArray = longArrayOf(
        50, 50, 50, 50, 50, 100, 350, 25, 25, 25, 25, 200
    )
    val amplitudes: IntArray = intArrayOf(
        33, 51, 75, 113, 170, 255, 0, 38, 62, 100, 160, 255
    )
    val repeatIndex = -1 // Don't repeat.

    vibrator.vibrate(
        VibrationEffect.createWaveform(
            timings, amplitudes, repeatIndex
        )
    )
    // [END android_haptics_custom_ramp_up]
}

private object RepeatingPatternScope {
    // [START android_haptics_custom_repeating]
    fun startVibrating() {
        val timings: LongArray = longArrayOf(50, 50, 100, 50, 50)
        val amplitudes: IntArray = intArrayOf(64, 128, 255, 128, 64)
        val repeat = 1 // Repeat from the second entry, index = 1.
        val repeatingEffect: VibrationEffect = VibrationEffect.createWaveform(
            timings, amplitudes, repeat
        )
        // repeatingEffect can be used in multiple places.

        vibrator.vibrate(repeatingEffect)
    }

    fun stopVibrating() {
        vibrator.cancel()
    }
    // [END android_haptics_custom_repeating]
}

private fun patternWithFallbackSnippet() {
    // [START android_haptics_custom_fallback]
    if (vibrator.hasAmplitudeControl()) {
        vibrator.vibrate(
            VibrationEffect.createWaveform(
                smoothTimings, amplitudes, smoothRepeatIdx
            )
        )
    } else {
        vibrator.vibrate(
            VibrationEffect.createWaveform(
                onOffTimings, onOffRepeatIdx
            )
        )
    }
    // [END android_haptics_custom_fallback]
}

private fun createComposedVibrationEffectsSnippet() {
    // [START android_haptics_custom_composition_basic]
    vibrator.vibrate(
        VibrationEffect.startComposition().addPrimitive(
            VibrationEffect.Composition.PRIMITIVE_SLOW_RISE
        ).addPrimitive(
            VibrationEffect.Composition.PRIMITIVE_CLICK
        ).compose()
    )
    // [END android_haptics_custom_composition_basic]
}

private fun addGapsBetweenVibrationPrimitivesSnippet() {
    // [START android_haptics_custom_composition_delays]
    val delayMs = 100
    vibrator.vibrate(
        VibrationEffect.startComposition().addPrimitive(
            VibrationEffect.Composition.PRIMITIVE_SPIN, 0.8f
        ).addPrimitive(
            VibrationEffect.Composition.PRIMITIVE_SPIN, 0.6f
        ).addPrimitive(
            VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f, delayMs
        ).compose()
    )
    // [END android_haptics_custom_composition_delays]
}

private fun checkSinglePrimitiveSupportedSnippet() {
    // [START android_haptics_custom_check_single_primitive]
    val primitive = VibrationEffect.Composition.PRIMITIVE_LOW_TICK

    if (vibrator.areAllPrimitivesSupported(primitive)) {
        vibrator.vibrate(
            VibrationEffect.startComposition()
                .addPrimitive(primitive).compose()
        )
    } else {
        // Play a predefined effect or custom pattern as a fallback.
    }
    // [END android_haptics_custom_check_single_primitive]
}

@SuppressLint("WrongConstant")
private fun checkMultiplePrimitivesSupportedSnippet() {
    // [START android_haptics_custom_check_multiple_primitives]
    val primitives: IntArray = intArrayOf(
        VibrationEffect.Composition.PRIMITIVE_LOW_TICK,
        VibrationEffect.Composition.PRIMITIVE_TICK,
        VibrationEffect.Composition.PRIMITIVE_CLICK
    )
    val supported: BooleanArray = vibrator.arePrimitivesSupported(*primitives)
    // [END android_haptics_custom_check_multiple_primitives]
}

// [START android_haptics_custom_resist_screen]
@Composable
fun ResistScreen() {
    // Control variables for the dragging of the indicator.
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(0f) }

    // Only vibrates while the user is dragging
    LaunchedEffect(isDragging) {
        // Continuously run the effect for vibration to occur even when the view
        // is not being drawn, when user stops dragging midway through gesture.
        while (isDragging) {
            // Calculate the interval inversely proportional to the drag offset.
            val vibrationInterval = calculateVibrationInterval(dragOffset)
            // Calculate the scale directly proportional to the drag offset.
            val vibrationScale = calculateVibrationScale(dragOffset)

            delay(vibrationInterval)
            vibrator.vibrate(
                VibrationEffect.startComposition().addPrimitive(
                    VibrationEffect.Composition.PRIMITIVE_LOW_TICK,
                    vibrationScale
                ).compose()
            )
        }
    }

    Column(
        Modifier
            .draggable(
                orientation = Orientation.Vertical,
                onDragStarted = {
                    isDragging = true
                },
                onDragStopped = {
                    isDragging = false
                },
                state = rememberDraggableState { delta ->
                    dragOffset += delta
                }
            )
    ) {
        // Build the indicator UI based on how much the user has dragged it.
        ResistIndicator(dragOffset)
    }
}
// [END android_haptics_custom_resist_screen]

// [START android_haptics_custom_expand_screen]
enum class ExpandShapeState {
    Collapsed,
    Expanded
}

@Composable
fun ExpandScreen() {
    // Control variable for the state of the indicator.
    var currentState by remember { mutableStateOf(ExpandShapeState.Collapsed) }

    // Animation between expanded and collapsed states.
    val transitionData = updateTransitionData(currentState)

    Column(
        Modifier
            .clickable(
                onClick = {
                    if (currentState == ExpandShapeState.Collapsed) {
                        currentState = ExpandShapeState.Expanded
                        vibrator.vibrate(
                            VibrationEffect.startComposition().addPrimitive(
                                VibrationEffect.Composition.PRIMITIVE_SLOW_RISE,
                                0.3f
                            ).addPrimitive(
                                VibrationEffect.Composition.PRIMITIVE_QUICK_FALL,
                                0.3f
                            ).compose()
                        )
                    } else {
                        currentState = ExpandShapeState.Collapsed
                        vibrator.vibrate(
                            VibrationEffect.startComposition().addPrimitive(
                                VibrationEffect.Composition.PRIMITIVE_SLOW_RISE
                            ).compose()
                        )
                    }
                }
            )
    ) {
        // Build the indicator UI based on the current state.
        ExpandIndicator(transitionData)
    }
}
// [END android_haptics_custom_expand_screen]

// [START android_haptics_custom_wobble_screen]
@Composable
fun WobbleScreen() {
    // Control variables for the dragging and animating state of the elastic.
    var dragDistance by remember { mutableStateOf(0f) }
    var isWobbling by remember { mutableStateOf(false) }

    // Use drag distance to create an animated float value behaving like a spring.
    val dragDistanceAnimated by animateFloatAsState(
        targetValue = if (dragDistance > 0f) dragDistance else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        // The spring ends on its target. An end value of 0f means that the
        // shape is back at rest, so the wobble is over.
        finishedListener = { endValue -> if (endValue == 0f) isWobbling = false },
    )

    LaunchedEffect(isWobbling) {
        while (isWobbling) {
            val displacement = dragDistanceAnimated / MAX_DRAG_DISTANCE
            // Use some sort of minimum displacement so the final few frames
            // of animation don't generate a vibration.
            if (displacement > SPIN_MIN_DISPLACEMENT) {
                vibrator.vibrate(
                    VibrationEffect.startComposition().addPrimitive(
                        VibrationEffect.Composition.PRIMITIVE_SPIN,
                        nextSpinScale(displacement)
                    ).addPrimitive(
                        VibrationEffect.Composition.PRIMITIVE_SPIN,
                        nextSpinScale(displacement)
                    ).compose()
                )
            }
            // Delay the next check for a sufficient duration until the
            // current composition finishes. Note that you can use
            // Vibrator.getPrimitiveDurations API to calculcate the delay.
            delay(VIBRATION_DURATION)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .draggable(
                onDragStopped = {
                    // Wobble only if the shape is far enough from rest to vibrate.
                    // It then springs back to 0f, and the finishedListener ends
                    // the wobble.
                    val displacement = dragDistanceAnimated / MAX_DRAG_DISTANCE
                    isWobbling = displacement > SPIN_MIN_DISPLACEMENT
                    dragDistance = 0f
                },
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    isWobbling = false
                    dragDistance += delta
                }
            )
    ) {
        // Draw the wobbling shape using the animated spring-like value.
        WobbleShape(dragDistanceAnimated)
    }
}

// Calculate a random scale for each spin to vary the full effect.
fun nextSpinScale(displacement: Float): Float {
    // Generate a random offset in the range [-0.1, +0.1] to be added to the
    // vibration scale so the spin effects have slightly different values.
    val randomOffset: Float = Random.Default.nextFloat() * 0.2f - 0.1f
    return (displacement + randomOffset).absoluteValue.coerceIn(0f, 1f)
}
// [END android_haptics_custom_wobble_screen]

// [START android_haptics_custom_bounce_screen]
enum class BallPosition {
    Start,
    End
}

@Composable
fun BounceScreen() {
    // Control variable for the state of the ball.
    var ballPosition by remember { mutableStateOf(BallPosition.Start) }
    var bounceCount by remember { mutableStateOf(0) }

    // Animation for the bouncing ball.
    var transitionData = updateTransitionData(ballPosition)
    val collisionData = updateCollisionData(transitionData)

    // Ball is about to contact floor. The effect restarts only when the
    // collision state changes, so it vibrates once per collision.
    LaunchedEffect(collisionData.collisionWithFloor) {
        if (collisionData.collisionWithFloor) {
            val vibrationScale = 0.7.pow(bounceCount++).toFloat()
            vibrator.vibrate(
                VibrationEffect.startComposition().addPrimitive(
                    VibrationEffect.Composition.PRIMITIVE_THUD,
                    vibrationScale
                ).compose()
            )
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .clickable {
                if (transitionData.isAtStart) {
                    ballPosition = BallPosition.End
                } else {
                    ballPosition = BallPosition.Start
                    bounceCount = 0
                }
            },
    ) {
        // Build the ball UI based on the current state.
        BouncingBall(transitionData)
    }
}
// [END android_haptics_custom_bounce_screen]

private fun basicEnvelopeBuilderSnippet() {
    // [START android_haptics_custom_basic_envelope]
    vibrator.vibrate(
        VibrationEffect.BasicEnvelopeBuilder()
            .setInitialSharpness(0.0f)
            .addControlPoint(1.0f, 1.0f, 500)
            .addControlPoint(0.0f, 1.0f, 100)
            .build()
    )
    // [END android_haptics_custom_basic_envelope]
}

private fun waveformEnvelopeBuilderSnippet() {
    // [START android_haptics_custom_waveform_envelope]
    vibrator.vibrate(
        VibrationEffect.WaveformEnvelopeBuilder()
            .addControlPoint(1.0f, 60f, 50)
            .addControlPoint(1.0f, 120f, 100)
            .addControlPoint(1.0f, 120f, 200)
            .addControlPoint(0.0f, 60f, 50)
            .build()
    )
    // [END android_haptics_custom_waveform_envelope]
}

// [START android_haptics_custom_bouncing_spring]
@Composable
fun BouncingSpringAnimation() {
    // The spring's height above the floor: 1f is the drop height, 0f is the floor.
    val springHeight = remember { Animatable(1f) }
    var isAnimating by remember { mutableStateOf(true) }

    LaunchedEffect(isAnimating) {
        if (!isAnimating) return@LaunchedEffect
        springHeight.snapTo(1f)

        // Each bounce reaches a fraction of the previous height.
        var bounceHeight = 1f
        while (bounceHeight > MIN_BOUNCE_HEIGHT) {
            // Fall to the floor, speeding up like a falling object. A fall from
            // a greater height takes longer.
            val fallDurationMs = (DROP_DURATION_MS * sqrt(bounceHeight)).toInt()
            springHeight.animateTo(0f, tween(fallDurationMs, easing = EaseInQuad))

            // The spring hits the floor: play a "boing" envelope effect that fades
            // out. A higher bounce gives a stronger and sharper vibration.
            vibrator.vibrate(
                VibrationEffect.BasicEnvelopeBuilder()
                    // Starting from zero sharpness here, will simulate a smoother
                    // "boing" effect.
                    .setInitialSharpness(0f)
                    // Add a control point to reach the target intensity and
                    // sharpness very quickly.
                    .addControlPoint(bounceHeight, bounceHeight * MAX_SHARPNESS, 20L)
                    // Add a control point to fade out the vibration intensity while
                    // maintaining sharpness.
                    .addControlPoint(0f, bounceHeight * MAX_SHARPNESS, fallDurationMs.toLong())
                    .build()
            )

            // Bounce back up to a lower height, slowing down like a thrown object.
            // When the next bounce is too small, the spring stays on the floor.
            bounceHeight *= BOUNCE_DAMPING
            if (bounceHeight > MIN_BOUNCE_HEIGHT) {
                val riseDurationMs = (DROP_DURATION_MS * sqrt(bounceHeight)).toInt()
                springHeight.animateTo(bounceHeight, tween(riseDurationMs, easing = EaseOutQuad))
            }
        }
        isAnimating = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = !isAnimating) { isAnimating = true }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            // Read the height in the draw phase, and scale it to the distance
            // between the top of the canvas and the floor.
            val floorTop = size.height - FLOOR_HEIGHT.toPx()
            val dropDistance = floorTop - SPRING_HEIGHT.toPx()
            drawSpring(topY = dropDistance * (1f - springHeight.value))
            drawFloor(top = floorTop)
        }
        if (!isAnimating) {
            Text("Tap to restart", Modifier.align(Alignment.Center))
        }
    }
}
// [END android_haptics_custom_bouncing_spring]

// [START android_haptics_custom_rocket_launch]
@Composable
fun RocketLaunchAnimation() {
    var isLaunched by remember { mutableStateOf(false) }
    val animation = remember { Animatable(0f) }

    val animationDuration = 3000
    LaunchedEffect(isLaunched) {
        if (isLaunched) {
            animation.animateTo(
                1.2f, // Overshoot so that the rocket goes off the screen.
                animationSpec = tween(
                    durationMillis = animationDuration,
                    // Applies an easing curve with a slow start and rapid acceleration
                    // towards the end.
                    easing = CubicBezierEasing(1f, 0f, 0.75f, 1f)
                )
            )
            animation.snapTo(0f)
            isLaunched = false
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                if (!isLaunched) {
                    // Play vibration with same duration as the animation, using 70% of
                    // the time for the rise of the vibration, to match the easing curve
                    // defined previously.
                    playVibration(vibrator, animationDuration.toLong(), 0.7f)
                    isLaunched = true
                }
            }
            .background(colorResource(R.color.background))
    ) {
        // Read the animation in the draw phase, and scale it to the canvas height.
        drawRocket(size.height * animation.value)
    }
}

private fun playVibration(
    vibrator: Vibrator,
    totalDurationMs: Long,
    riseBias: Float,
    minOutputAccelerationGs: Float = 0.1f,
) {
    require(riseBias in 0f..1f) { "Rise bias must be between 0 and 1." }

    if (!vibrator.areEnvelopeEffectsSupported()) {
        return
    }

    val resonantFrequency = vibrator.resonantFrequency
    if (resonantFrequency.isNaN()) {
        // Device doesn't have or expose a resonant frequency.
        return
    }

    val startFrequency = vibrator.frequencyProfile?.getFrequencyRange(minOutputAccelerationGs)?.lower ?: return

    if (startFrequency >= resonantFrequency) {
        // Vibrator can't generate the minimum required output at lower frequencies.
        return
    }

    val minDurationMs = vibrator.envelopeEffectInfo.minControlPointDurationMillis
    val rampUpDurationMs = (riseBias * totalDurationMs).toLong() - minDurationMs
    val rampDownDurationMs = totalDurationMs - rampUpDurationMs - minDurationMs

    vibrator.vibrate(
        VibrationEffect.WaveformEnvelopeBuilder()
            // Quickly reach the target output at the start frequency
            .addControlPoint(0.1f, startFrequency, minDurationMs)
            .addControlPoint(0.1f, resonantFrequency, rampUpDurationMs)
            .addControlPoint(0.1f, startFrequency, rampDownDurationMs)
            // Controlled ramp down to zero to avoid ringing after the vibration.
            .addControlPoint(0.0f, startFrequency, minDurationMs)
            .build()
    )
}
// [END android_haptics_custom_rocket_launch]

// [START android_haptics_custom_lavabeats]
private fun createEnvelopeEffect(
    beatParameters: List<BeatParameter>
): VibrationEffect =
    VibrationEffect.WaveformEnvelopeBuilder()
        .apply {
            repeat(beatParameters.getNumBeats()) {
                // First pulse chirp
                addControlPoint(
                    beatParameters.getFirstPulseAmplitude(),
                    beatParameters.getFirstPulseStartFreq(),
                    ENVELOPE_RAMP_DURATION_MILLIS,
                )
                addControlPoint(
                    beatParameters.getFirstPulseAmplitude(),
                    beatParameters.getFirstPulseEndFreq(),
                    beatParameters.getFirstPulseDurationMillis().toLong(),
                )
                addControlPoint(
                    0f,
                    beatParameters.getFirstPulseEndFreq(),
                    ENVELOPE_RAMP_DURATION_MILLIS,
                )

                // Delay between first and second pulse
                addControlPoint(
                    0f,
                    beatParameters.getFirstPulseEndFreq(),
                    beatParameters.getFirstToSecondPulseDelayMillis().toLong(),
                )

                // Second pulse
                addControlPoint(
                    beatParameters.getSecondPulseAmplitude(),
                    beatParameters.getSecondPulseFreq(),
                    ENVELOPE_RAMP_DURATION_MILLIS,
                )
                addControlPoint(
                    beatParameters.getSecondPulseAmplitude(),
                    beatParameters.getSecondPulseFreq(),
                    (1_000 / (2f * beatParameters.getSecondPulseFreq())).toLong(),
                )
                addControlPoint(
                    0f,
                    beatParameters.getSecondPulseFreq(),
                    ENVELOPE_RAMP_DURATION_MILLIS,
                )
                addControlPoint(
                    0f,
                    beatParameters.getSecondPulseFreq(),
                    beatParameters.getBeatDelayMillis().toLong(),
                )
            }
        }
        .build()

/** A parameter of a haptic beat effect that represents an ECG signal parameter */
data class BeatParameter(
    val description: String = "",
    val value: Float = 0f,
    val range: ClosedFloatingPointRange<Float> = 0f..1f,
    val steps: Int = 0,
    val isFrequencyType: Boolean = false,
)
// [END android_haptics_custom_lavabeats]

private const val MAX_DRAG_DISTANCE = 500f
private const val SPIN_MIN_DISPLACEMENT = 0.05f
private const val VIBRATION_DURATION = 50L

private val SPRING_HEIGHT = 100.dp
private val FLOOR_HEIGHT = 20.dp
private const val DROP_DURATION_MS = 500f
private const val BOUNCE_DAMPING = 0.6f
private const val MIN_BOUNCE_HEIGHT = 0.05f
private const val MAX_SHARPNESS = 0.5f

private const val TAG = "LavaBeatsEffectPlayer"
private const val MIN_BEAT_DELAY_MILLIS = 5f
private const val ENVELOPE_RAMP_DURATION_MILLIS = 5L

private fun List<BeatParameter>.getFirstPulseStartFreq(): Float = this[0].value
private fun List<BeatParameter>.getFirstPulseEndFreq(): Float = this[1].value
private fun List<BeatParameter>.getFirstPulseDurationMillis(): Float = this[2].value
private fun List<BeatParameter>.getFirstPulseAmplitude(): Float = this[3].value
private fun List<BeatParameter>.getSecondPulseFreq(): Float = this[4].value
private fun List<BeatParameter>.getSecondPulseAmplitude(): Float = this[5].value
private fun List<BeatParameter>.getFirstToSecondPulseDelayMillis(): Float = this[6].value
private fun List<BeatParameter>.getBpm(): Float = this[7].value
private fun List<BeatParameter>.getNumBeats(): Int = this[8].value.toInt()
private fun List<BeatParameter>.getBeatDelayMillis(): Float {
    val targetValue =
        60_000f / getBpm() - getFirstPulseDurationMillis() - getFirstToSecondPulseDelayMillis()
    if (targetValue <= 0) {
        Log.e(
            TAG,
            "Invalid beat delay from selected parameters, returning a minimum of " +
                "$MIN_BEAT_DELAY_MILLIS",
        )
        return MIN_BEAT_DELAY_MILLIS
    }
    return targetValue
}

// The helpers below draw the demos that the snippets above call into. They are
// adapted from the Haptics sample in android/platform-samples
// (samples/user-interface/haptics), without its view models, theme and resources.

/** Which demo [CustomHapticEffectsDemo] shows. */
enum class CustomHapticsDemo { Resist, Expand, Wobble, Bounce, Spring, Rocket }

/**
 * Entry point for the demos. It assigns the file-level [vibrator] that every
 * snippet uses before the first demo composes.
 */
@Composable
fun CustomHapticEffectsDemo(demo: CustomHapticsDemo) {
    val context = LocalContext.current
    vibrator = remember(context) { context.getSystemService(Vibrator::class.java) }
    when (demo) {
        CustomHapticsDemo.Resist -> ResistScreen()
        CustomHapticsDemo.Expand -> ExpandScreen()
        CustomHapticsDemo.Wobble -> WobbleScreen()
        CustomHapticsDemo.Bounce -> BounceScreen()
        CustomHapticsDemo.Spring -> BouncingSpringAnimation()
        CustomHapticsDemo.Rocket -> RocketLaunchAnimation()
    }
}

private val SHAPE_COLOR = Color(0xFF6750A4)
private val FLOOR_COLOR = Color(0xFF625B71)
private val FLAME_COLOR = Color(0xFFFF7043)

// Resist: ticks get more frequent and stronger as the user drags further.
private const val RESIST_TICK_INTERVAL_MIN_MS = 30L
private const val RESIST_TICK_INTERVAL_MAX_MS = 60L
private const val RESIST_TICK_INTENSITY_MIN = 0.2f
private const val RESIST_TICK_INTENSITY_MAX = 0.8f
private val RESIST_START_SIZE = 64.dp
private val RESIST_END_SIZE = 128.dp
private val RESIST_START_STROKE = 8.dp
private val RESIST_END_STROKE = 16.dp
private val RESIST_END_OFFSET_Y = 150.dp
private const val RESIST_START_ROTATION = 90f
private const val RESIST_END_ROTATION = RESIST_START_ROTATION + 360f

// MAX_DRAG_DISTANCE is in px and the response is linear. The sample uses 350.dp
// with a DecelerateInterpolator; the difference doesn't matter for the demo.
private fun resistFraction(dragOffset: Float): Float =
    (dragOffset / MAX_DRAG_DISTANCE).coerceIn(0f, 1f)

private fun calculateVibrationInterval(dragOffset: Float): Long = lerp(
    RESIST_TICK_INTERVAL_MAX_MS.toFloat(),
    RESIST_TICK_INTERVAL_MIN_MS.toFloat(),
    resistFraction(dragOffset)
).toLong()

private fun calculateVibrationScale(dragOffset: Float): Float =
    lerp(RESIST_TICK_INTENSITY_MIN, RESIST_TICK_INTENSITY_MAX, resistFraction(dragOffset))

/**
 * A ring that grows, thickens, rotates and moves down with the drag. It fills
 * the screen so the draggable Column in [ResistScreen] has a touch area.
 */
@Composable
private fun ResistIndicator(dragOffset: Float) {
    val fraction = resistFraction(dragOffset)
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            progress = { 0.75f },
            modifier = Modifier
                .offset { IntOffset(0, lerp(0f, RESIST_END_OFFSET_Y.toPx(), fraction).roundToInt()) }
                .size(lerp(RESIST_START_SIZE, RESIST_END_SIZE, fraction))
                .graphicsLayer {
                    rotationZ = lerp(RESIST_START_ROTATION, RESIST_END_ROTATION, fraction)
                },
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = lerp(RESIST_START_STROKE, RESIST_END_STROKE, fraction)
        )
        if (fraction < 0.05f) {
            Text("Drag down", Modifier.padding(top = 16.dp))
        }
    }
}

// Expand: a ring that goes from small and thick to large and thin.
private const val EXPAND_TO_EXPANDED_MS = 650
private const val EXPAND_TO_COLLAPSED_MS = 500
private val EXPAND_COLLAPSED_SIZE = 64.dp
private val EXPAND_EXPANDED_SIZE = EXPAND_COLLAPSED_SIZE * 5
private val EXPAND_COLLAPSED_THICKNESS = EXPAND_COLLAPSED_SIZE / 2
private val EXPAND_EXPANDED_THICKNESS = EXPAND_COLLAPSED_SIZE / 4

/** Animation values for [ExpandScreen]. [progress] is 0f collapsed and 1f expanded. */
private class ExpandTransitionData(val progress: Float)

@Composable
private fun updateTransitionData(state: ExpandShapeState): ExpandTransitionData {
    val transition = updateTransition(state, label = "expand")
    val progress by transition.animateFloat(
        transitionSpec = {
            if (targetState == ExpandShapeState.Expanded) {
                tween(EXPAND_TO_EXPANDED_MS)
            } else {
                tween(EXPAND_TO_COLLAPSED_MS)
            }
        },
        label = "progress"
    ) { if (it == ExpandShapeState.Expanded) 1f else 0f }
    return ExpandTransitionData(progress)
}

@Composable
private fun ExpandIndicator(transitionData: ExpandTransitionData) {
    val color = MaterialTheme.colorScheme.primary
    Canvas(Modifier.fillMaxSize()) {
        val progress = transitionData.progress
        val sizePx = lerp(EXPAND_COLLAPSED_SIZE, EXPAND_EXPANDED_SIZE, progress).toPx()
        val thicknessPx = lerp(EXPAND_COLLAPSED_THICKNESS, EXPAND_EXPANDED_THICKNESS, progress).toPx()
        // A stroke is centered on the radius, so shrink the radius to keep the
        // outer diameter equal to sizePx.
        drawCircle(
            color = color,
            radius = (sizePx - thicknessPx) / 2,
            style = Stroke(width = thicknessPx)
        )
    }
}

// Wobble: a tall shape anchored to the bottom whose top edge bulges with the drag.
private val WOBBLE_MAX_BULGE = 120.dp

@Composable
private fun WobbleShape(dragDistanceAnimated: Float) {
    val color = MaterialTheme.colorScheme.primary
    Box(Modifier.fillMaxSize()) {
        Text("Drag down and release", Modifier.align(Alignment.TopCenter).padding(top = 48.dp))
        Canvas(Modifier.fillMaxSize()) {
            // The region doesn't limit dragDistance and the spring overshoots
            // below zero, so clamp the bulge like the sample's ElasticTopShape.
            val bulge = (dragDistanceAnimated / MAX_DRAG_DISTANCE).coerceIn(-1f, 1f) *
                WOBBLE_MAX_BULGE.toPx()
            val top = size.height * 0.25f
            val path = Path().apply {
                moveTo(0f, size.height)
                lineTo(0f, top)
                // The apex of a quadratic curve sits halfway to the control point.
                quadraticTo(size.width / 2, top + 2 * bulge, size.width, top)
                lineTo(size.width, size.height)
                close()
            }
            drawPath(path, color)
        }
    }
}

// Bounce: a ball that drops on a floor and bounces a few times.
private const val BOUNCE_DROP_MS = 3000
private const val BOUNCE_RESET_MS = 1000
private val BALL_SIZE = 64.dp
private val BALL_DROP_HEIGHT = 300.dp
private val FLOOR_SIZE = 80.dp
// The ball counts as touching the floor within this distance. A range is
// needed because we don't know which frames the animation draws.
private val FLOOR_COLLISION_DISTANCE = 5.dp

/** Animation values for [BounceScreen]. */
private class BounceTransitionData(
    /** The transition is complete and the ball is in its start position. */
    val isAtStart: Boolean,
    /** The ball has been dropped and hasn't come to rest yet. */
    val isBouncing: Boolean,
    /** 0.dp on the floor, negative above it. */
    val ballOffsetY: Dp,
)

private class CollisionData(val collisionWithFloor: Boolean)

@Composable
private fun updateTransitionData(state: BallPosition): BounceTransitionData {
    val transition = updateTransition(state, label = "bounce")
    val ballOffsetY by transition.animateDp(
        transitionSpec = {
            if (targetState == BallPosition.End) {
                tween(BOUNCE_DROP_MS, easing = BounceEasing())
            } else {
                tween(BOUNCE_RESET_MS, easing = LinearOutSlowInEasing)
            }
        },
        label = "ballOffsetY"
    ) { if (it == BallPosition.End) 0.dp else -BALL_DROP_HEIGHT }
    val isAtTargetState = transition.currentState == transition.targetState
    return BounceTransitionData(
        isAtStart = isAtTargetState && transition.currentState == BallPosition.Start,
        isBouncing = !isAtTargetState && transition.targetState == BallPosition.End,
        ballOffsetY = ballOffsetY
    )
}

/**
 * The flag is true while the ball is within [FLOOR_COLLISION_DISTANCE] of the
 * floor during the drop, and false again between bounces and once the ball is at
 * rest, so the effect keyed on it in [BounceScreen] fires once per bounce.
 */
private fun updateCollisionData(transitionData: BounceTransitionData): CollisionData =
    CollisionData(
        collisionWithFloor = transitionData.isBouncing &&
            transitionData.ballOffsetY > -FLOOR_COLLISION_DISTANCE
    )

@Composable
private fun BouncingBall(transitionData: BounceTransitionData) {
    val ballColor = MaterialTheme.colorScheme.primary
    val floorColor = MaterialTheme.colorScheme.secondaryContainer
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        // The ball is drawn first so the easing's overshoot below the floor line
        // is hidden behind the floor.
        Box(
            Modifier
                .offset { IntOffset(0, (transitionData.ballOffsetY - FLOOR_SIZE).roundToPx()) }
                .size(BALL_SIZE)
                .clip(CircleShape)
                .background(ballColor)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(FLOOR_SIZE)
                .background(floorColor)
        )
        if (transitionData.isAtStart) {
            Text("Tap to drop", Modifier.align(Alignment.TopCenter).padding(top = 48.dp))
        }
    }
}

/**
 * An easing that bounces several times, adapted from the platform
 * BounceInterpolator with more bounces in the same amount of time.
 */
private class BounceEasing : Easing {
    override fun transform(fraction: Float): Float {
        val t = fraction * 1.5986f
        return when {
            t < 0.3535f -> bounce(t) + 0.1f // The addition is how much the floor compresses.
            t < 0.8007f -> bounce(t - 0.5771f) + 0.6f + 0.08f
            t < 1.1169f -> bounce(t - 0.9588f) + 0.8f + 0.06f
            t < 1.3405f -> bounce(t - 1.2287f) + 0.9f + 0.04f
            t < 1.4986f -> bounce(t - 1.419557f) + 0.95f + 0.02f
            else -> bounce(t - 1.5486f) + 0.98f
        }
    }

    private fun bounce(t: Float): Float = t * t * 8.0f
}

// Spring: a coil that falls on a floor, used by BouncingSpringAnimation.
private val SPRING_WIDTH = 48.dp
private val SPRING_STROKE = 8.dp
private const val SPRING_COILS = 8

private fun DrawScope.drawFloor(top: Float) {
    drawRect(
        color = FLOOR_COLOR,
        topLeft = Offset(0f, top),
        size = Size(size.width, size.height - top)
    )
}

private fun DrawScope.drawSpring(topY: Float) {
    val halfWidth = SPRING_WIDTH.toPx() / 2
    val left = center.x - halfWidth
    val right = center.x + halfWidth
    val coilHeight = SPRING_HEIGHT.toPx() / SPRING_COILS
    val path = Path().apply {
        moveTo(left, topY)
        lineTo(right, topY)
        // Zig-zag between the two sides, one segment per coil.
        for (i in 1..SPRING_COILS) {
            lineTo(if (i % 2 == 0) right else left, topY + i * coilHeight)
        }
        val bottom = topY + SPRING_HEIGHT.toPx()
        lineTo(if (SPRING_COILS % 2 == 0) left else right, bottom)
    }
    drawPath(
        path = path,
        color = SHAPE_COLOR,
        style = Stroke(width = SPRING_STROKE.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

// Rocket: a body with a nose, two fins and a flame, used by RocketLaunchAnimation.
private val ROCKET_WIDTH = 24.dp
private val ROCKET_HEIGHT = 72.dp
private val ROCKET_PAD_HEIGHT = 24.dp
private val ROCKET_NOSE_HEIGHT = 24.dp
private val ROCKET_FIN_SIZE = 12.dp
private val ROCKET_FLAME_HEIGHT = 32.dp

private fun DrawScope.drawRocket(rocketPositionY: Float) {
    val width = ROCKET_WIDTH.toPx()
    val height = ROCKET_HEIGHT.toPx()
    val left = center.x - width / 2
    val right = center.x + width / 2
    val bottom = size.height - ROCKET_PAD_HEIGHT.toPx() - rocketPositionY
    val top = bottom - height
    val finSize = ROCKET_FIN_SIZE.toPx()

    if (rocketPositionY > 0f) {
        val flame = Path().apply {
            moveTo(left + width * 0.25f, bottom)
            lineTo(center.x, bottom + ROCKET_FLAME_HEIGHT.toPx())
            lineTo(right - width * 0.25f, bottom)
            close()
        }
        drawPath(flame, FLAME_COLOR)
    }
    drawRoundRect(
        color = SHAPE_COLOR,
        topLeft = Offset(left, top),
        size = Size(width, height),
        cornerRadius = CornerRadius(width / 4)
    )
    val nose = Path().apply {
        moveTo(left, top)
        lineTo(center.x, top - ROCKET_NOSE_HEIGHT.toPx())
        lineTo(right, top)
        close()
    }
    drawPath(nose, SHAPE_COLOR)
    val fins = Path().apply {
        moveTo(left, bottom)
        lineTo(left - finSize, bottom)
        lineTo(left, bottom - finSize)
        close()
        moveTo(right, bottom)
        lineTo(right + finSize, bottom)
        lineTo(right, bottom - finSize)
        close()
    }
    drawPath(fins, SHAPE_COLOR)
}
