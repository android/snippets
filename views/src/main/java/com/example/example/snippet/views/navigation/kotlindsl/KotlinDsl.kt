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

package com.example.example.snippet.views.navigation.kotlindsl

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigator
import androidx.navigation.createGraph
import androidx.navigation.fragment.findNavController
import androidx.navigation.get
import kotlinx.serialization.Serializable

// [START android_views_navigation_kotlin_dsl_routes]
@Serializable data object Home
@Serializable data class Plant(val id: String)
// [END android_views_navigation_kotlin_dsl_routes]

class HomeFragment : Fragment() {
    // [START android_views_navigation_kotlin_dsl_navigate]
    private fun navigateToPlant(plantId: String) {
       findNavController().navigate(route = PlantDetail(id = plantId))
    }
    // [END android_views_navigation_kotlin_dsl_navigate]
}

private fun NavController.addCustomDestination() {
    graph = createGraph(startDestination = Graph.CustomDestination.route) {
        // [START android_views_navigation_kotlin_dsl_custom_destination]
        // The NavigatorProvider is retrieved from the NavController.
        val customDestination = navigatorProvider[CustomNavigator::class].createDestination().apply {
            route = Graph.CustomDestination.route
        }
        addDestination(customDestination)
        // [END android_views_navigation_kotlin_dsl_custom_destination]
    }
}

private fun NavController.addCustomDestinationWithUnaryPlus() {
    graph = createGraph(startDestination = Graph.CustomDestination.route) {
        // [START android_views_navigation_kotlin_dsl_custom_destination_unary_plus]
        // The NavigatorProvider is retrieved from the NavController.
        +navigatorProvider[CustomNavigator::class].createDestination().apply {
            route = Graph.CustomDestination.route
        }
        // [END android_views_navigation_kotlin_dsl_custom_destination_unary_plus]
    }
}

private fun navigateToSearch(navController: NavController) {
    // [START android_views_navigation_kotlin_dsl_custom_type_navigate]
    val params = SearchParameters("rose", listOf("available"))
    navController.navigate(route = SearchRoute(params))
    // [END android_views_navigation_kotlin_dsl_custom_type_navigate]
}

@Serializable
private data class PlantDetail(val id: String)

@Serializable
private data class SearchParameters(val searchQuery: String, val filters: List<String>)

@Serializable
private data class SearchRoute(val parameters: SearchParameters)

private object Graph {
    object CustomDestination {
        const val route = "custom_destination"
    }
}

@Navigator.Name("custom")
private class CustomNavigator : Navigator<NavDestination>() {
    override fun createDestination() = NavDestination(this)
}
