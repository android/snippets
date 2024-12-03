package com.example.media

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.media.camera.CameraPreviewScreen
import com.example.media.camera.CameraPreviewViewModel
import com.example.media.ui.theme.SnippetsTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MediaSnippetsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnippetsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    val context = LocalContext.current
                    val viewModel = remember { CameraPreviewViewModel(context.applicationContext) }
                    CameraPreviewScreen(viewModel)
                }
            }
        }
    }
}