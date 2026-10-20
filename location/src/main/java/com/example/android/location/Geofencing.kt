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

package com.example.android.location

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

private const val TAG = "Geofencing"

private class GeofencingActivity : ComponentActivity() {

    private val geofenceList = mutableListOf<Geofence>()
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    // [START android_location_geofencing_client]
    lateinit var geofencingClient: GeofencingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        // [START_EXCLUDE]
        super.onCreate(savedInstanceState)
        // [END_EXCLUDE]
        geofencingClient = LocationServices.getGeofencingClient(this)
    }
    // [END android_location_geofencing_client]

    private fun createGeofenceObjects(entry: Map.Entry<String, LatLng>) {
        // [START android_location_geofencing_create_objects]
        geofenceList.add(
            Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(entry.key)
                // Set the circular region of this geofence.
                .setCircularRegion(
                    entry.value.latitude,
                    entry.value.longitude,
                    Constants.GEOFENCE_RADIUS_IN_METERS
                )
                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
                )
                // Create the geofence.
                .build()
        )
        // [END android_location_geofencing_create_objects]
    }

    // [START android_location_geofencing_request]
    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }
    // [END android_location_geofencing_request]

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun addGeofences() {
        // [START android_location_geofencing_add]
        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)
            .addOnSuccessListener {
                // Geofences added.
                // ...
            }
            .addOnFailureListener {
                // Failed to add geofences.
                // ...
            }
        // [END android_location_geofencing_add]
    }

    private fun removeGeofences() {
        // [START android_location_geofencing_remove]
        geofencingClient.removeGeofences(geofencePendingIntent)
            .addOnSuccessListener {
                // Geofences removed.
                // ...
            }
            .addOnFailureListener {
                // Failed to remove geofences.
                // ...
            }
        // [END android_location_geofencing_remove]
    }
}

private object PendingIntentSnippet {
    // [START android_location_geofencing_pending_intent]
    class MainActivity : ComponentActivity() {
        // ...
        private val geofencePendingIntent: PendingIntent by lazy {
            // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
            // addGeofences() and removeGeofences().
            val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                // Starting on Android S+ the pending intent has to be mutable.
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
            PendingIntent.getBroadcast(this, 0, intent, flags)
        }
    }
    // [END android_location_geofencing_pending_intent]
}

// [START android_location_geofencing_broadcast_receiver]
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    // ...
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) } ?: return
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                this,
                geofenceTransition,
                triggeringGeofences
            )

            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails)
            Log.i(TAG, geofenceTransitionDetails)
        } else {
            // Log the error.
            Log.e(
                TAG,
                context?.getString(
                    R.string.geofence_transition_invalid_type,
                    geofenceTransition
                ).toString()
            )
        }
    }
}
// [END android_location_geofencing_broadcast_receiver]

private data class LatLng(val latitude: Double, val longitude: Double)

private object Constants {
    const val GEOFENCE_RADIUS_IN_METERS = 100f
    const val GEOFENCE_EXPIRATION_IN_MILLISECONDS = 3600000L
}

private fun getGeofenceTransitionDetails(
    receiver: BroadcastReceiver,
    geofenceTransition: Int,
    triggeringGeofences: List<Geofence>?
): String = "$receiver: $geofenceTransition (${triggeringGeofences?.size})"

private fun sendNotification(details: String) {
    Log.d(TAG, details)
}
