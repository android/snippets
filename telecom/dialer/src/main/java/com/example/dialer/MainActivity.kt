package com.example.dialer

import android.Manifest
import android.content.ContentUris
import android.os.Bundle
import android.provider.CallLog
import android.telecom.TelecomManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dialer.ui.theme.SnippetsTheme

class MainActivity : ComponentActivity() {
    lateinit var telecomManager: TelecomManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val callLogUri = getContentUriForVoIPCallLogs()
        telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager

        setContent {
            SnippetsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun getContentUriForVoIPCallLogs() =
        // START android_telecom_call_log_uri_with_voip_logs
        CallLog.Calls.CONTENT_URI.buildUpon()
            .appendQueryParameter("include_voip_calls", "true")
            .build()
        // END android_telecom_call_log_uri_with_voip_logs

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    fun initiateCallback(callId: Long) {
        // START android_telecom_dialer_callback
        // Uri generated with unique ID of the call log entry to launch the respective VoIP app for callback
        val address = ContentUris.withAppendedId(CallLog.Calls.CONTENT_URI, callId)

        // extra information required to initiate callback
        val extras = Bundle()

        telecomManager.placeCall(address, extras)
        // END android_telecom_dialer_callback
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SnippetsTheme {
        Greeting("Android")
    }
}