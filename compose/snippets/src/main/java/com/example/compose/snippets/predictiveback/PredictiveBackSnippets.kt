package com.example.compose.snippets.predictiveback

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.scaleOut
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.animation.EnterTransition
import android.os.SystemClock
import androidx.activity.compose.PredictiveBackHandler
import androidx.navigation.compose.NavHost
import androidx.compose.animation.core.Animatable
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.launch


@Composable
fun MainNavigation(
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
fun HomeScreen(
    modifier: Modifier = Modifier, navController: NavHostController
) {

}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier, navController: NavHostController
) {

@Composable
fun HomeScreenDrawer() {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        var drawerState by remember {
            mutableStateOf(DrawerState.Closed)
        }
        var screenState by remember {
            mutableStateOf(Screen.Home)
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
        fun toggleDrawerState() {
            coroutineScope.launch {
                if (drawerState == DrawerState.Open) {
                    closeDrawer()
                } else {
                    openDrawer()
                }
            }
        }
        val velocityTracker = remember {
            VelocityTracker()
        }

        // [START android_compose_predictivebackhandler]
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
        // [END android_compose_predictivebackhandler]

    }
}

private enum class DrawerState {
    Open,
    Closed
}
