package com.example.compose.snippets.designsystems

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A simple full-screen settings-like mock that uses the current MaterialTheme surface color
 * and disables tonal elevation so the surfaces render as pure black when the dark scheme is set to black.
 */
@Composable
fun SettingsMock(itemsList: List<String>) {
    LazyColumn {
        items(itemsList) { title ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
