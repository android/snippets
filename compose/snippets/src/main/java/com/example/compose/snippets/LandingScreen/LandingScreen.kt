package com.example.compose.snippets.LandingScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LandingScreen(
    toBrushExamples: () -> Unit,
    toImageExamples: () -> Unit,
){
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ){
        Text(
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
            text = "Android snippets",
        )
        Text(
            text = "Use the following buttons to view a selection of the snippets used in the Android documentation."
        )
        NavigationButtons(toBrushExamples, toImageExamples)
    }

}

@Composable
fun NavigationButtons(toBrushExamples: () -> Unit, toImageExamples: () -> Unit){
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = toBrushExamples) {
            Text("Brush examples")
        }
        Button(onClick = toImageExamples) {
            Text("Image examples")
        }
    }
}