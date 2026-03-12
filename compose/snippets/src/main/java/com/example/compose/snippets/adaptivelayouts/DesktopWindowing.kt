package com.example.compose.snippets.adaptivelayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isCaptionBarVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// [START android_compose_desktop_window_insets_title]
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaptionBar() {
    if (WindowInsets.isCaptionBarVisible) {
        Row(
            modifier = Modifier
                .windowInsetsTopHeight(WindowInsets.captionBar)
                .fillMaxWidth()
                .background(
                    if (isSystemInDarkTheme())
                        Color.White
                    else Color.Black

                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Caption Bar Title",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
// [END android_compose_desktop_window_insets_title]