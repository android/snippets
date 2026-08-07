package com.example.aiglasses.camera

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.xr.projected.ProjectedContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Phone Companion Activity for AI Glasses Camera Sample.
 *
 * Because `AIGlassesCameraActivity` declares `android:requiredDisplayCategory="xr_projected"`,
 * the Android system blocks it from launching directly on a phone screen (`display-id=0`).
 *
 * This `MainActivity` serves as the standard phone entry point when tapped from the phone's app drawer.
 * It monitors connection status to the AI glasses and allows launching `AIGlassesCameraActivity`
 * directly into the projected glasses context.
 */
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "CameraSampleHost"
    }

    private var isProjectedConnected by mutableStateOf(false)
    private var projectedConnectionStatus by mutableStateOf("Checking AI Glasses connection...")
    private var connectionJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeGlassesConnection()

        setContent {
            val sophisticatedDarkScheme = darkColorScheme(
                background = Color(0xFF0D0F14),
                surface = Color(0xFF161922),
                surfaceVariant = Color(0xFF1E222D),
                onSurface = Color(0xFFE6E8ED),
                onSurfaceVariant = Color(0xFF9EA4B0),
                primary = Color(0xFF72A7FF),
                onPrimary = Color(0xFF0A1E3C),
                primaryContainer = Color(0xFF1B2E4E),
                onPrimaryContainer = Color(0xFFD2E3FF)
            )
            MaterialTheme(colorScheme = sophisticatedDarkScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PhoneDashboardScreen(
                        connectionStatus = projectedConnectionStatus,
                        isConnected = isProjectedConnected,
                        onLaunchCameraActivity = { launchOnGlasses() },
                        onLaunchMotionGestureActivity = { launchMotionGestureOnGlasses() },
                        onLaunchBackGestureActivity = { launchBackGestureOnGlasses() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        connectionJob?.cancel()
        super.onDestroy()
    }

    private fun observeGlassesConnection() {
        connectionJob?.cancel()
        connectionJob = lifecycleScope.launch {
            val flow = runCatching {
                ProjectedContext.isProjectedDeviceConnected(this@MainActivity, Dispatchers.IO)
            }.onFailure {
                projectedConnectionStatus = "Glasses connectivity service unavailable"
                Log.w(TAG, "Failed to observe glasses connectivity: ${it.message}")
            }.getOrNull() ?: return@launch

            flow.collect { connected ->
                isProjectedConnected = connected
                projectedConnectionStatus = if (connected) {
                    "AI Glasses Connected"
                } else {
                    "AI Glasses Not Connected"
                }
                Log.d(TAG, "Glasses connection state updated: $connected")
            }
        }
    }

    private fun launchOnGlasses() {
        if (!isProjectedConnected) {
            Toast.makeText(this, "Please connect your AI glasses first.", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val projectedContext = ProjectedContext.createProjectedDeviceContext(this)
            val options = ProjectedContext.createProjectedActivityOptions(projectedContext)
            val intent = Intent(this, HelloCameraActionActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch camera activity: ${e.message}", e)
            Toast.makeText(this, "Failed to launch on glasses: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchMotionGestureOnGlasses() {
        if (!isProjectedConnected) {
            Toast.makeText(this, "Please connect your AI glasses first.", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val projectedContext = ProjectedContext.createProjectedDeviceContext(this)
            val options = ProjectedContext.createProjectedActivityOptions(projectedContext)
            val intent = Intent(this, HelloMotionGestureActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch motion gesture activity: ${e.message}", e)
            Toast.makeText(this, "Failed to launch on glasses: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchBackGestureOnGlasses() {
        if (!isProjectedConnected) {
            Toast.makeText(this, "Please connect your AI glasses first.", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val projectedContext = ProjectedContext.createProjectedDeviceContext(this)
            val options = ProjectedContext.createProjectedActivityOptions(projectedContext)
            val intent = Intent(this, HelloBackGestureActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent, options.toBundle())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch back gesture activity: ${e.message}", e)
            Toast.makeText(this, "Failed to launch on glasses: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun PhoneDashboardScreen(
    connectionStatus: String,
    isConnected: Boolean,
    onLaunchCameraActivity: () -> Unit,
    onLaunchMotionGestureActivity: () -> Unit,
    onLaunchBackGestureActivity: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Detect UI Input",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Test out different inputs on glasses's Glimmer UI elements.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(36.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF282D3A)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "STATUS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) Color(0xFF42BE65) else Color(0xFFE2A03F))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = connectionStatus,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onLaunchCameraActivity,
            enabled = isConnected,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text(
                text = "Launch Camera Action Sample",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onLaunchMotionGestureActivity,
            enabled = isConnected,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text(
                text = "Launch Hello Motion Gesture",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onLaunchBackGestureActivity,
            enabled = isConnected,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text(
                text = "Launch Hello Back Gesture",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

