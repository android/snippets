package com.example.compose.snippets.topcomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.navigation.TopComponentsDestination


@Composable
fun TopComponentsScreen(navigate: (TopComponentsDestination) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(TopComponentsDestination.values().toList()) { destination ->
            NavigationItem(destination) {
                navigate(
                    destination
                )
            }
        }
    }
}

@Composable
fun NavigationItem(destination: TopComponentsDestination, onClick: () -> Unit) {
    Button(
        onClick = { onClick() }
    ) {
        Text( destination.title )
    }
}
