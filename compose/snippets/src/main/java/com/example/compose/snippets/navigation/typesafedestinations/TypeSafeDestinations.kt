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

package com.example.compose.snippets.navigation.typesafedestinations

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

// [START android_compose_navigation_type_safe_destinations_routes]
// import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class Profile(val userId: String)
// [END android_compose_navigation_type_safe_destinations_routes]

private fun navigateToProfile(navController: NavController) {
    // [START android_compose_navigation_type_safe_destinations_navigate]
    navController.navigate(Profile(userId = "user123"))
    // [END android_compose_navigation_type_safe_destinations_navigate]
}
