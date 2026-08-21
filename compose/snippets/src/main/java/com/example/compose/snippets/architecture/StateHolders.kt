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

package com.example.compose.snippets.architecture

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private object StateHoldersSnippet1 {
    // [START android_architecture_stateholders_ui_state_simple_counter]
    @Composable
    fun Counter() {
        // The UI state is managed by the UI itself
        var count by remember { mutableStateOf(0) }
        Row {
            Button(onClick = { ++count }) {
                Text(text = "Increment")
            }
            Button(onClick = { --count }) {
                Text(text = "Decrement")
            }
        }
    }
    // [END android_architecture_stateholders_ui_state_simple_counter]
}

private object StateHoldersSnippet2 {
    data class Contact(val name: String)
    @Composable private fun ScrollToTopButton() {}

    // [START android_architecture_stateholders_ui_logic_contacts_list]
    @Composable
    fun ContactsList(contacts: List<Contact>) {
        val listState = rememberLazyListState()
        val isAtTopOfList by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex < 3
            }
        }

        // Create the LazyColumn with the lazyListState
        // ...
        /* [START_EXCLUDE silent] */
        LazyColumn(state = listState) {
            items(contacts) { Text(it.name) }
        }
        /* [END_EXCLUDE] */

        // Show or hide the button (UI logic) based on the list scroll position
        AnimatedVisibility(visible = !isAtTopOfList) {
            ScrollToTopButton()
        }
    }
    // [END android_architecture_stateholders_ui_logic_contacts_list]
}

private object StateHoldersSnippet3 {
    data class UserProfileUiState(val profilePicture: String = "")
    class UserProfileViewModel : ViewModel() {
        val uiState = MutableStateFlow(UserProfileUiState())
    }
    @Composable private fun UserAvatar(picture: String) {}

    // [START android_architecture_stateholders_business_logic_user_profile]
    @Composable
    fun UserProfileScreen(viewModel: UserProfileViewModel = hiltViewModel()) {
        // Read screen UI state from the business logic state holder
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        // Call on the UserAvatar Composable to display the photo
        UserAvatar(picture = uiState.profilePicture)
    }
    // [END android_architecture_stateholders_business_logic_user_profile]
}

private object StateHoldersSnippet4 {
    data class Contact(val name: String)
    data class ContactsUiState(
        val contacts: List<Contact> = emptyList(),
        val deepLinkedContact: Contact? = null
    )
    class ContactsViewModel : ViewModel() {
        val uiState = MutableStateFlow(ContactsUiState())
    }

    // [START android_architecture_stateholders_business_and_ui_logic_contacts]
    @Composable
    fun ContactsList(viewModel: ContactsViewModel = hiltViewModel()) {
        // Read screen UI state from the business logic state holder
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val contacts = uiState.contacts
        val deepLinkedContact = uiState.deepLinkedContact

        val listState = rememberLazyListState()

        // Create the LazyColumn with the lazyListState
        // ...
        /* [START_EXCLUDE silent] */
        LazyColumn(state = listState) {
            items(contacts) { Text(it.name) }
        }
        /* [END_EXCLUDE] */

        // Perform UI logic that depends on information from business logic
        if (deepLinkedContact != null && contacts.isNotEmpty()) {
            LaunchedEffect(listState, deepLinkedContact, contacts) {
                val deepLinkedContactIndex = contacts.indexOf(deepLinkedContact)
                if (deepLinkedContactIndex >= 0) {
                    // Scroll to deep linked item
                    listState.animateScrollToItem(deepLinkedContactIndex)
                }
            }
        }
    }
    // [END android_architecture_stateholders_business_and_ui_logic_contacts]
}

object StateHoldersSnippet5 {
    interface AuthorsRepository
    interface NewsRepository
    class AuthorScreenUiState

    // [START android_architecture_stateholders_hilt_author_viewmodel]
    @HiltViewModel
    class AuthorViewModel @Inject constructor(
        savedStateHandle: SavedStateHandle,
        private val authorsRepository: AuthorsRepository,
        newsRepository: NewsRepository
    ) : ViewModel() {

        val uiState: StateFlow<AuthorScreenUiState> = /* [START_EXCLUDE] */ MutableStateFlow(AuthorScreenUiState()) /* [END_EXCLUDE] */

        // Business logic
        fun followAuthor(followed: Boolean) {
            /* [START_EXCLUDE silent] */
            /* [END_EXCLUDE] */
        }
    }
    // [END android_architecture_stateholders_hilt_author_viewmodel]
}

private object StateHoldersSnippet6 {
    interface NiaNavigationDestination

    // [START android_architecture_stateholders_plain_state_holder_nia_app_state]
    @Stable
    class NiaAppState(
        val navController: NavHostController,
        val windowSizeClass: WindowSizeClass
    ) {

        // UI logic
        val shouldShowBottomBar: Boolean
            get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact ||
                windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

        // UI logic
        val shouldShowNavRail: Boolean
            get() = !shouldShowBottomBar

        // UI State
        val currentDestination: NavDestination?
            @Composable get() = navController
                .currentBackStackEntryAsState().value?.destination

        // UI logic
        fun navigate(destination: NiaNavigationDestination, route: String? = null) { /* ... */ }

        /* ... */
    }
    // [END android_architecture_stateholders_plain_state_holder_nia_app_state]
}

private object StateHoldersSnippet7 {
    enum class DrawerValue { Closed, Open }

    // [START android_architecture_stateholders_compoundable_state_holders]
    @Stable
    class DrawerState(/* ... */) {
        /* [START_EXCLUDE silent] */
        class SwipeableState(vararg args: Any?)
        /* [END_EXCLUDE] */
        internal val swipeableState = SwipeableState(/* ... */)
        // ...
    }

    @Stable
    class MyAppState(
        private val drawerState: DrawerState,
        private val navController: NavHostController
    ) { /* ... */ }

    @Composable
    fun rememberMyAppState(
        drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
        navController: NavHostController = rememberNavController()
    ): MyAppState = remember(drawerState, navController) {
        MyAppState(drawerState, navController)
    }
    // [END android_architecture_stateholders_compoundable_state_holders]

    @Composable
    private fun rememberDrawerState(initialValue: DrawerValue): DrawerState =
        remember { DrawerState() }
}

private object StateHoldersSnippet8 {
    class SomeState
    data class MyScreenUiState(val value: String = "") {
        fun toSomeState(): SomeState = SomeState()
    }

    private fun <T, R> StateFlow<T>.map(transform: (T) -> R): StateFlow<R> =
        MutableStateFlow(transform(value))

    // [START android_architecture_stateholders_dependencies_pass_params]
    class MyScreenViewModel(/* ... */) /* [START_EXCLUDE silent] */ : ViewModel() /* [END_EXCLUDE] */ {
        val uiState: StateFlow<MyScreenUiState> = /* ... */ /* [START_EXCLUDE silent] */ MutableStateFlow(MyScreenUiState()) /* [END_EXCLUDE] */
        fun doSomething() { /* ... */ }
        fun doAnotherThing() { /* ... */ }
        // ...
    }

    @Stable
    class MyScreenState(
        // DO NOT pass a ViewModel instance to a plain state holder class
        // private val viewModel: MyScreenViewModel,

        // Instead, pass only what it needs as a dependency
        private val someState: StateFlow<SomeState>,
        private val doSomething: () -> Unit,

        // Other UI-scoped types
        private val scaffoldState: ScaffoldState
    ) {
        /* ... */
    }

    @Composable
    fun rememberMyScreenState(
        someState: StateFlow<SomeState>,
        doSomething: () -> Unit,
        scaffoldState: ScaffoldState = rememberScaffoldState()
    ): MyScreenState = remember(someState, doSomething, scaffoldState) {
        MyScreenState(someState, doSomething, scaffoldState)
    }

    @Composable
    fun MyScreen(
        modifier: Modifier = Modifier,
        viewModel: MyScreenViewModel = viewModel(),
        state: MyScreenState = rememberMyScreenState(
            someState = viewModel.uiState.map { it.toSomeState() },
            doSomething = viewModel::doSomething
        ),
        // ...
    ) {
        /* ... */
    }
    // [END android_architecture_stateholders_dependencies_pass_params]
}
