package com.example.compose.snippets.navigation3.decorators

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.compose.snippets.navigation3.savingstate.Home
import kotlinx.serialization.Serializable


// [START android_compose_navigation3_decorator_1]
@Serializable
data object Home : NavKey

@Composable
fun DecoratorsBasic() {

    val backStack = rememberNavBackStack(Home)
    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        entryProvider = entryProvider { },
    )
    // [END android_compose_navigation3_decorator_1]
}


