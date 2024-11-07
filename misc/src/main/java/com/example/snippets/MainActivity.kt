package com.example.snippets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.snippets.navigation.Destination
import com.example.snippets.navigation.LandingScreen
import com.example.snippets.ui.theme.SnippetsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataRepository: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnippetsTheme {
                val navController = rememberNavController()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController, startDestination = "LandingScreen") {
                        composable("LandingScreen") {
                            LandingScreen { navController.navigate(it.route) }
                        }
                        Destination.entries.forEach { destination ->
                            composable(destination.route) {
                                when (destination) {
                                    Destination.BroadcastReceiverExamples -> BroadcastReceiverSample()
                                    // Add your destination here
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
