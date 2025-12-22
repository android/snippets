package com.example.xr.projected

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.surface

// [START androidxr_projected_ai_glasses_activity]
class GlassesMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GlimmerTheme {
                HomeScreen(onClose = {
                    finish()
                })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Do things to make the user aware that this activity is active (for
        // example, play audio frequently), when the display is off.
    }

    override fun onStop() {
        super.onStop()
        // Stop all the data source access.
    }

}
// [END androidxr_projected_ai_glasses_activity]

// [START androidxr_projected_ai_glasses_activity_homescreen]
@Composable
fun HomeScreen(modifier: Modifier = Modifier, onClose: () -> Unit) {
    Box(
        modifier = modifier
            .surface(focusable = false).fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            title = { Text("Android XR") },
            action = {
                Button(onClick = {
                    onClose()
                }) {
                    Text("Close")
                }
            }
        ) {
            Text("Hello, AI Glasses!")
        }
    }
}
// [END androidxr_projected_ai_glasses_activity_homescreen]
