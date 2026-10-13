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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.R
import kotlin.math.absoluteValue
import kotlin.math.pow
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
                    // Wobble only if the user pulled the shape down.
                    isWobbling = dragDistance > 0f
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
            bounceHeight *= BOUNCE_DAMPING
            val riseDurationMs = (DROP_DURATION_MS * sqrt(bounceHeight)).toInt()
            springHeight.animateTo(bounceHeight, tween(riseDurationMs, easing = EaseOutQuad))
        }
        springHeight.animateTo(0f)
        isAnimating = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = !isAnimating) { isAnimating = true }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            // Read the height in the draw phase, and scale it to the space above
            // the floor.
            val floorTop = size.height - FLOOR_HEIGHT.toPx()
            drawSpring(bottomY = floorTop * (1f - springHeight.value))
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

private fun calculateVibrationInterval(dragOffset: Float): Long = 50L
private fun calculateVibrationScale(dragOffset: Float): Float = 0.5f

@Composable
private fun ResistIndicator(dragOffset: Float) {}

private class TransitionData(val isAtStart: Boolean = true)
private class CollisionData(val collisionWithFloor: Boolean = false)

@Composable
private fun <T> updateTransitionData(state: T): TransitionData = TransitionData()

private fun updateCollisionData(transitionData: TransitionData): CollisionData = CollisionData()

@Composable
private fun ExpandIndicator(transitionData: TransitionData) {}

@Composable
private fun WobbleShape(dragDistanceAnimated: Float) {}

@Composable
private fun BouncingBall(transitionData: TransitionData) {}

private fun DrawScope.drawSpring(bottomY: Float) {}

private fun DrawScope.drawFloor(top: Float) {}

private fun DrawScope.drawRocket(rocketPositionY: Float) {}
