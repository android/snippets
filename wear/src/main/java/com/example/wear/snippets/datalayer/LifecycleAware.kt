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

package com.example.wear.snippets.datalayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable

// [START android_wear_datalayer_lifecycle_observer]
class WearDataLayerObserver(
    private val dataClient: DataClient,
    private val onDataReceived: (DataEventBuffer) -> Unit
) : DefaultLifecycleObserver, DataClient.OnDataChangedListener {

    // Implementation of the DataClient listener
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        onDataReceived(dataEvents)
    }

    // Automatically register when the Activity starts
    override fun onResume(owner: LifecycleOwner) {
        dataClient.addListener(this)
    }

    // Automatically unregister when the Activity pauses
    override fun onPause(owner: LifecycleOwner) {
        dataClient.removeListener(this)
    }
}
// [END android_wear_datalayer_lifecycle_observer]

// [START android_wear_datalayer_lifecycle_activity]
class DataLayerLifecycleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataClient = Wearable.getDataClient(this)

        // Create the observer and link it to the activity's lifecycle
        val wearObserver = WearDataLayerObserver(dataClient) { dataEvents ->
            handleDataEvents(dataEvents)
        }

        lifecycle.addObserver(wearObserver)
    }

    private fun handleDataEvents(dataEvents: DataEventBuffer) {
        // ... filter and process events ...
    }
}
// [END android_wear_datalayer_lifecycle_activity]
