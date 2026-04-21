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

package com.example.compose.snippets.adaptivelayouts

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.RequiresWindowSdkExtension
import androidx.window.WindowSdkExtensions
import androidx.window.layout.FoldingFeature
import androidx.window.layout.SupportedPosture
import androidx.window.layout.WindowInfoTracker
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// [START android_adaptive_fold_aware_flows]
class DisplayFeaturesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // [START_EXCLUDE]
        super.onCreate(savedInstanceState)
        // [END_EXCLUDE]

        lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                WindowInfoTracker.getOrCreate(this@DisplayFeaturesActivity)
                    .windowLayoutInfo(this@DisplayFeaturesActivity)
                    .collect { newLayoutInfo ->
                        // Use newLayoutInfo to update the layout.
                    }
            }
        }
    }
}
// [END android_adaptive_fold_aware_flows]

class MainActivity : ComponentActivity() {

    // [START android_adaptive_fold_feature]
    override fun onCreate(savedInstanceState: Bundle?) {
        // [START_EXCLUDE]
        super.onCreate(savedInstanceState)
        // [END_EXCLUDE]
        lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Safely collects from WindowInfoTracker when the lifecycle is
                // STARTED and stops collection when the lifecycle is STOPPED.
                WindowInfoTracker.getOrCreate(this@MainActivity)
                    .windowLayoutInfo(this@MainActivity)
                    .collect { layoutInfo ->
                        // New posture information.
                        val foldingFeature = layoutInfo.displayFeatures
                            .filterIsInstance<FoldingFeature>()
                            .firstOrNull()
                        // Use information from the foldingFeature object.
                    }
            }
        }
    }
    // [END android_adaptive_fold_feature]
}

@OptIn(ExperimentalContracts::class)
// [START android_adaptive_tabletop_posture]
fun isTableTopPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
        foldFeature.orientation == FoldingFeature.Orientation.HORIZONTAL
}
// [END android_adaptive_tabletop_posture]

@RequiresWindowSdkExtension(6)
fun checkSupportedPostures(context: Context) {
    // [START android_adaptive_supported_postures]
    if (WindowSdkExtensions.getInstance().extensionVersion >= 6) {
        val postures = WindowInfoTracker.getOrCreate(context).supportedPostures
        if (postures.contains(SupportedPosture.TABLETOP)) {
            // Device supports tabletop posture.
        }
    }
    // [END android_adaptive_supported_postures]
}

@OptIn(ExperimentalContracts::class)
// [START android_adaptive_book_posture]
fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
        foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}
// [END android_adaptive_book_posture]
