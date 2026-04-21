/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.compose.snippets.adaptivelayouts

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.area.WindowAreaCapability
import androidx.window.area.WindowAreaController
import androidx.window.area.WindowAreaInfo
import androidx.window.area.WindowAreaPresentationSessionCallback
import androidx.window.area.WindowAreaSession
import androidx.window.area.WindowAreaSessionCallback
import androidx.window.area.WindowAreaSessionPresenter
import androidx.window.core.ExperimentalWindowApi
import java.util.concurrent.Executor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalWindowApi::class)
// [START android_adaptive_window_callback]
class ExampleActivity : ComponentActivity(), WindowAreaPresentationSessionCallback {
// [END android_adaptive_window_callback]

    val logTag = "FoldableModes"

    // [START android_adaptive_foldable_vars]
    private lateinit var windowAreaController: WindowAreaController
    private lateinit var displayExecutor: Executor
    private var windowAreaSession: WindowAreaSession? = null
    private var windowAreaInfo: WindowAreaInfo? = null
    private var capabilityStatus: WindowAreaCapability.Status =
        WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED

    private val dualScreenOperation = WindowAreaCapability.Operation.OPERATION_PRESENT_ON_AREA
    private val rearDisplayOperation = WindowAreaCapability.Operation.OPERATION_TRANSFER_ACTIVITY_TO_AREA
    // [END android_adaptive_foldable_vars]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val operation = dualScreenOperation

        // [START android_adaptive_foldable_init]
        displayExecutor = ContextCompat.getMainExecutor(this)
        windowAreaController = WindowAreaController.getOrCreate()

        lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                windowAreaController.windowAreaInfos
                    .map { info -> info.firstOrNull { it.type == WindowAreaInfo.Type.TYPE_REAR_FACING } }
                    .onEach { info -> windowAreaInfo = info }
                    .map { it?.getCapability(operation)?.status ?: WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED }
                    .distinctUntilChanged()
                    .collect {
                        capabilityStatus = it
                    }
            }
        }
        // [END android_adaptive_foldable_init]
    }

    fun checkCapability() {
        // [START android_adaptive_capability_check]
        when (capabilityStatus) {
            WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED -> {
              // The selected display mode is not supported on this device.
            }
            WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNAVAILABLE -> {
              // The selected display mode is not available.
            }
            WindowAreaCapability.Status.WINDOW_AREA_STATUS_AVAILABLE -> {
              // The selected display mode is available and can be enabled.
            }
            WindowAreaCapability.Status.WINDOW_AREA_STATUS_ACTIVE -> {
              // The selected display mode is already active.
            }
            else -> {
              // The selected display mode status is unknown.
            }
        }
        // [END android_adaptive_capability_check]
    }

    // [START android_adaptive_toggle_dual_screen]
    fun toggleDualScreenMode() {
        if (windowAreaSession != null) {
            windowAreaSession?.close()
        }
        else {
            windowAreaInfo?.token?.let { token ->
                windowAreaController.presentContentOnWindowArea(
                    token = token,
                    activity = this,
                    executor = displayExecutor,
                    windowAreaPresentationSessionCallback = this
                )
            }
        }
    }
    // [END android_adaptive_toggle_dual_screen]

    // [START android_adaptive_session_callbacks]
    override fun onSessionStarted(session: WindowAreaSessionPresenter) {
        windowAreaSession = session
        session.setContentView(ComposeView(session.context).apply {
            setContent {
                MyScreen()
            }
        })
    }

    override fun onSessionEnded(t: Throwable?) {
        if (t != null) {
            Log.e(logTag, "Something was broken: ${t.message}")
        }
    }

    override fun onContainerVisibilityChanged(isVisible: Boolean) {
         Log.d(logTag, "onContainerVisibilityChanged. isVisible = $isVisible")
    }
    // [END android_adaptive_session_callbacks]

    @Composable
    fun MyScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello world")
        }
    }
}

@OptIn(ExperimentalWindowApi::class)
class RearDisplayActivity : ComponentActivity(), WindowAreaSessionCallback {

    val logTag = "RearDisplayActivity"

    private lateinit var windowAreaController: WindowAreaController
    private lateinit var displayExecutor: Executor
    private var windowAreaSession: WindowAreaSession? = null
    private var windowAreaInfo: WindowAreaInfo? = null
    private var capabilityStatus: WindowAreaCapability.Status =
        WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED
    private val operation = WindowAreaCapability.Operation.OPERATION_TRANSFER_ACTIVITY_TO_AREA

    // [START android_adaptive_toggle_rear_display]
    fun toggleRearDisplayMode() {
        if(capabilityStatus == WindowAreaCapability.Status.WINDOW_AREA_STATUS_ACTIVE) {
            if(windowAreaSession == null) {
                windowAreaSession = windowAreaInfo?.getActiveSession(
                    operation
                )
            }
            windowAreaSession?.close()
        } else {
            windowAreaInfo?.token?.let { token ->
                windowAreaController.transferActivityToWindowArea(
                    token = token,
                    activity = this,
                    executor = displayExecutor,
                    windowAreaSessionCallback = this
                )
            }
        }
    }
    // [END android_adaptive_toggle_rear_display]

    // [START android_adaptive_rear_display_callbacks]
    override fun onSessionStarted(session: WindowAreaSession) {
         Log.d(logTag, "onSessionStarted")
    }

    override fun onSessionEnded(t: Throwable?) {
        if (t != null) {
            Log.e(logTag, "Something was broken: ${t.message}")
        }
    }
    // [END android_adaptive_rear_display_callbacks]
}
