package com.example.compose.snippets.pictureinpicture

import android.app.PictureInPictureParams
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
fun PipListenerPreAPI12(shouldEnterPipMode: Boolean) {
    // [START android_compose_pip_pre12_listener]
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S
    ) {
        val context = LocalContext.current
        DisposableEffect(context) {
            val onUserLeaveBehavior : () -> Unit = {
                context.findActivity()
                    .enterPictureInPictureMode(PictureInPictureParams.Builder().build())
            }
            context.findActivity().addOnUserLeaveHintListener(
                onUserLeaveBehavior
            )
            onDispose {
                context.findActivity().removeOnUserLeaveHintListener(
                    onUserLeaveBehavior
                )
            }
        }
    }
    // [END android_compose_pip_pre12_listener]
}

@Composable
fun EnterPiPPre12(shouldEnterPipMode: Boolean) {
    // [START android_compose_pip_pre12_should_enter_pip]
    val currentShouldEnterPipMode by rememberUpdatedState(newValue = shouldEnterPipMode)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S
    ) {
        val context = LocalContext.current
        DisposableEffect(context) {
            val onUserLeaveBehavior: () -> Unit= {
                if (currentShouldEnterPipMode) {
                    context.findActivity()
                        .enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                }
            }
            context.findActivity().addOnUserLeaveHintListener(
                onUserLeaveBehavior
            )
            onDispose {
                context.findActivity().removeOnUserLeaveHintListener(
                    onUserLeaveBehavior
                )
            }
        }
    }
    // [END android_compose_pip_pre12_should_enter_pip]
}

internal fun Context.findActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Picture in picture should be called in the context of an Activity")
}

