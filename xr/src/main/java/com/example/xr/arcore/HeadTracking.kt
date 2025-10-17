/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.xr.arcore

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.xr.arcore.RenderViewpoint
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.spatial.Subspace
import androidx.xr.runtime.Config
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionConfigureConfigurationNotSupported
import androidx.xr.runtime.SessionConfigureSuccess
import androidx.xr.runtime.manifest.HEAD_TRACKING
import androidx.xr.runtime.math.IntSize2d
import androidx.xr.runtime.math.Pose
import androidx.xr.scenecore.PanelEntity
import androidx.xr.scenecore.scene
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun configureSession(session: Session) {
    // [START androidxr_arcore_headtracking_configure]
    val newConfig = session.config.copy(
        headTracking = Config.HeadTrackingMode.LAST_KNOWN,
    )
    when (val result = session.configure(newConfig)) {
        is SessionConfigureSuccess -> TODO(/* Success! */)
        is SessionConfigureConfigurationNotSupported ->
            TODO(/* Some combinations of configurations are not valid. Handle this failure case. */)

        else ->
            TODO(/* The session could not be configured. See SessionConfigureResult for possible causes. */)
    }
    // [END androidxr_arcore_headtracking_configure]
}

private suspend fun getMonoViewpoint(session: Session) {
    // [START androidxr_arcore_headtracking_mono]
    val mono = RenderViewpoint.mono(session) ?: return
    mono.state.collect { state ->
        val fov = state.fieldOfView
        val viewpointPose = state.pose
    }
    // [END androidxr_arcore_headtracking_mono]
}

class HeadTrackingDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    demoContent()
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this@HeadTrackingDemoActivity,
                HEAD_TRACKING
            ) == PackageManager.PERMISSION_GRANTED -> {
                demoContent()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, HEAD_TRACKING
            ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
            }

            else -> {
                requestPermissionLauncher.launch(HEAD_TRACKING)
            }
        }
    }

    private fun createMovingPanel(session: Session): PanelEntity {
        val composeView = ComposeView(this)
        composeView.setContent {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    text = "I'm trying to stay in the field of view!"
                )
            }
        }
        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )

        val parentView: View =
            if (composeView.parent != null && composeView.parent is View) composeView.parent as View
            else composeView

        parentView.setViewTreeLifecycleOwner(this@HeadTrackingDemoActivity as LifecycleOwner)
        parentView.setViewTreeViewModelStoreOwner(this@HeadTrackingDemoActivity as ViewModelStoreOwner)
        parentView.setViewTreeSavedStateRegistryOwner(this@HeadTrackingDemoActivity as SavedStateRegistryOwner)

        return PanelEntity.create(
            session,
            composeView,
            IntSize2d(640, 640),
            "movableEntity",
        )
    }

    fun demoContent() {
        setContent {
            Subspace {
                val session = LocalSession.current!!
                session.configure(session.config.copy(headTracking = Config.HeadTrackingMode.LAST_KNOWN))
                val panel = createMovingPanel(session)
                // [START androidxr_arcore_headtracking_entity_follow]
                val viewpointPose = RenderViewpoint.left(session)!!.state
                lifecycleScope.launch {
                    while (true) {
                        delay(2000)
                        val start = panel.getPose()
                        val startTime = session.state.value.timeMark

                        val pose = session.scene.perceptionSpace.transformPoseTo(
                            viewpointPose.value.pose,
                            session.scene.activitySpace
                        )
                        val target = Pose(pose.translation + pose.forward * 1f, pose.rotation)
                        while (true) {
                            val ratio =
                                (session.state.value.timeMark - startTime).inWholeMilliseconds / 500f
                            panel.setPose(Pose.lerp(start, target, ratio))
                            if (ratio > 1f) break
                        }
                    }
                }
                // [END androidxr_arcore_headtracking_entity_follow]
            }
        }
    }
}
