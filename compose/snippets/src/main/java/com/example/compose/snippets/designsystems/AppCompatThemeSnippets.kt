package com.example.compose.snippets.designsystems

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
// [START android_compose_designsystems_interop_appcompattheme]
import com.google.accompanist.themeadapter.appcompat.AppCompatTheme

class AppCompatThemeExample : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      // Colors and typography have been read from the
      // View-based theme used in this Activity
      // Shapes are the default for M2 as this didn't exist in M1
      AppCompatTheme {
        // Your app-level composable here
      }
    }
  }
}
// [END android_compose_designsystems_interop_appcompattheme]