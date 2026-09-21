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
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.R
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.random.Random
import kotlinx.coroutines.delay

private lateinit var vibrator: Vibrator
private lateinit var context: Context
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

@RequiresApi(Build.VERSION_CODES.R)
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

@RequiresApi(Build.VERSION_CODES.S)
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

@RequiresApi(Build.VERSION_CODES.S)
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
@RequiresApi(Build.VERSION_CODES.S)
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

@RequiresApi(Build.VERSION_CODES.S)
// [START android_haptics_custom_resist_screen]
@Composable
fun ResistScreen() {
    // Control variables for the dragging of the indicator.
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(0f) }

    // Only vibrates while the user is dragging
    if (isDragging) {
        LaunchedEffect(Unit) {
            // Continuously run the effect for vibration to occur even when the view
            // is not being drawn, when user stops dragging midway through gesture.
            while (true) {
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
    }

    Screen() {
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
}
// [END android_haptics_custom_resist_screen]

@RequiresApi(Build.VERSION_CODES.R)
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

    Screen() {
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
}
// [END android_haptics_custom_expand_screen]

@RequiresApi(Build.VERSION_CODES.S)
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
    )

    if (isWobbling) {
        LaunchedEffect(Unit) {
            while (true) {
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
    }

    Box(
        Modifier
            .fillMaxSize()
            .draggable(
                onDragStopped = {
                    isWobbling = true
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

@RequiresApi(Build.VERSION_CODES.S)
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

    // Ball is about to contact floor, only vibrating once per collision.
    var hasVibratedForBallContact by remember { mutableStateOf(false) }
    if (collisionData.collisionWithFloor) {
        if (!hasVibratedForBallContact) {
            val vibrationScale = 0.7.pow(bounceCount++).toFloat()
            vibrator.vibrate(
                VibrationEffect.startComposition().addPrimitive(
                    VibrationEffect.Composition.PRIMITIVE_THUD,
                    vibrationScale
                ).compose()
            )
            hasVibratedForBallContact = true
        }
    } else {
        // Reset for next contact with floor.
        hasVibratedForBallContact = false
    }

    Screen() {
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
}
// [END android_haptics_custom_bounce_screen]

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
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

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
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

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
// [START android_haptics_custom_bouncing_spring]
@Composable
fun BouncingSpringAnimation() {
    var springX by remember { mutableStateOf(SPRING_WIDTH) }
    var springY by remember { mutableStateOf(SPRING_HEIGHT) }
    var velocityX by remember { mutableFloatStateOf(INITIAL_VELOCITY) }
    var velocityY by remember { mutableFloatStateOf(INITIAL_VELOCITY) }
    var sharpness by remember { mutableFloatStateOf(INITIAL_SHARPNESS) }
    var intensity by remember { mutableFloatStateOf(INITIAL_INTENSITY) }
    var multiplier by remember { mutableFloatStateOf(INITIAL_MULTIPLIER) }
    var bottomBounceCount by remember { mutableIntStateOf(0) }
    var animationStartTime by remember { mutableLongStateOf(0L) }
    var isAnimating by remember { mutableStateOf(false) }

    val (screenHeight, screenWidth) = getScreenDimensions(context)

    LaunchedEffect(isAnimating) {
        animationStartTime = System.currentTimeMillis()
        isAnimating = true

        while (isAnimating) {
            velocityY += GRAVITY
            springX += velocityX.dp
            springY += velocityY.dp

            // Handle bottom collision
            if (springY > screenHeight - FLOOR_HEIGHT - SPRING_HEIGHT / 2) {
                // Set the spring's y-position to the bottom bounce point, to keep it
                // above the floor.
                springY = screenHeight - FLOOR_HEIGHT - SPRING_HEIGHT / 2

                // Reverse the vertical velocity and apply damping to simulate a bounce.
                velocityY *= -BOUNCE_DAMPING
                bottomBounceCount++

                // Calculate the fade-out duration of the vibration based on the
                // vertical velocity.
                val fadeOutDuration =
                    ((abs(velocityY) / GRAVITY) * FRAME_DELAY_MS).toLong()

                // Create a "boing" envelope vibration effect that fades out.
                vibrator.vibrate(
                    VibrationEffect.BasicEnvelopeBuilder()
                        // Starting from zero sharpness here, will simulate a smoother
                        // "boing" effect.
                        .setInitialSharpness(0f)
                        // Add a control point to reach the target intensity and
                        // sharpness very quickly.
                        .addControlPoint(intensity, sharpness, 20L)
                        // Add a control point to fade out the vibration intensity while
                        // maintaining sharpness.
                        .addControlPoint(0f, sharpness, fadeOutDuration)
                        .build()
                )

                // Decrease the intensity and sharpness of the vibration for subsequent
                // bounces, and reduce the multiplier to create a fading effect.
                intensity *= multiplier
                sharpness *= multiplier
                multiplier -= 0.1f
            }

            if (springX > screenWidth - SPRING_WIDTH / 2) {
                // Prevent the spring from moving beyond the right edge of the screen.
                springX = screenWidth - SPRING_WIDTH / 2
            }

            // Check for 3 bottom bounces and then slow down.
            if (bottomBounceCount >= MAX_BOTTOM_BOUNCE &&
                System.currentTimeMillis() - animationStartTime > 1000
            ) {
                velocityX *= 0.9f
                velocityY *= 0.9f
            }

            delay(FRAME_DELAY_MS) // Control animation speed.

            // Determine if the animation should continue based on the spring's
            // position and velocity.
            isAnimating = (springY < screenHeight + SPRING_HEIGHT ||
                springX < screenWidth + SPRING_WIDTH) &&
                (velocityX >= 0.1f || velocityY >= 0.1f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                if (!isAnimating) {
                    resetAnimation()
                }
            }
            .width(screenWidth)
            .height(screenHeight)
    ) {
        DrawSpring(springX, springY)
        DrawFloor()
        if (!isAnimating) {
            DrawText("Tap to restart")
        }
    }
}
// [END android_haptics_custom_bouncing_spring]

@RequiresApi(Build.VERSION_CODES.BAKLAVA)
// [START android_haptics_custom_rocket_launch]
@Composable
fun RocketLaunchAnimation() {
    val screenHeight = remember { mutableFloatStateOf(0f) }
    var rocketPositionY by remember { mutableFloatStateOf(0f) }
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
            ) {
                rocketPositionY = screenHeight.floatValue * value
            }
            animation.snapTo(0f)
            rocketPositionY = 0f
            isLaunched = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                if (!isLaunched) {
                    // Play vibration with same duration as the animation, using 70% of
                    // the time for the rise of the vibration, to match the easing curve
                    // defined previously.
                    playVibration(vibrator, animationDuration.toLong(), 0.7f)
                    isLaunched = true
                }
            }
            .background(colorResource(R.color.background))
            .onSizeChanged { screenHeight.floatValue = it.height.toFloat() }
    ) {
        drawRocket(rocketPositionY)
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
@RequiresApi(Build.VERSION_CODES.BAKLAVA)
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
@Stable
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

private val SPRING_WIDTH = 100.dp
private val SPRING_HEIGHT = 100.dp
private val FLOOR_HEIGHT = 20.dp
private const val INITIAL_VELOCITY = 0f
private const val INITIAL_SHARPNESS = 0.5f
private const val INITIAL_INTENSITY = 1.0f
private const val INITIAL_MULTIPLIER = 0.8f
private const val GRAVITY = 0.5f
private const val BOUNCE_DAMPING = 0.7f
private const val FRAME_DELAY_MS = 16L
private const val MAX_BOTTOM_BOUNCE = 3

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
private fun Screen(content: @Composable () -> Unit) {
    content()
}

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

private fun getScreenDimensions(context: Context): Pair<Dp, Dp> = Pair(800.dp, 400.dp)

private fun resetAnimation() {}

@Composable
private fun DrawSpring(springX: Dp, springY: Dp) {}

@Composable
private fun DrawFloor() {}

@Composable
private fun DrawText(text: String) {}

@Composable
private fun drawRocket(rocketPositionY: Float) {}

private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}
