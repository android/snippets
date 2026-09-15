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

package com.example.compose.snippets.navigation3.results

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.result.LocalResultEventBus
import androidx.navigation3.runtime.result.ResultEffect
import androidx.navigation3.runtime.result.rememberResultEventBus
import androidx.navigation3.runtime.result.rememberResultEventBusNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable

// Domain models & Routes
@Serializable
private data object HomeScreenRoute : NavKey

@Serializable
private data object ContactPickerRoute : NavKey

@Serializable
private data object AddressPickerRoute : NavKey

@Serializable
data class Contact(val name: String = "", val email: String = "")

@Serializable
data class Address(val street: String = "", val city: String = "")

enum class ProductFilter {
    All,
    Electronics,
    Books
}

@Serializable
data class ConfirmationResult(val confirmed: Boolean = true)

class ComposeMessageViewModel : ViewModel() {
    var recipient: Contact? = null
    fun onRecipientSelected(contact: Contact) {
        recipient = contact
    }
}

class RideSummaryViewModel : ViewModel() {
    var pickupAddress: Address? = null
    var destinationAddress: Address? = null
    fun onPickupAddressSelected(address: Address) {
        pickupAddress = address
    }
    fun onDestinationAddressSelected(address: Address) {
        destinationAddress = address
    }
}

class NotificationViewModel : ViewModel() {
    fun onPermissionConfirmed(confirmation: ConfirmationResult) {}
}

class Navigator {
    fun goBack() {}
}

@Composable
fun ContactPicker(onSelect: (Contact) -> Unit) {}

@Composable
fun AddressPicker(onSelect: (Address) -> Unit) {}

@Composable
fun AddressPickerScreen(onAddressSelected: (Address) -> Unit) {
    AddressPicker(onSelect = onAddressSelected)
}

@Composable
fun ContactPickerScreen(onContactSelected: (Contact) -> Unit) {
    ContactPicker(onSelect = onContactSelected)
}

@Composable
fun ComposeMessageContent(recipient: Contact?, onPickContact: () -> Unit) {}

@Composable
fun RideSummaryContent(
    pickupAddress: Address?,
    destinationAddress: Address?,
    onPickPickup: () -> Unit,
    onPickDestination: () -> Unit
) {}

@Composable
fun ProductListContent(activeFilter: ProductFilter, onOpenFilterPicker: () -> Unit) {}

@Composable
fun ThemePreviewContent(
    primaryColor: Color,
    accentColor: Color,
    onPickPrimary: () -> Unit,
    onPickAccent: () -> Unit
) {}

private object BasicResultSnippet {
    @Composable
    fun ResultNavDisplay() {
        // [START android_compose_navigation3_result_basic]
        NavDisplay(
            /* ... */
            // [START_EXCLUDE silent]
            backStack = rememberNavBackStack(HomeScreenRoute),
            entryProvider = entryProvider {
                entry<HomeScreenRoute> { }
            },
            // [END_EXCLUDE]
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberResultEventBusNavEntryDecorator()
            )
        )
        // [END android_compose_navigation3_result_basic]
    }
}

private object SendResultKeySnippet {
    /*
    // [START android_compose_navigation3_result_send_key]
    import androidx.compose.runtime.Composable
    import androidx.navigation3.runtime.result.LocalResultEventBus
    // [START_EXCLUDE silent]
    */
    fun EntryProviderScope<NavKey>.addressPickerEntry(navigator: Navigator) {
    // [END_EXCLUDE]

    entry<AddressPickerRoute> {
        val resultBus = LocalResultEventBus.current

        AddressPickerScreen(
            onAddressSelected = { selectedAddress: Address ->
                resultBus.sendResult(
                    resultKey = "pickup_address",
                    result = selectedAddress
                )
                navigator.goBack()
            }
        )
    }
    // [END android_compose_navigation3_result_send_key]
    }
}

private object SendResultTypeSnippet {
    /*
    // [START android_compose_navigation3_result_send_type]
    import androidx.compose.runtime.Composable
    import androidx.navigation3.runtime.result.LocalResultEventBus
    // [START_EXCLUDE silent]
    */
    fun EntryProviderScope<NavKey>.contactPickerEntry(navigator: Navigator) {
    // [END_EXCLUDE]

    entry<ContactPickerRoute> {
        val resultBus = LocalResultEventBus.current

        ContactPickerScreen(
            onContactSelected = { selectedContact: Contact ->
                resultBus.sendResult(result = selectedContact)
                navigator.goBack()
            }
        )
    }
    // [END android_compose_navigation3_result_send_type]
    }
}

private object ReceiveEffectKeySnippet {
    /*
    // [START android_compose_navigation3_result_effect_key]
    import androidx.compose.runtime.Composable
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation3.runtime.result.ResultEffect
    // [START_EXCLUDE silent]
    */
    // [END_EXCLUDE]

    @Composable
    fun RideSummaryScreen(
        onOpenAddressPicker: (key: String) -> Unit,
        viewModel: RideSummaryViewModel = viewModel()
    ) {
        ResultEffect<Address>(resultKey = "pickup_address") { address ->
            viewModel.onPickupAddressSelected(address)
        }

        ResultEffect<Address>(resultKey = "destination_address") { address ->
            viewModel.onDestinationAddressSelected(address)
        }

        RideSummaryContent(
            pickupAddress = viewModel.pickupAddress,
            destinationAddress = viewModel.destinationAddress,
            onPickPickup = { onOpenAddressPicker("pickup_address") },
            onPickDestination = { onOpenAddressPicker("destination_address") }
        )
    }
    // [END android_compose_navigation3_result_effect_key]
}

private object ReceiveEffectTypeSnippet {
    /*
    // [START android_compose_navigation3_result_effect_type]
    import androidx.compose.material3.SnackbarHostState
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.remember
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation3.runtime.result.ResultEffect
    // [START_EXCLUDE silent]
    */
    // [END_EXCLUDE]

    @Composable
    fun ComposeMessageScreen(
        onPickContact: () -> Unit,
        snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
        viewModel: ComposeMessageViewModel = viewModel()
    ) {
        ResultEffect<Contact> { contact ->
            // Suspending calls are supported directly in the effect body
            snackbarHostState.showSnackbar("Selected ${contact.name}")
            viewModel.onRecipientSelected(contact)
        }

        ComposeMessageContent(
            recipient = viewModel.recipient,
            onPickContact = onPickContact
        )
    }
    // [END android_compose_navigation3_result_effect_type]
}

private object ReceiveStateKeySnippet {
    /*
    // [START android_compose_navigation3_result_state_key]
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.graphics.Color
    import androidx.navigation3.runtime.result.LocalResultEventBus
    // [START_EXCLUDE silent]
    */
    // [END_EXCLUDE]

    @Composable
    fun ThemePreviewScreen(
        onOpenColorPicker: (key: String) -> Unit
    ) {
        val resultBus = LocalResultEventBus.current

        val primaryColor by resultBus.conflateAsState<Color>(
            resultKey = "primary_color",
            defaultValue = MaterialTheme.colorScheme.primary
        )

        val accentColor by resultBus.conflateAsState<Color>(
            resultKey = "accent_color",
            defaultValue = MaterialTheme.colorScheme.tertiary
        )

        ThemePreviewContent(
            primaryColor = primaryColor,
            accentColor = accentColor,
            onPickPrimary = { onOpenColorPicker("primary_color") },
            onPickAccent = { onOpenColorPicker("accent_color") }
        )
    }
    // [END android_compose_navigation3_result_state_key]
}

private object ReceiveStateTypeSnippet {
    /*
    // [START android_compose_navigation3_result_state_type]
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.getValue
    import androidx.navigation3.runtime.result.LocalResultEventBus
    // [START_EXCLUDE silent]
    */
    // [END_EXCLUDE]

    @Composable
    fun FilterableProductListScreen(
        initialFilter: ProductFilter = ProductFilter.All,
        onOpenFilterPicker: () -> Unit
    ) {
        val resultBus = LocalResultEventBus.current

        // Observe latest filter result as Compose State, starting with initialFilter
        val activeFilter by resultBus.conflateAsState<ProductFilter>(
            defaultValue = initialFilter
        )

        ProductListContent(
            activeFilter = activeFilter,
            onOpenFilterPicker = onOpenFilterPicker
        )
    }
    // [END android_compose_navigation3_result_state_type]
}

private object HoistedBusSnippet {
    /*
    // [START android_compose_navigation3_result_hoist]
    import androidx.compose.runtime.Composable
    import androidx.navigation3.runtime.result.rememberResultEventBus
    import androidx.navigation3.runtime.result.rememberResultEventBusNavEntryDecorator
    import androidx.navigation3.ui.NavDisplay
    // [START_EXCLUDE silent]
    */
    @Composable
    fun HoistedResultNavigation() {
    // [END_EXCLUDE]

    // Hoist the ResultEventBus at the top level
    val resultEventBus = rememberResultEventBus()

    // Pass the hoisted bus to the decorator
    val resultEventBusNavEntryDecorator =
        rememberResultEventBusNavEntryDecorator<NavKey>(
            resultEventBus = resultEventBus
        )

    NavDisplay(
        /* ... */
        // [START_EXCLUDE silent]
        backStack = rememberNavBackStack(HomeScreenRoute),
        entryProvider = entryProvider<NavKey> {
            entry<HomeScreenRoute> { }
        },
        // [END_EXCLUDE]
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            resultEventBusNavEntryDecorator
        )
    )
    // [END android_compose_navigation3_result_hoist]
    }
}

private object ClearResultSnippet {
    /*
    // [START android_compose_navigation3_result_clear]
    import androidx.compose.runtime.Composable
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation3.runtime.result.LocalResultEventBus
    import androidx.navigation3.runtime.result.ResultEffect
    // [START_EXCLUDE silent]
    */
    // [END_EXCLUDE]

    @Composable
    fun NotificationSettingsScreen(
        viewModel: NotificationViewModel = viewModel()
    ) {
        val resultBus = LocalResultEventBus.current

        ResultEffect<ConfirmationResult>(resultKey = "confirm_permission") { confirmation ->
            viewModel.onPermissionConfirmed(confirmation)

            // Clear the result after consumption to prevent re-delivery
            resultBus.removeResult(resultKey = "confirm_permission")
        }
    }
    // [END android_compose_navigation3_result_clear]
}
