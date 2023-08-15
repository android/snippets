package com.example.compose.snippets.topcomponents

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ButtonExamples(){
    Column(
        modifier = Modifier
            .padding(48.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Text("Filled button:")
        FilledButtonExample( onClick = { Log.d("Filled button", "Filled button clicked.")} )
        Text("Filled tonal button:")
        FilledTonalButtonExample( onClick = { Log.d("Filled tonal button", "Filled tonal button clicked.")} )
        Text("Elevated button:")
        ElevatedButtonExample( onClick = { Log.d("Elevated button", "Elevated button clicked.")} )
        Text("Outlined button:")
        OutlinedButtonExample( onClick = { Log.d("Outlined button", "Outlined button clicked.")} )
        Text("Text button")
        TextButtonExample(onClick = {Log.d("Text button", "Text button clicked.")})
    }
}

@Composable
fun FilledButtonExample(onClick: () -> Unit){
    Button(onClick = {onClick()}){
        Text("Filled")
    }
}

@Composable
fun FilledTonalButtonExample(onClick: () -> Unit){
    FilledTonalButton(onClick = {onClick()}){
        Text("Tonal")
    }
}

@Composable
fun ElevatedButtonExample(onClick: () -> Unit){
    ElevatedButton(onClick = {onClick()}){
        Text("Elevated")
    }
}

@Composable
fun OutlinedButtonExample(onClick: () -> Unit){
    OutlinedButton(onClick = {onClick()}){
        Text("Outlined")
    }
}

@Composable
fun TextButtonExample(onClick: () -> Unit){
    TextButton(
        onClick = { onClick() }
    ){
        Text("Text Button")
    }
}