package com.example.kmp.snippets

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel


// [START android_kmp_viewmodel_screen]
// androidApp/ui/MainScreen.kt

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(
        factory = mainViewModelFactory,
    ),
) {
// observe the viewModel state
}
// [END android_kmp_viewmodel_screen]
