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

package android.graphics

// Compile-only stand-ins so graphics/BrushExampleSnippets.kt builds for wasm. Its AGSL
// RuntimeShader snippets need Android 13+ and aren't registered for interactive previews.

class RuntimeShader(@Suppress("UNUSED_PARAMETER") shader: String) {
    fun setFloatUniform(uniformName: String, value1: Float, value2: Float) = Unit

    fun setColorUniform(uniformName: String, color: Color) = Unit
}

class Color private constructor() {
    companion object {
        fun valueOf(red: Float, green: Float, blue: Float, alpha: Float): Color = Color()
    }
}
