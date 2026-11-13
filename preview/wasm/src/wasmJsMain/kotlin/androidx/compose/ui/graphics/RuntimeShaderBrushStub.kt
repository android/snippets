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

// On Android, RuntimeShader is a Shader, so `ShaderBrush(runtimeShader)` compiles. This overload
// lets graphics/BrushExampleSnippets.kt build for wasm against the RuntimeShader stub; the AGSL
// snippet that uses it isn't registered for interactive previews, so it draws nothing.
@Suppress("UNUSED_PARAMETER")
fun ShaderBrush(shader: RuntimeShader): Brush = SolidColor(Color.Transparent)
