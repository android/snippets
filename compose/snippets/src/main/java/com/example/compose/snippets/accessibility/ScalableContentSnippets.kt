package com.example.compose.snippets.accessibility

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

// [START android_compose_accessibility_scalable_content_density_scaling]
private class DensityScalingState(
    // Note: For accessibility, typical min/max values are ~0.75x and ~3.5x.
    private val minScale: Float = 0.75f,
    private val maxScale: Float = 3.5f,
    private val currentDensity: Density
) {
    val transformableState = TransformableState { zoomChange, _, _ ->
        scaleFactor.floatValue =
            (scaleFactor.floatValue * zoomChange).coerceIn(minScale, maxScale)
    }
    val scaleFactor = mutableFloatStateOf(1f)
    fun scaledDensity(): Density {
        return Density(
            currentDensity.density * scaleFactor.floatValue,
            currentDensity.fontScale
        )
    }
}

// [START_EXCLUDE silent]
@Preview
// [END_EXCLUDE silent]
@Composable
fun DensityScalingSample() {
    val currentDensity = LocalDensity.current
    val scaleState =
        remember(currentDensity) { DensityScalingState(currentDensity = currentDensity) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .transformable(state = scaleState.transformableState),
        contentAlignment = Alignment.TopCenter
    ) {
        CompositionLocalProvider(
            LocalDensity provides scaleState.scaledDensity()
        ) {
            DemoCard()
        }
    }
}
// [END android_compose_accessibility_scalable_content_density_scaling]

// [START android_compose_accessibility_scalable_content_font_scaling]
class FontScaleState(
    // Note: For accessibility, typical min/max values are ~0.75x and ~3.5x.
    private val minScale: Float = 0.75f,
    private val maxScale: Float = 3.5f,
    private val currentDensity: Density
) {
    val transformableState = TransformableState { zoomChange, _, _ ->
        scaleFactor.floatValue =
            (scaleFactor.floatValue * zoomChange).coerceIn(minScale, maxScale)
    }
    val scaleFactor = mutableFloatStateOf(1f)
    fun scaledFont(): Density {
        return Density(
            currentDensity.density,
            currentDensity.fontScale * scaleFactor.floatValue
        )
    }
}

// [START_EXCLUDE silent]
@Preview
// [END_EXCLUDE silent]
@Composable
fun FontScalingSample() {
    val currentDensity = LocalDensity.current
    val scaleState = remember { FontScaleState(currentDensity = currentDensity) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .transformable(state = scaleState.transformableState),
        contentAlignment = Alignment.TopCenter
    ) {
        CompositionLocalProvider(
            LocalDensity provides scaleState.scaledFont()
        ) {
            DemoCard()
        }
    }
}
// [END android_compose_accessibility_scalable_content_font_scaling]

// [START android_compose_accessibility_scalable_content_demo_card]
@Composable
private fun DemoCard() {
    Card(
        modifier = Modifier
            .width(360.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Demo Card", style = MaterialTheme.typography.headlineMedium)
            var isChecked by remember { mutableStateOf(true) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Demo Switch", Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                Switch(checked = isChecked, onCheckedChange = { isChecked = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Person, "Icon", Modifier.size(32.dp))
                Spacer(Modifier.width(8.dp))
                Text("Demo Icon", style = MaterialTheme.typography.bodyLarge)
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    Modifier
                        .width(100.dp)
                        .weight(1f)
                        .height(80.dp)
                        .background(Color.Blue)
                )
                Box(
                    Modifier
                        .width(100.dp)
                        .weight(1f)
                        .height(80.dp)
                        .background(Color.Red)
                )
            }
            Text(
                "Demo Text: Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                    " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
        }
    }
}
// [END android_compose_accessibility_scalable_content_demo_card]