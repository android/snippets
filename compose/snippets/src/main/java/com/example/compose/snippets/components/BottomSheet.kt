package com.example.compose.snippets.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PartialBottomSheet() {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Button(
            onClick = { showBottomSheet = true }
        ){
            Text("Display partial bottom sheet")
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false }
            ) {
                Text("Swipe up to open sheet. Swipe down to dismiss.")
                Text("Here is some placeholder content: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut justo urna, facilisis vel aliquam pretium, mollis id ex. Nam sollicitudin, purus id mattis accumsan, justo eros imperdiet nisl, ac iaculis arcu nisi et ex. Praesent mi enim, luctus lobortis mauris eget, sollicitudin efficitur purus. Donec quis tellus aliquam, commodo mi feugiat, dapibus lectus.")
            }
        }
    }
}
