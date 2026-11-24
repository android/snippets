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

package com.example.example.snippet.views.navigation.nestedgraphs

import android.view.View
import androidx.navigation.findNavController
import com.example.example.snippet.views.R

private fun navigateToNestedGraph(view: View) {
    // [START android_views_navigation_nested_graphs_navigate_action]
    view.findNavController().navigate(R.id.action_mainFragment_to_sendMoneyGraph)
    // [END android_views_navigation_nested_graphs_navigate_action]
}
