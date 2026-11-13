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

package androidx.compose.animation.graphics

// androidx.compose.animation:animation-graphics has no Compose Multiplatform wasm artifact. These
// stand-ins let AnimationSnippets.kt compile on wasm; its animated vector drawable snippet isn't
// registered for a preview.
@RequiresOptIn
annotation class ExperimentalAnimationGraphicsApi
