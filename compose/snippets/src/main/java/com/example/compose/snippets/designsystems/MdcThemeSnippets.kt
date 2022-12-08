package com.example.compose.snippets.designsystems

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
// [START android_compose_designsystems_interop_mdctheme]
import com.google.accompanist.themeadapter.material.MdcTheme

class MdcThemeExample : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Use MdcTheme instead of M2 MaterialTheme
    // Colors, typography, and shapes have been read from the
    // View-based theme used in this Activity
    setContent {
      MdcTheme {
        // Your app-level composable here
      }
    }
  }
}
// [END android_compose_designsystems_interop_mdctheme]