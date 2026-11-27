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

import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder
import org.jetbrains.skia.Shader

/**
 * Web version of Android's AGSL `RuntimeShader`, backed by Skia's runtime effects. AGSL is based
 * on SkSL, so the same shader source compiles here. Uniforms set with [setFloatUniform] and
 * [setColorUniform] are applied when the shader is built (see `ShaderBrush(RuntimeShader)`).
 */
class RuntimeShader(shader: String) {
    private val builder = RuntimeShaderBuilder(RuntimeEffect.makeForShader(shader))

    fun setFloatUniform(uniformName: String, value: Float) {
        builder.uniform(uniformName, value)
    }

    fun setFloatUniform(uniformName: String, value1: Float, value2: Float) {
        builder.uniform(uniformName, value1, value2)
    }

    fun setFloatUniform(uniformName: String, value1: Float, value2: Float, value3: Float) {
        builder.uniform(uniformName, value1, value2, value3)
    }

    fun setFloatUniform(
        uniformName: String,
        value1: Float,
        value2: Float,
        value3: Float,
        value4: Float,
    ) {
        builder.uniform(uniformName, value1, value2, value3, value4)
    }

    fun setColorUniform(uniformName: String, color: Color) {
        builder.uniform(uniformName, color.red(), color.green(), color.blue(), color.alpha())
    }

    /** Builds a Skia shader with the uniforms set so far. */
    fun makeShader(): Shader = builder.makeShader()
}

/** The subset of Android's `Color` that the shared snippets use to pass colors to shaders. */
class Color private constructor(
    private val red: Float,
    private val green: Float,
    private val blue: Float,
    private val alpha: Float,
) {
    fun red(): Float = red

    fun green(): Float = green

    fun blue(): Float = blue

    fun alpha(): Float = alpha

    companion object {
        fun valueOf(red: Float, green: Float, blue: Float, alpha: Float): Color =
            Color(red, green, blue, alpha)
    }
}
