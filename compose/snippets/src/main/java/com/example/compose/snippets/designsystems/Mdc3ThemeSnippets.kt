package com.example.compose.snippets.designsystems

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
// [START android_compose_designsystems_interop_mdc3theme]
import com.google.accompanist.themeadapter.material3.Mdc3Theme

class Mdc3ThemeExample : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Use Mdc3Theme instead of M3 MaterialTheme
    // Color scheme, typography, and shapes have been read from the
    // View-based theme used in this Activity
    setContent {
      Mdc3Theme {
        // Your app-level composable here
      }
    }
  }
}
// [END android_compose_designsystems_interop_mdc3theme]