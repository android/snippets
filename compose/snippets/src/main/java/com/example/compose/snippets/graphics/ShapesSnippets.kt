package com.example.compose.snippets.graphics

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Cubic
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon


@Preview
@Composable
fun BasicShapeCanvas() {
    // [START android_compose_graphics_basic_polygon]
    Box(modifier = Modifier
        .drawWithCache {
            val roundedPolygon = RoundedPolygon(
                numVertices = 6,
                radius = size.width / 2,
                centerX = size.width / 2,
                centerY = size.height / 2
            )
            val roundedPolygonPath = roundedPolygon.cubics
                .toPath()
            onDrawBehind {
                drawPath(roundedPolygonPath, color = Color.Blue)
            }
        }
        .fillMaxSize())
    // [END android_compose_graphics_basic_polygon]
}

@Preview
@Composable
private fun RoundedShapeExample() {
    // [START android_compose_graphics_polygon_rounding]
    Box(modifier = Modifier
        .drawWithCache {
            val roundedPolygon = RoundedPolygon(
                numVertices = 3,
                radius = size.width / 2,
                centerX = size.width / 2,
                centerY = size.height / 2,
                rounding = CornerRounding(
                    size.width / 10f,
                    smoothing = 1f
                )
            )
            val roundedPolygonPath = roundedPolygon.cubics
                .toPath()
            onDrawBehind {
                drawPath(roundedPolygonPath, color = Color.Black)
            }
        }
        .fillMaxSize())
    // [END android_compose_graphics_polygon_rounding]
}

@Preview
@Composable
private fun RoundedShapeSmoothnessExample() {
    // [START android_compose_graphics_polygon_rounding_smooth]
    Box(modifier = Modifier
        .drawWithCache {
            val roundedPolygon = RoundedPolygon(
                numVertices = 3,
                radius = size.width / 2,
                centerX = size.width / 2,
                centerY = size.height / 2,
                rounding = CornerRounding(
                    size.width / 10f,
                    smoothing = 0.1f
                )
            )
            val roundedPolygonPath = roundedPolygon.cubics
                .toPath()
            onDrawBehind {
                drawPath(roundedPolygonPath, color = Color.Black)
            }
        }
        .size(100.dp))

    // [END android_compose_graphics_polygon_rounding_smooth]
}

@Preview
@Composable
private fun MorphExample() {
    // [START android_compose_graphics_polygon_morph]
    Box(modifier = Modifier
        .drawWithCache {
            val triangle = RoundedPolygon(
                numVertices = 3,
                radius = size.width / 2f,
                centerX = size.width / 2f,
                centerY = size.height / 2f,
                rounding = CornerRounding(
                    size.width / 10f,
                    smoothing = 0.1f
                )
            )
            val square = RoundedPolygon(
                numVertices = 4,
                radius = size.width / 2f,
                centerX = size.width / 2f,
                centerY = size.height / 2f
            )

            val morph = Morph(start = triangle, end = square)
            val morphPath = morph
                .toComposePath(progress = 0.5f)

            onDrawBehind {
                drawPath(morphPath, color = Color.Black)
            }
        }
        .fillMaxSize())
    // [END android_compose_graphics_polygon_morph]
}

@Preview
@Composable
private fun MorphExampleAnimation() {
    // [START android_compose_graphics_polygon_morph_animation]
    val infiniteAnimation = rememberInfiniteTransition(label = "infinite animation")
    val morphProgress = infiniteAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(500),
            repeatMode = RepeatMode.Reverse),
        label = "morph"
    )
    Box(modifier = Modifier
        .drawWithCache {
            val triangle = RoundedPolygon(
                numVertices = 3,
                radius = size.width / 2f,
                centerX = size.width / 2f,
                centerY = size.height / 2f,
                rounding = CornerRounding(
                    size.width / 10f,
                    smoothing = 0.1f
                )
            )
            val square = RoundedPolygon(
                numVertices = 4,
                radius = size.width / 2f,
                centerX = size.width / 2f,
                centerY = size.height / 2f
            )

            val morph = Morph(start = triangle, end = square)
            // todo ensure proper caching here.
            val morphPath = morph
                .toComposePath(progress = morphProgress.value)

            onDrawBehind {
                drawPath(morphPath, color = Color.Black)
            }
        }
        .fillMaxSize())
    // [END android_compose_graphics_polygon_morph_animation]
}
/**
 * Transforms the morph at a given progress into a [Path].
 * It can optionally be scaled, using the origin (0,0) as pivot point.
 */
fun Morph.toComposePath(progress: Float, scale: Float = 1f, path: Path = Path()): Path {
    var first = true
    path.rewind()
    forEachCubic(progress) { bezier ->
        if (first) {
            path.moveTo(bezier.anchor0X * scale, bezier.anchor0Y * scale)
            first = false
        }
        path.cubicTo(
            bezier.control0X * scale, bezier.control0Y * scale,
            bezier.control1X * scale, bezier.control1Y * scale,
            bezier.anchor1X * scale, bezier.anchor1Y * scale
        )
    }
    path.close()
    return path
}

/**
 * Function used to create a Path from a list of Cubics.
 */
fun List<Cubic>.toPath(path: Path = Path()): Path {
    path.rewind()
    firstOrNull()?.let { first ->
        path.moveTo(first.anchor0X, first.anchor0Y)
    }
    for (bezier in this) {
        path.cubicTo(
            bezier.control0X, bezier.control0Y,
            bezier.control1X, bezier.control1Y,
            bezier.anchor1X, bezier.anchor1Y
        )
    }
    path.close()
    return path
}

class MorphPolygonShape(
    private val morph: Morph,
    private val percentage: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        /*val matrixA = calculateMatrix(shapeA.bounds, size.width, size.height)
        shapeA.transform(matrixA)
        val matrixB = calculateMatrix(shapeB.bounds, size.width, size.height)
        shapeB.transform(matrixB)
        val morph = Morph(shapeA, shapeB)
        morph.progress = percentage*/
        return Outline.Generic(morph.toComposePath(percentage))
    }
}
@Preview
@Composable
private fun MorphOnClick() {
    // [START android_compose_graphics_morph_on_click]
    val shapeA = remember {
        RoundedPolygon(5,
            rounding = CornerRounding(0.2f))
    }
    val shapeB = remember {
        RoundedPolygon(3,
            rounding = CornerRounding(0.3f))
    }
    val morph = remember {
        Morph(shapeA, shapeB)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedProgress = animateFloatAsState(targetValue = if (isPressed) 1f else 0f,
        label = "progress", animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium)
    )
    Box(modifier = Modifier
        .padding(8.dp)
        .clip(MorphPolygonShape(morph, animatedProgress.value))
        .background(Color(0xFF80DEEA))
        .size(200.dp)
        .clickable(interactionSource = interactionSource, indication = null) {
        }
    ) {
    }
    // [END android_compose_graphics_morph_on_click]
}

@Preview
@Composable
private fun ApplyPolygonAsClip() {

}