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

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

class CheckoutViewModel : ViewModel() {
    var pickupAddress: Address? = null
    fun onPickupAddressSelected(address: Address) {
        pickupAddress = address
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
fun ComposeMessageContent(recipient: Contact?, onPickContact: () -> Unit) {}

@Composable
fun CheckoutContent(pickupAddress: Address?, onSelectAddress: () -> Unit) {}

@Composable
fun ProductListContent(activeFilter: ProductFilter, onOpenFilterPicker: () -> Unit) {}

@Composable
fun OrderSummaryContent(pickupAddress: Address, onSelectAddress: () -> Unit) {}

private object BasicResultSnippet {
    @Composable
    fun ResultNavDisplay() {
        // [START android_compose_navigation3_result_basic]
        NavDisplay(
            /* ... */
            // [START_EXCLUDE]
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

private object SendResultTypeSnippet {
    // [START android_compose_navigation3_result_send_type]
    // Pure, decoupled screen composable
    @Composable
    fun ContactPickerScreen(
        onContactSelected: (Contact) -> Unit
    ) {
        ContactPicker(onSelect = onContactSelected)
    }

    // [START_EXCLUDE]
    fun EntryProviderScope<NavKey>.contactPickerEntry(navigator: Navigator) {
    // [END_EXCLUDE]
        // In your entryProvider:
        entry<ContactPickerRoute> {
            val resultBus = LocalResultEventBus.current

            ContactPickerScreen(
                onContactSelected = { selectedContact ->
                    // Send result by type
                    resultBus.sendResult<Contact>(result = selectedContact)
                    navigator.goBack()
                }
            )
        }
    // [START_EXCLUDE]
    }
    // [END_EXCLUDE]
    // [END android_compose_navigation3_result_send_type]
}

private object SendResultKeySnippet {
    // [START android_compose_navigation3_result_send_key]
    // Pure, decoupled screen composable
    @Composable
    fun AddressPickerScreen(
        onAddressSelected: (Address) -> Unit
    ) {
        AddressPicker(onSelect = onAddressSelected)
    }

    // [START_EXCLUDE]
    fun EntryProviderScope<NavKey>.addressPickerEntry(navigator: Navigator) {
    // [END_EXCLUDE]
        // In your entryProvider:
        entry<AddressPickerRoute> {
            val resultBus = LocalResultEventBus.current

            AddressPickerScreen(
                onAddressSelected = { selectedAddress ->
                    // Send result with an explicit key
                    resultBus.sendResult<Address>(
                        resultKey = "pickup_address",
                        result = selectedAddress
                    )
                    navigator.goBack()
                }
            )
        }
    // [START_EXCLUDE]
    }
    // [END_EXCLUDE]
    // [END android_compose_navigation3_result_send_key]
}

private object ReceiveEffectTypeSnippet {
    // [START android_compose_navigation3_result_effect_type]
    @Composable
    fun ComposeMessageScreen(
        onPickContact: () -> Unit,
        snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
        viewModel: ComposeMessageViewModel = viewModel()
    ) {
        val resultBus = LocalResultEventBus.current

        ResultEffect<Contact> { contact ->
            // Suspending calls are supported directly in the effect body
            snackbarHostState.showSnackbar("Selected ${contact.name}")
            viewModel.onRecipientSelected(contact)

            // Clear after consumption to prevent re-delivery on back-stack re-entry
            resultBus.removeResult<Contact>()
        }

        ComposeMessageContent(
            recipient = viewModel.recipient,
            onPickContact = onPickContact
        )
    }
    // [END android_compose_navigation3_result_effect_type]
}

private object ReceiveEffectKeySnippet {
    // [START android_compose_navigation3_result_effect_key]
    @Composable
    fun CheckoutScreen(
        onSelectAddress: () -> Unit,
        viewModel: CheckoutViewModel = viewModel()
    ) {
        // Listen for results associated with a specific key
        ResultEffect<Address>(resultKey = "pickup_address") { address ->
            viewModel.onPickupAddressSelected(address)
        }

        CheckoutContent(
            pickupAddress = viewModel.pickupAddress,
            onSelectAddress = onSelectAddress
        )
    }
    // [END android_compose_navigation3_result_effect_key]
}

private object ReceiveStateTypeSnippet {
    // [START android_compose_navigation3_result_state_type]
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

private object ReceiveStateKeySnippet {
    // [START android_compose_navigation3_result_state_key]
    @Composable
    fun OrderSummaryScreen(
        defaultAddress: Address,
        onSelectAddress: () -> Unit
    ) {
        val resultBus = LocalResultEventBus.current

        // Observe latest result with an explicit key as Compose State
        val pickupAddress by resultBus.conflateAsState<Address>(
            resultKey = "pickup_address",
            defaultValue = defaultAddress
        )

        OrderSummaryContent(
            pickupAddress = pickupAddress,
            onSelectAddress = onSelectAddress
        )
    }
    // [END android_compose_navigation3_result_state_key]
}

private object HoistedBusSnippet {
    // [START android_compose_navigation3_result_hoist]
    @Composable
    fun HoistedResultNavigation() {
        // Hoist the ResultEventBus at the top level
        val resultEventBus = rememberResultEventBus()

        // Pass the hoisted bus to the decorator
        val resultEventBusNavEntryDecorator =
            rememberResultEventBusNavEntryDecorator<NavKey>(
                resultEventBus = resultEventBus
            )

        NavDisplay(
            /* ... */
            // [START_EXCLUDE]
            backStack = rememberNavBackStack(HomeScreenRoute),
            entryProvider = entryProvider {
                entry<HomeScreenRoute> { }
            },
            // [END_EXCLUDE]
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                resultEventBusNavEntryDecorator
            )
        )
    }
    // [END android_compose_navigation3_result_hoist]
}

private object ClearResultSnippet {
    // [START android_compose_navigation3_result_clear]
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
