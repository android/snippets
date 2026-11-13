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

package androidx.compose.animation.graphics.res

import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

/*
 * Wasm stand-ins for animation-graphics' resource APIs (see ExperimentalAnimationGraphicsApi).
 * Animated vector drawables can't be loaded on wasm, so these draw an empty 24dp vector.
 */

@Suppress("UNUSED_PARAMETER")
@Composable
fun AnimatedImageVector.Companion.animatedVectorResource(id: Int): AnimatedImageVector =
    AnimatedImageVector(
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).build()
    )

@Suppress("UNUSED_PARAMETER")
@Composable
fun rememberAnimatedVectorPainter(animatedImageVector: AnimatedImageVector, atEnd: Boolean): Painter =
    rememberVectorPainter(animatedImageVector.imageVector)
