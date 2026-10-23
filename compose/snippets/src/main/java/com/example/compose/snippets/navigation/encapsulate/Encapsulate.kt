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

package com.example.compose.snippets.navigation.encapsulate

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

private object ContactScreens {
    // [START android_compose_navigation_encapsulate_screens]
    // ContactScreens.kt

    // Displays a list of contacts.
    @Composable
    internal fun ContactsScreen(
      uiState: ContactsUiState,
      onNavigateToContactDetails: (contactId: String) -> Unit
    ) { /* ... */ }

    // Displays the details for an individual contact.
    @Composable
    internal fun ContactDetailsScreen(contact: ContactDetails) { /* ... */ }
    // [END android_compose_navigation_encapsulate_screens]
}

// [START android_compose_navigation_encapsulate_navigation_events]
// ContactsNavigation.kt

fun NavController.navigateToContactDetails(id: String) {
  navigate(route = ContactDetails(id = id))
}
// [END android_compose_navigation_encapsulate_navigation_events]

// [START android_compose_navigation_encapsulate_my_app]
// MyApp.kt

@Composable
fun MyApp() {
  // [START_EXCLUDE]
  val navController = rememberNavController()
  // [END_EXCLUDE]
  NavHost(navController, startDestination = Contacts) {
     contactsDestination(onNavigateToContactDetails = { contactId ->
        navController.navigateToContactDetails(id = contactId)
     })
     contactDetailsDestination()
  }
}
// [END android_compose_navigation_encapsulate_my_app]

@Serializable
private object Contacts

@Serializable
private data class ContactDetails(val id: String)

private class ContactsUiState

private fun NavGraphBuilder.contactsDestination(
    onNavigateToContactDetails: (contactId: String) -> Unit
) {
    composable<Contacts> {
        ContactScreens.ContactsScreen(ContactsUiState(), onNavigateToContactDetails)
    }
}

private fun NavGraphBuilder.contactDetailsDestination() {
    composable<ContactDetails> { navBackStackEntry ->
        ContactScreens.ContactDetailsScreen(contact = navBackStackEntry.toRoute())
    }
}
