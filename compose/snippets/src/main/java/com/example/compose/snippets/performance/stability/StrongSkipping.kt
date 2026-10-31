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

package com.example.compose.snippets.performance.stability

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DontMemoize
import androidx.compose.runtime.NonSkippableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

private object NonSkippableExample {
    // [START android_compose_stability_strong_skipping_non_skippable]
    @NonSkippableComposable
    @Composable
    fun MyNonSkippableComposable() {}
    // [END android_compose_stability_strong_skipping_non_skippable]
}

private object LambdaUnmemoizedExample {
    // [START android_compose_stability_strong_skipping_lambda_unmemoized]
    @Composable
    fun MyComposable(unstableObject: Unstable, stableObject: Stable) {
        val lambda = {
            use(unstableObject)
            use(stableObject)
        }
    }
    // [END android_compose_stability_strong_skipping_lambda_unmemoized]
}

private object LambdaMemoizedExample {
    // [START android_compose_stability_strong_skipping_lambda_memoized]
    @Composable
    fun MyComposable(unstableObject: Unstable, stableObject: Stable) {
        val lambda = remember(unstableObject, stableObject) {
            {
                use(unstableObject)
                use(stableObject)
            }
        }
    }
    // [END android_compose_stability_strong_skipping_lambda_memoized]
}

@Composable
private fun DontMemoizeExample() {
    // [START android_compose_stability_strong_skipping_dont_memoize]
    val lambda = @DontMemoize {
        // ...
    }
    // [END android_compose_stability_strong_skipping_dont_memoize]
}

private class Unstable {
    var count: Int = 0
}

@Stable
private class Stable

private fun use(value: Any) {}
