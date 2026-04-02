package com.example.wear.snippets.audio

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider

class RemoteMediaSessionActivity : ComponentActivity() {

    private lateinit var viewModel: RemoteMediaActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 37) {
            viewModel = ViewModelProvider(this)[RemoteMediaActivityViewModel::class.java]
            setContent {
            // set the UI content here
            }
        }
    }
}
