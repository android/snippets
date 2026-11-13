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

package androidx.graphics.shapes

import android.graphics.Path

// graphics-shapes only has `toPath()` on Android, where it returns an android.graphics.Path. These
// wasm versions build the same outline from the shape's cubics.
fun RoundedPolygon.toPath(path: Path = Path()): Path = cubics.toPath(path)

fun Morph.toPath(progress: Float, path: Path = Path()): Path = asCubics(progress).toPath(path)

private fun List<Cubic>.toPath(path: Path): Path {
    val composePath = path.composePath
    composePath.rewind()
    firstOrNull()?.let { composePath.moveTo(it.anchor0X, it.anchor0Y) }
    for (bezier in this) {
        composePath.cubicTo(
            bezier.control0X, bezier.control0Y,
            bezier.control1X, bezier.control1Y,
            bezier.anchor1X, bezier.anchor1Y,
        )
    }
    composePath.close()
    return path
}
