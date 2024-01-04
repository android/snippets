package com.example.compose.snippets.graphics

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Cubic
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import com.example.compose.snippets.R
import kotlin.math.min


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
            repeatMode = RepeatMode.Reverse
        ),
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
// [START android_compose_morph_clip_shape]
class MorphPolygonShape(
    private val morph: Morph,
    private val percentage: Float
) : Shape {

    private val matrix = Matrix()
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        // Below assumes that you haven't changed the default radius of 1f, nor the centerX and centerY of 0f
        // By default this stretches the path to the size of the container, if you don't want stretching, use the same size.width for both x and y.
        matrix.scale(size.width / 2f, size.height / 2f)
        matrix.translate(1f, 1f)

        val path = morph.toComposePath(progress = percentage)
        path.transform(matrix)
        return Outline.Generic(path)
    }
}
// [END android_compose_morph_clip_shape]
@Preview
@Composable
private fun MorphOnClick() {
    // [START android_compose_graphics_morph_on_click]
    val shapeA = remember {
        RoundedPolygon(
            6,
            rounding = CornerRounding(0.2f)
        )
    }
    val shapeB = remember {
        RoundedPolygon.star(
            6,
            rounding = CornerRounding(0.1f)
        )
    }
    val morph = remember {
        Morph(shapeA, shapeB)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedProgress = animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        label = "progress",
        animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium)
    )
    Box(modifier = Modifier
        .size(200.dp)
        .padding(8.dp)
        .clip(MorphPolygonShape(morph, animatedProgress.value))
        .background(Color(0xFF80DEEA))
        .size(200.dp)
        .clickable(interactionSource = interactionSource, indication = null) {
        }
    ) {
        Text("Hello", modifier = Modifier.align(Alignment.Center))
    }
    // [END android_compose_graphics_morph_on_click]
}


// [START android_compose_shapes_polygon_compose_shape]
class RoundedPolygonShape(
    private val polygon: RoundedPolygon
) : Shape {
    private val matrix = Matrix()
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = polygon.cubics.toPath()
        // below assumes that you haven't changed the default radius of 1f, nor the centerX and centerY of 0f
        // By default this stretches the path to the size of the container, if you don't want stretching, use the same size.width for both x and y.
        matrix.scale(size.width / 2f, size.height / 2f)
        matrix.translate(1f, 1f)
        path.transform(matrix)

        return Outline.Generic(path)
    }
}
// [END android_compose_shapes_polygon_compose_shape]

@Preview
@Composable
fun ApplyPolygonAsClipBasic() {
    // [START android_compose_shapes_apply_as_clip]
    val hexagon = remember {
        RoundedPolygon(
            6,
            rounding = CornerRounding(0.2f)
        )
    }
    val clip = remember(hexagon) {
        RoundedPolygonShape(polygon = hexagon)
    }
    Box(
        modifier = Modifier
            .clip(clip)
            .background(MaterialTheme.colorScheme.secondary)
            .size(200.dp)
    ) {
        Text(
            "Hello Compose",
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
    // [END android_compose_shapes_apply_as_clip]
}

@Preview
@Composable
fun ApplyPolygonAsClipImage() {
    // [START android_compose_shapes_apply_as_clip_advanced]
    val hexagon = remember {
        RoundedPolygon(
            6,
            rounding = CornerRounding(0.2f)
        )
    }
    val clip = remember(hexagon) {
        RoundedPolygonShape(polygon = hexagon)
    }
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.dog),
            contentDescription = "Dog",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .graphicsLayer {
                    this.shadowElevation = 6.dp.toPx()
                    this.shape = clip
                    this.clip = true
                    this.ambientShadowColor = Color.Black
                    this.spotShadowColor = Color.Black
                }
                .size(200.dp)

        )
    }
    // [END android_compose_shapes_apply_as_clip_advanced]
}

// [START android_compose_shapes_custom_rotating_morph_shape]
class CustomRotatingMorphShape(
    private val morph: Morph,
    private val percentage: Float,
    private val rotation: Float
) : Shape {

    private val matrix = Matrix()
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        // Below assumes that you haven't changed the default radius of 1f, nor the centerX and centerY of 0f
        // By default this stretches the path to the size of the container, if you don't want stretching, use the same size.width for both x and y.
        matrix.scale(size.width / 2f, size.height / 2f)
        matrix.translate(1f, 1f)
        matrix.rotateZ(rotation)

        val path = morph.toComposePath(progress = percentage)
        path.transform(matrix)

        return Outline.Generic(path)
    }
}
@Preview
@Composable
private fun RotatingScallopedProfilePic() {
    val shapeA = remember {
        RoundedPolygon(
            12,
            rounding = CornerRounding(0.2f)
        )
    }
    val shapeB = remember {
        RoundedPolygon.star(
            12,
            rounding = CornerRounding(0.2f)
        )
    }
    val morph = remember {
        Morph(shapeA, shapeB)
    }
    val infiniteTransition = rememberInfiniteTransition("infinite outline movement")
    val animatedProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "animatedMorphProgress"
    )
    val animatedRotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "animatedMorphProgress"
    )
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.dog),
            contentDescription = "Dog",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CustomRotatingMorphShape(morph, animatedProgress.value, animatedRotation.value))
                .size(200.dp)
        )
    }
}
// [END android_compose_shapes_custom_rotating_morph_shape]