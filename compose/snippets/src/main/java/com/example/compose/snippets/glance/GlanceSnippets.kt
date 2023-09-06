/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.snippets.glance

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.action.actionStartService
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import com.example.compose.snippets.R
import com.example.compose.snippets.layouts.MainActivity

private object GlanceCreateAppWidgetSnippet01 {
    // [START android_compose_glance_receiver01]
    class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget: GlanceAppWidget = TODO("Create GlanceAppWidget")
    }
    // [END android_compose_glance_receiver01]
}

private object GlanceCreateAppWidgetSnippet02 {
    // [START android_compose_glance_receiver02]
    class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
        // Let MyAppWidgetReceiver know which GlanceAppWidget to use
        override val glanceAppWidget: GlanceAppWidget = MyAppWidget()
    }
    // [END android_compose_glance_receiver02]

    // [START android_compose_glance_widget]
    class MyAppWidget : GlanceAppWidget() {
        override suspend fun provideGlance(context: Context, id: GlanceId) {

            // In this method, load data needed to render the AppWidget.
            // Use `withContext` to switch to another thread for long running
            // operations.

            provideContent {
                // create your AppWidget here
                Text("Hello World")
            }
        }
    }
    // [END android_compose_glance_widget]
}

// TODO Refactor this so the whole snippet is in an external file snippit the includes
private object CreateUI {
    // [START android_compose_glance_createui]
  /* Import Glance Composables
   In the event there is a name clash with the Compose classes of the same name,
   you may rename the imports per https://kotlinlang.org/docs/packages.html#imports
   using the as keyword.

  import androidx.glance.Button
  import androidx.glance.layout.Column
  import androidx.glance.layout.Row
  import androidx.glance.text.Text
  */
    class MyAppWidget : GlanceAppWidget() {

        override suspend fun provideGlance(context: Context, id: GlanceId) {
            // Load data needed to render the AppWidget.
            // Use `withContext` to switch to another thread for long running
            // operations.

            provideContent {
                // create your AppWidget here
                MyContent()
            }
        }

        @Composable
        private fun MyContent() {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Where to?", modifier = GlanceModifier.padding(12.dp))
                Row(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        text = "Home",
                        onClick = actionStartActivity<MainActivity>()
                    )
                    Button(
                        text = "Work",
                        onClick = actionStartActivity<MainActivity>()
                    )
                }
            }
        }
    }
    // [END android_compose_glance_createui]
}

private object ActionLaunchActivity {

    // [START android_compose_glance_launchactivity]
    @Composable
    fun MyContent() {
        // ..
        Button(
            text = "Go Home",
            onClick = actionStartActivity<MainActivity>()
        )
    }
    // [END android_compose_glance_launchactivity]
}

private object ActionLaunchService {

    // [START android_compose_glance_launchservice]
    @Composable
    fun MyButton() {
        // ..
        Button(
            text = "Sync",
            onClick = actionStartService<SyncService>(
                isForegroundService = true // define how the service is launched
            )
        )
    }
    // [END android_compose_glance_launchservice]
}

private object ActionLaunchSendBroadcastEvent {

    // [START android_compose_glance_sendbroadcastevent]
    @Composable
    fun MyButton() {
        // ..
        Button(
            text = "Send",
            onClick = actionSendBroadcast<MyReceiver>()
        )
    }
    // [END android_compose_glance_sendbroadcastevent]
}

private object ActionLambda {
    @Composable
    fun actionLambda() {
        // [START android_compose_glance_lambda01]
        Text(
            text = "Submit",
            modifier = GlanceModifier.clickable {
                submitData()
            }
        )
        // [END android_compose_glance_lambda01]
    }

    @Composable
    fun actionLambda2() {
        // [START android_compose_glance_lambda02]
        Button(
            text = "Submit",
            onClick = {
                submitData()
            }
        )
        // [END android_compose_glance_lambda02]
    }
}

private object ActionCallbackSnippet02 {
    // [START android_compose_glance_actioncallback02]
    class RefreshAction : ActionCallback {
        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            // do some work but offset long-term tasks (e.g a Worker)
            MyAppWidget().update(context, glanceId)
        }
    }
    // [END android_compose_glance_actioncallback02]
    /*dummy class*/
    class MyAppWidget : GlanceAppWidget() {
        override suspend fun provideGlance(context: Context, id: GlanceId) {
            TODO("Not yet implemented")
        }
    }
}

private object ActionCallbackSnippet01 {
    // [START android_compose_glance_actioncallback01]
    @Composable
    private fun MyContent() {
        // ..
        Image(
            provider = ImageProvider(R.drawable.ic_hourglass_animated),
            modifier = GlanceModifier.clickable(
                onClick = actionRunCallback<RefreshAction>()
            ),
            contentDescription = "Refresh"
        )
    }

    class RefreshAction : ActionCallback {
        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            // TODO implement
        }
    }
    // [END android_compose_glance_actioncallback01]
}

private object ActionParameters {
    // [START android_compose_glance_actioncparameters01]
    private val destinationKey = ActionParameters.Key<String>(
        NavigationActivity.KEY_DESTINATION
    )

    class MyAppWidget : GlanceAppWidget() {

        // ..

        @Composable
        private fun MyContent() {
            // ..
            Button(
                text = "Home",
                onClick = actionStartActivity<NavigationActivity>(
                    actionParametersOf(destinationKey to "home")
                )
            )
            Button(
                text = "Work",
                onClick = actionStartActivity<NavigationActivity>(
                    actionParametersOf(destinationKey to "work")
                )
            )
        }

        override suspend fun provideGlance(context: Context, id: GlanceId) {
            provideContent { MyContent() }
        }
    }
    // [END android_compose_glance_actioncparameters01]

    abstract class ActionParametersActivity : Activity() {
        val KEY_DESTINATION = "destination"

        // [START android_compose_glance_actioncparameters02]
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val destination = intent.extras?.getString(KEY_DESTINATION) ?: return
            // ...
        }
    }
    // [END android_compose_glance_actioncparameters02]

    // [START android_compose_glance_actioncparameters03
    class RefreshAction : ActionCallback {
        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            val destination: String = parameters[destinationKey] ?: return
            // ...
        }
    }
    // [END android_compose_glance_actioncparameters03]
}

/**
 * Dummy activity for snippet
 */
class NavigationActivity : AppCompatActivity() {
    companion object {
        val KEY_DESTINATION: String = "destination"
    }
}

/**
 * Dummy lambda
 */
private fun submitData() {
    TODO("Not yet implemented")
}

/**
 * Dummy broadcast receiver for snippets
 */
class MyReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        TODO("Not yet implemented")
    }
}

/**
 * Dummy service for snippets
 */
class SyncService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}
