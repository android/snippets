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

package com.example.compose.snippets.adaptivelayouts.displaymodes

import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

// Stubs for WindowManager APIs
private class WindowAreaController {
    val windowAreaInfos: kotlinx.coroutines.flow.Flow<List<WindowAreaInfo>> = TODO()
    fun presentContentOnWindowArea(token: Binder, activity: android.app.Activity, executor: Executor, windowAreaPresentationSessionCallback: Any) {}
    fun transferActivityToWindowArea(token: Binder, activity: android.app.Activity, executor: Executor, windowAreaSessionCallback: Any) {}
    companion object {
        fun getOrCreate(): WindowAreaController = TODO()
    }
}
private class WindowAreaSession {
    fun close() {}
}
private class WindowAreaInfo {
    val type: Type = TODO()
    val token: Binder = TODO()
    fun getCapability(operation: Any): WindowAreaCapability = TODO()
    fun getActiveSession(operation: Any): WindowAreaSession = TODO()
    enum class Type { TYPE_REAR_FACING }
}
private class WindowAreaCapability {
    val status: Status = TODO()
    enum class Status { WINDOW_AREA_STATUS_UNSUPPORTED, WINDOW_AREA_STATUS_UNAVAILABLE, WINDOW_AREA_STATUS_AVAILABLE, WINDOW_AREA_STATUS_ACTIVE }
    object Operation {
        val OPERATION_PRESENT_ON_AREA = Any()
        val OPERATION_TRANSFER_ACTIVITY_TO_AREA = Any()
    }
}
internal class WindowAreaSessionPresenter {
    val context: android.content.Context = TODO()
    fun setContentView(view: android.view.View) {}
}
internal interface windowAreaPresentationSessionCallback {
    fun onSessionStarted(session: WindowAreaSessionPresenter) {}
    fun onSessionEnded(t: Throwable?) {}
    fun onContainerVisibilityChanged(isVisible: Boolean) {}
}
internal interface WindowAreaSessionCallback {
    fun onSessionStarted() {}
    fun onSessionEnded(t: Throwable?) {}
}

class SupportFoldableDisplayModesActivity : ComponentActivity() {

    // [START android_compose_adaptivelayouts_displaymodes_variables]
    private lateinit var windowAreaController: WindowAreaController
    private lateinit var displayExecutor: Executor
    private var windowAreaSession: WindowAreaSession? = null
    private var windowAreaInfo: WindowAreaInfo? = null
    private var capabilityStatus: WindowAreaCapability.Status =
        WindowAreaCapability.Status.WINDOW_AREA_STATUS_UNSUPPORTED

    private val dualScreenOperation = WindowAreaCapability.Operation.OPERATION_PRESENT_ON_AREA
    private val rearDisplayOperation = WindowAreaCapability.Operation.OPERATION_TRANSFER_ACTIVITY_TO_AREA
    // [END android_compose_adaptivelayouts_displaymodes_variables]

    // Placeholder for 'operation' and 'logTag' used in snippets
    private val operation = Any()
    private val logTag = "SupportFoldableDisplayModes"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START android_compose_adaptivelayouts_displaymodes_initialize]
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
        // [END android_compose_adaptivelayouts_displaymodes_initialize]
    }

    fun checkAvailability() {
        // [START android_compose_adaptivelayouts_displaymodes_availability]
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
        // [END android_compose_adaptivelayouts_displaymodes_availability]
    }

    // [START android_compose_adaptivelayouts_displaymodes_toggle_dual]
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
    // [END android_compose_adaptivelayouts_displaymodes_toggle_dual]

    // [START android_compose_adaptivelayouts_displaymodes_toggle_rear]
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
    // [END android_compose_adaptivelayouts_displaymodes_toggle_rear]
}

// [START android_compose_adaptivelayouts_displaymodes_activity_decl]
class MainActivity : ComponentActivity(), windowAreaPresentationSessionCallback
// [END android_compose_adaptivelayouts_displaymodes_activity_decl]

internal class CallbackActivity : ComponentActivity(), windowAreaPresentationSessionCallback {
    private var windowAreaSession: WindowAreaSessionPresenter? = null
    private val logTag = "CallbackActivity"

    // [START android_compose_adaptivelayouts_displaymodes_callbacks]
    override fun onSessionStarted(session: WindowAreaSessionPresenter) {
        windowAreaSession = session
        val view = TextView(session.context)
        view.text = "Hello world!"
        session.setContentView(view)
    }

    override fun onSessionEnded(t: Throwable?) {
        if(t != null) {
            Log.e(logTag, "Something was broken: ${t.message}")
        }
    }

    override fun onContainerVisibilityChanged(isVisible: Boolean) {
        Log.d(logTag, "onContainerVisibilityChanged. isVisible = $isVisible")
    }
    // [END android_compose_adaptivelayouts_displaymodes_callbacks]
}

class RearDisplayCallbackActivity : ComponentActivity(), WindowAreaSessionCallback {
    private val logTag = "RearDisplayCallbackActivity"

    // [START android_compose_adaptivelayouts_displaymodes_callbacks_rear]
    override fun onSessionStarted() {
        Log.d(logTag, "onSessionStarted")
    }

    override fun onSessionEnded(t: Throwable?) {
        if(t != null) {
            Log.e(logTag, "Something was broken: ${t.message}")
        }
    }
    // [END android_compose_adaptivelayouts_displaymodes_callbacks_rear]
}
