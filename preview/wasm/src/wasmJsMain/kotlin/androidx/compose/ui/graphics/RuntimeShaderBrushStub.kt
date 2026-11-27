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

package androidx.compose.ui.graphics

import android.graphics.RuntimeShader
import androidx.compose.ui.geometry.Size

/**
 * On Android, `RuntimeShader` is a `Shader`, so `ShaderBrush(runtimeShader)` is the regular
 * factory. On the web, [RuntimeShader] wraps a Skia runtime effect, so this overload builds the
 * Skia shader when the brush is first drawn at a size, after the snippet has set its uniforms.
 */
fun ShaderBrush(shader: RuntimeShader): ShaderBrush = object : ShaderBrush() {
    override fun createShader(size: Size): Shader = shader.makeShader().asComposeShader()
}
