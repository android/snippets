package com.example.compose.snippets.graphics

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import com.example.compose.snippets.ui.theme.SnippetsTheme


@Preview(showBackground = true)
// [START android_compose_graphics_simple_shadow]
@Composable
fun ElevationBasedShadow() {
    Box(modifier = Modifier.aspectRatio(1f).fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Box(Modifier
            .size(100.dp, 100.dp)
            .shadow(10.dp, RectangleShape)
            .background(Color.White)
        )
    }
}
// [END android_compose_graphics_simple_shadow]

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
// [START android_compose_graphics_simple_drop_shadow]
@Composable
fun SimpleDropShadowUsage() {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .width(300.dp)
                .height(300.dp)
                .dropShadow(
                    shape = RoundedCornerShape(20.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        spread = 6.dp,
                        color = Color(0x40000000),
                        offset = DpOffset(x = 4.dp, 4.dp)
                    )
                )
                .align(Alignment.Center)

                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Text(
                "Drop Shadow",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 32.sp
            )
        }
    }
}
// [END android_compose_graphics_simple_drop_shadow]

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
// [START android_compose_graphics_simple_inner_shadow]
@Composable
fun SimpleInnerShadowUsage() {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .width(300.dp)
                .height(200.dp)
                .align(Alignment.Center)

                //note that the background needs to be defined before defining the inner shadow
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )

                .innerShadow(
                    shape = RoundedCornerShape(20.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        spread = 2.dp,
                        color = Color(0x40000000),
                        offset = DpOffset(x = 6.dp, 7.dp)
                    )
                )

        ) {
            Text(
                "Inner Shadow",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 32.sp
            )
        }
    }
}
// [END android_compose_graphics_simple_inner_shadow]

@Preview(
    showBackground = true,
    backgroundColor = 0xFF232323
)
// [START android_compose_graphics_realistic_shadow]
@Composable
fun RealisticShadows() {
    Box(Modifier.fillMaxSize()) {
        val dropShadowColor1 = Color(0xB3000000)
        val dropShadowColor2 = Color(0x66000000)

        val innerShadowColor1 = Color(0xCC000000)
        val innerShadowColor2 = Color(0xFF050505)
        val innerShadowColor3 = Color(0x40FFFFFF)
        val innerShadowColor4 = Color(0x1A050505)
        Box(
            Modifier
                .width(300.dp)
                .height(200.dp)
                .align(Alignment.Center)

                .dropShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 40.dp,
                        spread = 0.dp,
                        color = dropShadowColor1,
                        offset = DpOffset(x = 2.dp, 8.dp)
                    )
                )

                .dropShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 4.dp,
                        spread = 0.dp,
                        color = dropShadowColor2,
                        offset = DpOffset(x = 0.dp, 4.dp)
                    )
                )


                //note that the background needs to be defined before defining the inner shadow
                .background(
                    color = Color.Black,
                    shape = RoundedCornerShape(100.dp)
                )
////
                .innerShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 12.dp,
                        spread = 3.dp,
                        color = innerShadowColor1,
                        offset = DpOffset(x = 6.dp, 6.dp)
                    )
                )

                .innerShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 4.dp,
                        spread = 1.dp,
                        color = Color.White,
                        offset = DpOffset(x = 5.dp, 5.dp)
                    )
                )

                .innerShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 12.dp,
                        spread = 5.dp,
                        color = innerShadowColor2,
                        offset = DpOffset(x = (-3).dp, (-12).dp)
                    )
                )

                .innerShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 3.dp,
                        spread = 10.dp,
                        color = innerShadowColor3,
                        offset = DpOffset(x = 0.dp, 0.dp)
                    )
                )

                .innerShadow(
                    shape = RoundedCornerShape(100.dp),
                    shadow = Shadow(
                        radius = 3.dp,
                        spread = 9.dp,
                        color = innerShadowColor4,
                        offset = DpOffset(x = 1.dp, 1.dp)
                    )
                )


        ) {
            Text(
                "Realistic Shadows",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}
// [END android_compose_graphics_realistic_shadow]

// Define breathing states
enum class BreathingState {
    Inhaling,
    Exhaling
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun GradientBasedShadowAnimation() {
    SnippetsTheme {
        val colors = listOf(
            Color(0xFF4cc9f0),
            Color(0xFFf72585),
            Color(0xFFb5179e),
            Color(0xFF7209b7),
            Color(0xFF560bad),
            Color(0xFF480ca8),
            Color(0xFF3a0ca3),
            Color(0xFF3f37c9),
            Color(0xFF4361ee),
            Color(0xFF4895ef),
            Color(0xFF4cc9f0)
        )

        //..

        // State for the breathing animation
        var breathingState by remember { mutableStateOf(BreathingState.Inhaling) }

        // Create transition based on breathing state
        val transition = updateTransition(
            targetState = breathingState,
            label = "breathing_transition"
        )

        // Animate spread based on breathing state
        val animatedSpread by transition.animateFloat(
            transitionSpec = {
                tween(
                    durationMillis = 5000,
                    easing = FastOutSlowInEasing
                )
            },
            label = "spread_animation"
        ) { state ->
            when (state) {
                BreathingState.Inhaling -> 10f
                BreathingState.Exhaling -> 2f
            }
        }

        // Animate alpha based on breathing state (optional)
        val animatedAlpha by transition.animateFloat(
            transitionSpec = {
                tween(
                    durationMillis = 2000,
                    easing = FastOutSlowInEasing
                )
            },
            label = "alpha_animation"
        ) { state ->
            when (state) {
                BreathingState.Inhaling -> 1f
                BreathingState.Exhaling -> 1f
            }
        }


        // Get text based on current state
        val breathingText = when (breathingState) {
            BreathingState.Inhaling -> "Inhale"
            BreathingState.Exhaling -> "Exhale"
        }

        // Switch states when animation completes
        LaunchedEffect(breathingState) {
            delay(5000) // Wait for animation to complete
            breathingState = when (breathingState) {
                BreathingState.Inhaling -> BreathingState.Exhaling
                BreathingState.Exhaling -> BreathingState.Inhaling
            }
        }

        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // [START android_compose_graphics_gradient_shadow]
            Box(
                modifier = Modifier
                    .width(240.dp)
                    .height(200.dp)
                    .dropShadow(
                        shape = RoundedCornerShape(70.dp),
                        shadow = Shadow(
                            radius = 10.dp,
                            spread = animatedSpread.dp,
                            brush = Brush.sweepGradient(
                                colors
                            ),
                            offset = DpOffset(x = 0.dp, y = 0.dp),
                            alpha = animatedAlpha
                        )
                    )
                    .clip(RoundedCornerShape(70.dp))
                    .background(Color(0xEDFFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = breathingText,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            // [END android_compose_graphics_gradient_shadow]
        }
    }
}

@Preview
// [START android_compose_graphics_neumorphic_shadow]
@Composable
fun NeumorphicRaisedButton(
    shape: RoundedCornerShape = RoundedCornerShape(30.dp)
) {
    val bgColor = Color(0xFFe0e0e0)
    val lightShadow = Color(0xFFFFFFFF)
    val darkShadow = Color(0xFFb1b1b1)
    val upperOffset = -10.dp
    val lowerOffset = 10.dp
    val radius = 15.dp
    val spread = 0.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .wrapContentSize(Alignment.Center)
            .size(240.dp)
            .dropShadow(
                shape,
                shadow = Shadow(
                    radius = radius,
                    color = lightShadow,
                    spread = spread,
                    offset = DpOffset(upperOffset, upperOffset)
                ),
            )
            .dropShadow(
                shape,
                shadow = Shadow(
                    radius = radius,
                    color = darkShadow,
                    spread = spread,
                    offset = DpOffset(lowerOffset, lowerOffset)
                ),

                )
            .background(bgColor, shape)
    )
}
// [END android_compose_graphics_neumorphic_shadow]

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
// [START android_compose_graphics_animated_shadow]
@Composable
fun AnimatedColoredShadows() {
    SnippetsTheme {
        Box(Modifier.fillMaxSize()) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            // Create transition with pressed state
            val transition = updateTransition(
                targetState = isPressed,
                label = "button_press_transition"
            )

            fun <T> buttonPressAnimation() = tween<T>(
                durationMillis = 400,
                easing = EaseInOut
            )

            // Animate all properties using the transition
            val shadowAlpha by transition.animateFloat(
                label = "shadow_alpha",
                transitionSpec = { buttonPressAnimation() }
            ) { pressed ->
                if (pressed) 0f else 1f
            }
            // [START_EXCLUDE]
            val innerShadowAlpha by transition.animateFloat(
                label = "shadow_alpha",
                transitionSpec = { buttonPressAnimation() }
            ) { pressed ->
                if (pressed) 1f else 0f
            }

            val blueDropShadowColor = Color(0x5C007AFF)

            val darkBlueDropShadowColor = Color(0x66007AFF)

            val greyInnerShadowColor1 = Color(0x1A007AFF)

            val greyInnerShadowColor2 = Color(0x1A007AFF)
            // [END_EXCLUDE]

            val blueDropShadow by transition.animateColor(
                label = "shadow_color",
                transitionSpec = { buttonPressAnimation() }
            ) { pressed ->
                if (pressed) Color.Transparent else blueDropShadowColor
            }

            // [START_EXCLUDE]
            val darkBlueDropShadow by transition.animateColor(
                label = "shadow_color",
                transitionSpec = { buttonPressAnimation() }
            ) { pressed ->
                if (pressed) Color.Transparent else darkBlueDropShadowColor
            }

            val innerShadowColor1 by transition.animateColor(
                label = "shadow_color",
                transitionSpec = { buttonPressAnimation() }
            ) { pressed ->
                if (pressed) greyInnerShadowColor1
                else greyInnerShadowColor2
            }

            val innerShadowColor2 by transition.animateColor(
                label = "shadow_color",
                transitionSpec = { buttonPressAnimation() }
            ) { pressed ->
                if (pressed) Color(0x4D007AFF)
                else Color(0x1A007AFF)
            }
            // [END_EXCLUDE]



            Box(
                Modifier
                    .clickable(
                        interactionSource, indication = null
                    ) {
                        //** ...... **//
                    }
                    .width(300.dp)
                    .height(200.dp)
                    .align(Alignment.Center)

                    .dropShadow(
                        shape = RoundedCornerShape(70.dp),
                        shadow = Shadow(
                            radius = 10.dp,
                            spread = 0.dp,
                            color = blueDropShadow,
                            offset = DpOffset(x = 0.dp, -(2).dp),
                            alpha = shadowAlpha
                        )
                    )

                    .dropShadow(
                        shape = RoundedCornerShape(70.dp),
                        shadow = Shadow(
                            radius = 10.dp,
                            spread = 0.dp,
                            color = darkBlueDropShadow,
                            offset = DpOffset(x = 2.dp, 6.dp),
                            alpha = shadowAlpha
                        )
                    )

                    //note that the background needs to be defined before defining the inner shadow
                    .background(
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(70.dp)
                    )

                    .innerShadow(
                        shape = RoundedCornerShape(70.dp),
                        shadow = Shadow(
                            radius = 8.dp,
                            spread = 4.dp,
                            color = innerShadowColor2,
                            offset = DpOffset(x = 4.dp, 0.dp)
                        )
                    )

                    .innerShadow(
                        shape = RoundedCornerShape(70.dp),
                        shadow = Shadow(
                            radius = 20.dp,
                            spread = 4.dp,
                            color = innerShadowColor1,
                            offset = DpOffset(x = 4.dp, 0.dp),
                            alpha = innerShadowAlpha
                        )
                    )


            ) {
                Text(
                    "Animated Shadows",
                    // [START_EXCLUDE]
                    modifier = Modifier
                        .align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 24.sp,
                    color = Color.Black
                    // [END_EXCLUDE]
                )
            }
        }
    }
}
// [END android_compose_graphics_animated_shadow]

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFCC00
)
// [START android_compose_graphics_neobrutal_shadow]
@Composable
fun NeoBrutalShadows() {
    SnippetsTheme {
        val dropShadowColor = Color(0xFF007AFF)
        val borderColor = Color(0xFFFF2D55)
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .width(300.dp)
                    .height(200.dp)
                    .align(Alignment.Center)
                    .dropShadow(
                        shape = RoundedCornerShape(0.dp),
                        shadow = Shadow(
                            radius = 0.dp,
                            spread = 0.dp,
                            color = dropShadowColor,
                            offset = DpOffset(x = 8.dp, 8.dp)
                        )
                    )
                    .border(
                        8.dp, borderColor
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(0.dp)
                    )
            ) {
                Text(
                    "Neobrutal Shadows",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
// [END android_compose_graphics_neobrutal_shadow]