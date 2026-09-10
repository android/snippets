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

package com.example.example.snippet.views.devicecontrol

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.service.controls.Control
import android.service.controls.ControlsProviderService
import android.service.controls.DeviceTypes
import android.service.controls.actions.BooleanAction
import android.service.controls.actions.ControlAction
import android.service.controls.actions.FloatAction
import android.service.controls.templates.ControlButton
import android.service.controls.templates.ControlTemplate
import android.service.controls.templates.RangeTemplate
import android.service.controls.templates.ToggleTemplate
import java.util.Locale
import java.util.concurrent.Flow
import java.util.function.Consumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.jdk9.asPublisher
import kotlinx.coroutines.jdk9.flowPublish
import kotlinx.coroutines.launch

private const val EXTRA_MESSAGE = "com.example.devicecontrol.EXTRA_MESSAGE"

private class MainActivity : Activity()

private object DeclareServiceSnippet {
    // [START android_views_device_control_declare_service]
    class MyCustomControlService : ControlsProviderService() {
        // ...
        // [START_EXCLUDE silent]
        override fun createPublisherForAllAvailable(): Flow.Publisher<Control> = TODO()

        override fun createPublisherFor(controlIds: List<String>): Flow.Publisher<Control> = TODO()

        override fun performControlAction(
            controlId: String,
            action: ControlAction,
            consumer: Consumer<Int>,
        ) = TODO()
        // [END_EXCLUDE]
    }
    // [END android_views_device_control_declare_service]
}

private object DeclareConstantsSnippet {
    // [START android_views_device_control_declare_constants]
    private const val LIGHT_ID = 1234
    private const val LIGHT_TITLE = "My fancy light"
    private const val LIGHT_TYPE = DeviceTypes.TYPE_LIGHT
    private const val THERMOSTAT_ID = 5678
    private const val THERMOSTAT_TITLE = "My fancy thermostat"
    private const val THERMOSTAT_TYPE = DeviceTypes.TYPE_THERMOSTAT

    class MyCustomControlService : ControlsProviderService() {
        // ...
        // [START_EXCLUDE silent]
        override fun createPublisherForAllAvailable(): Flow.Publisher<Control> = TODO()

        override fun createPublisherFor(controlIds: List<String>): Flow.Publisher<Control> = TODO()

        override fun performControlAction(
            controlId: String,
            action: ControlAction,
            consumer: Consumer<Int>,
        ) = TODO()
        // [END_EXCLUDE]
    }
    // [END android_views_device_control_declare_constants]
}

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private object CreatePublisherAllAvailableSnippet {
    private const val LIGHT_ID = 1234
    private const val LIGHT_TITLE = "My fancy light"
    private const val LIGHT_TYPE = DeviceTypes.TYPE_LIGHT
    private const val THERMOSTAT_ID = 5678
    private const val THERMOSTAT_TITLE = "My fancy thermostat"
    private const val THERMOSTAT_TYPE = DeviceTypes.TYPE_THERMOSTAT

    // [START android_views_device_control_create_publisher_all_available]
    class MyCustomControlService : ControlsProviderService() {

        override fun createPublisherForAllAvailable(): Flow.Publisher<Control> =
            flowPublish {
                send(createStatelessControl(LIGHT_ID, LIGHT_TITLE, LIGHT_TYPE))
                send(createStatelessControl(THERMOSTAT_ID, THERMOSTAT_TITLE, THERMOSTAT_TYPE))
            }

        private fun createStatelessControl(id: Int, title: String, type: Int): Control {
            val intent = Intent(this, MainActivity::class.java)
                .putExtra(EXTRA_MESSAGE, title)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val action = PendingIntent.getActivity(
                this,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            return Control.StatelessBuilder(id.toString(), action)
                .setTitle(title)
                .setDeviceType(type)
                .build()
        }

        override fun createPublisherFor(controlIds: List<String>): Flow.Publisher<Control> {
            TODO()
        }

        override fun performControlAction(
            controlId: String,
            action: ControlAction,
            consumer: Consumer<Int>,
        ) {
            TODO()
        }
    }
    // [END android_views_device_control_create_publisher_all_available]
}

private const val LIGHT_ID = 1234
private const val LIGHT_TITLE = "My fancy light"
private const val LIGHT_TYPE = DeviceTypes.TYPE_LIGHT
private const val THERMOSTAT_ID = 5678
private const val THERMOSTAT_TITLE = "My fancy thermostat"
private const val THERMOSTAT_TYPE = DeviceTypes.TYPE_THERMOSTAT

class MyCustomControlService : ControlsProviderService() {

    override fun createPublisherForAllAvailable(): Flow.Publisher<Control> = TODO()

    // [START android_views_device_control_create_publisher_for]
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val controlFlows = mutableMapOf<String, MutableSharedFlow<Control>>()

    private var toggleState = false
    private var rangeState = 18f

    override fun createPublisherFor(controlIds: List<String>): Flow.Publisher<Control> {
        val flow = MutableSharedFlow<Control>(replay = 2, extraBufferCapacity = 2)

        controlIds.forEach { controlFlows[it] = flow }

        scope.launch {
            delay(1000) // Retrieving the toggle state.
            flow.tryEmit(createLight())

            delay(1000) // Retrieving the range state.
            flow.tryEmit(createThermostat())
        }
        return flow.asPublisher()
    }

    private fun createLight() = createStatefulControl(
        LIGHT_ID,
        LIGHT_TITLE,
        LIGHT_TYPE,
        toggleState,
        ToggleTemplate(
            LIGHT_ID.toString(),
            ControlButton(
                toggleState,
                toggleState.toString().uppercase(Locale.getDefault()),
            ),
        ),
    )

    private fun createThermostat() = createStatefulControl(
        THERMOSTAT_ID,
        THERMOSTAT_TITLE,
        THERMOSTAT_TYPE,
        rangeState,
        RangeTemplate(
            THERMOSTAT_ID.toString(),
            15f,
            25f,
            rangeState,
            0.1f,
            "%1.1f",
        ),
    )

    private fun <T> createStatefulControl(id: Int, title: String, type: Int, state: T, template: ControlTemplate): Control {
        val intent = Intent(this, MainActivity::class.java)
            .putExtra(EXTRA_MESSAGE, "$title $state")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val action = PendingIntent.getActivity(
            this,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return Control.StatefulBuilder(id.toString(), action)
            .setTitle(title)
            .setDeviceType(type)
            .setStatus(Control.STATUS_OK)
            .setControlTemplate(template)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
    // [END android_views_device_control_create_publisher_for]

    // [START android_views_device_control_perform_control_action]
    override fun performControlAction(
        controlId: String,
        action: ControlAction,
        consumer: Consumer<Int>,
    ) {
        controlFlows[controlId]?.let { flow ->
            when (controlId) {
                LIGHT_ID.toString() -> {
                    consumer.accept(ControlAction.RESPONSE_OK)
                    if (action is BooleanAction) toggleState = action.newState
                    flow.tryEmit(createLight())
                }
                THERMOSTAT_ID.toString() -> {
                    consumer.accept(ControlAction.RESPONSE_OK)
                    if (action is FloatAction) rangeState = action.newValue
                    flow.tryEmit(createThermostat())
                }
                else -> consumer.accept(ControlAction.RESPONSE_FAIL)
            }
        } ?: consumer.accept(ControlAction.RESPONSE_FAIL)
    }
    // [END android_views_device_control_perform_control_action]
}
