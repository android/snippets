package com.example.kmp.kmp_shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

// [START android_kmp_viewmodel_class]
class MainViewModel(
    private val repository: DataRepository,
) : ViewModel() { /* some logic */ }

// ViewModelFactory that retrieves the data repository for your app.
val mainViewModelFactory = viewModelFactory {
    initializer {
        MainViewModel(repository = getDataRepository())
    }
}

fun getDataRepository(): DataRepository = TODO()
// [END android_kmp_viewmodel_class]


class DataRepository