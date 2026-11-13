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

package com.example.example.snippet.views.navigation.navoptions

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.example.snippet.views.R

private fun navigateWithAction(navController: NavController) {
    // [START android_views_navigation_navoptions_navigate_action]
    navController.navigate(R.id.action_b_to_a)
    // [END android_views_navigation_navoptions_navigate_action]
}

private fun Fragment.navigateWithProgrammaticOptions() {
    // [START android_views_navigation_navoptions_programmatic]
    findNavController().navigate(
        R.id.action_fragmentOne_to_fragmentTwo,
        null,
        navOptions { // Use the Kotlin DSL for building NavOptions.
            anim {
                enter = android.R.animator.fade_in
                exit = android.R.animator.fade_out
            }
        }
    )
    // [END android_views_navigation_navoptions_programmatic]
}

private fun Fragment.navigateToDeepLink(deepLinkUri: Uri) {
    // [START android_views_navigation_navoptions_deep_link]
    findNavController().navigate(
        deepLinkUri,
        navOptions { // Use the Kotlin DSL for building NavOptions.
            anim {
                enter = android.R.animator.fade_in
                exit = android.R.animator.fade_out
            }
        }
    )
    // [END android_views_navigation_navoptions_deep_link]
}
