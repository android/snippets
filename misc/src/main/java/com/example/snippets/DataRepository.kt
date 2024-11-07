package com.example.snippets

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor() {
    // You would normally save this data in a database or other persistent storage
    private val _data: MutableStateFlow<String> = MutableStateFlow("This text will be updated")
    val data: StateFlow<String> = _data

    fun updateData(data: String) {
        _data.value += "\n$data"
    }
}
