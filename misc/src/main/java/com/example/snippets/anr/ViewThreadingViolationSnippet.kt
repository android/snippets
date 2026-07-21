package com.example.snippets.anr

import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Mock/stub classes so the documentation snippets compile cleanly.
interface MyData {
    val title: String
}

interface MyRepository {
    suspend fun getData(): MyData
}

@SuppressWarnings("unused")
object ViewThreadingViolationSnippet {

    fun switchContext(
        lifecycleOwner: LifecycleOwner,
        myRepository: MyRepository,
        myTextView: TextView
    ) {
        // [START android_view_threading_violation_coroutine_kotlin]
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val data = myRepository.getData()
            withContext(Dispatchers.Main) { // Switch context to Main
                myTextView.text = data.title
            }
        }
        // [END android_view_threading_violation_coroutine_kotlin]
    }

    @RequiresApi(37)
    fun checkViewThreadingViolation() {
        // [START android_view_threading_violation_listener_kotlin]
        val wrongThreadListener = View.CalledFromWrongThreadListener {
            // Handle the issue, e.g. crash if this is a dev build, or log an event
            // Unregister the listener to avoid redundant notifications for the same issue
        }
        View.registerCalledFromWrongThreadListener(wrongThreadListener)
        // [END android_view_threading_violation_listener_kotlin]
    }
}