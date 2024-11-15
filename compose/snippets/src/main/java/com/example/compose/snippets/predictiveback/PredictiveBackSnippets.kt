package com.example.compose.snippets.predictiveback

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.compose.animation.scaleOut
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.animation.EnterTransition
import android.os.SystemClock
import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException


@Composable
private fun PredictiveBackOverrideExit(
    modifier: Modifier,
) {
    val navController = rememberNavController()

    // [START android_compose_predictiveback_navhost]
    NavHost(
        navController = navController,
        startDestination = "home",
        popExitTransition = {
            scaleOut(
                targetScale = 0.9f,
                transformOrigin = TransformOrigin(pivotFractionX  = 0.5f, pivotFractionY = 0.5f)
            )
        },
        popEnterTransition = {
            EnterTransition.None
        },
        modifier = modifier,
    )
    // [END android_compose_predictiveback_navhost]
    {
        composable("home") {
            HomeScreen(
                modifier = modifier,
                navController = navController,
            )
        }
        composable("settings") {
            SettingsScreen(
                modifier = modifier,
                navController = navController,
            )
        }
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier, navController: NavHostController
) {

}

@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier, navController: NavHostController
) {

}

@Composable
private fun PredictiveBackHandlerBasicExample() {

    var boxScale by remember { mutableFloatStateOf(1F) }

    Box(
        modifier = Modifier
            .fillMaxSize(boxScale)
            .background(Color.Blue)
    )

    // [START android_compose_predictivebackhandler_basic]
    PredictiveBackHandler(true) {progress: Flow<BackEventCompat> ->
        // code for gesture back started
        try {
            progress.collect { backEvent ->
                // code for progress
                boxScale = 1F - (1F * backEvent.progress)
            }
            // code for completion

        } catch (e: CancellationException) {
            // code for cancellation
            boxScale = 1F
        }
    }
    // [END android_compose_predictivebackhandler_basic]
}


@Composable
private fun PredictiveBackHandlerManualProgress() {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        var drawerState by remember {
            mutableStateOf(DrawerState.Closed)
        }

        val translationX = remember {
            Animatable(0f)
        }

        val drawerWidth = with(LocalDensity.current) {
            DrawerWidth.toPx()
        }
        translationX.updateBounds(0f, drawerWidth)

        val coroutineScope = rememberCoroutineScope()

        suspend fun closeDrawer(velocity: Float = 0f) {
            translationX.animateTo(targetValue = 0f, initialVelocity = velocity)
            drawerState = DrawerState.Closed
        }
        suspend fun openDrawer(velocity: Float = 0f) {
            translationX.animateTo(targetValue = drawerWidth, initialVelocity = velocity)
            drawerState = DrawerState.Open
        }

        val velocityTracker = remember {
            VelocityTracker()
        }

        // [START android_compose_predictivebackhandler_manualprogress]
        PredictiveBackHandler(drawerState == DrawerState.Open) { progress ->
            try {
                progress.collect { backEvent ->
                    val targetSize = (drawerWidth - (drawerWidth * backEvent.progress))
                    translationX.snapTo(targetSize)
                    velocityTracker.addPosition(
                        SystemClock.uptimeMillis(),
                        Offset(backEvent.touchX, backEvent.touchY)
                    )
                }
                closeDrawer(velocityTracker.calculateVelocity().x)
            } catch (e: CancellationException) {
                openDrawer(velocityTracker.calculateVelocity().x)
            }
            velocityTracker.resetTracking()
        }
        // [END android_compose_predictivebackhandler_manualprogress]

    }
}

private enum class DrawerState {
    Open,
    Closed
}

private val DrawerWidth = 300.dp