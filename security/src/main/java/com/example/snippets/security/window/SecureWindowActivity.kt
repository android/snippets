package com.example.snippets.security.window

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.snippets.security.R

class SecureWindowActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Treat the content of the window as secure, preventing it from appearing in screenshots
        // or from being viewed on non-secure displays. On Android 17+ (API 37+), this also
        // opts-out the view from Content Capture,
        // See {@link com.android.view.ContentCaptureManager}.
        // [START android_security_secure_window]
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        // [END android_security_secure_window]

        setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.secure_text))
            }
        }
    }
}