package com.example.wear.snippets.notifications

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class LaunchOnWearActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // At this point the Activity would launch the activity remotely on Wear OS via data layer

        // Then the Activity would exit
    }
}