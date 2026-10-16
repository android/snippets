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

package com.example.compose.snippets.resources

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import com.example.compose.snippets.R

private fun loadPropertyAnimatorExample(myContext: Context, myObject: Any) {
    // [START android_resources_animation_load_animator]
    val set: AnimatorSet =
        (AnimatorInflater.loadAnimator(myContext, R.animator.property_animator) as AnimatorSet)
            .apply {
                setTarget(myObject)
                start()
            }
    // [END android_resources_animation_load_animator]
}
