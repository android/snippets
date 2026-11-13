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

package com.example.media

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.util.Log
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteDescriptor
import androidx.mediarouter.media.MediaRouteProvider
import androidx.mediarouter.media.MediaRouteProviderDescriptor
import androidx.mediarouter.media.MediaRouteProviderService
import androidx.mediarouter.media.MediaRouter
import androidx.mediarouter.media.MediaRouter.ControlRequestCallback

private const val TAG = "MediaRouteProviderOverview"

private object ProviderService {
    // [START android_media_routing_provider_service]
    class SampleMediaRouteProviderService : MediaRouteProviderService() {

        override fun onCreateMediaRouteProvider(): MediaRouteProvider {
            return SampleMediaRouteProvider(this)
        }
    }
    // [END android_media_routing_provider_service]

    private class SampleMediaRouteProvider(context: Context) : MediaRouteProvider(context)
}

private object ProviderRouteCategories {
    // [START android_media_routing_provider_route_categories]
    class SampleMediaRouteProvider(context: Context) : MediaRouteProvider(context) {

        companion object {
            private val CONTROL_FILTERS_BASIC: ArrayList<IntentFilter> = IntentFilter().run {
                addCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                arrayListOf(this)
            }
        }
    }
    // [END android_media_routing_provider_route_categories]
}

private object ProviderMediaTypes {
    // [START android_media_routing_provider_media_types]
    class SampleMediaRouteProvider(context: Context) : MediaRouteProvider(context) {

        companion object {

            private fun IntentFilter.addDataTypeUnchecked(type: String) {
                try {
                    addDataType(type)
                } catch (ex: IntentFilter.MalformedMimeTypeException) {
                    throw RuntimeException(ex)
                }
            }

            private val CONTROL_FILTERS_BASIC: ArrayList<IntentFilter> = IntentFilter().run {
                addCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                addAction(MediaControlIntent.ACTION_PLAY)
                addDataScheme("http")
                addDataScheme("https")
                addDataScheme("rtsp")
                addDataTypeUnchecked("video/*")
                arrayListOf(this)
            }
        }
        // ...
    }
    // [END android_media_routing_provider_media_types]
}

private object ProviderPlaybackControls {
    // [START android_media_routing_provider_playback_controls]
    class SampleMediaRouteProvider(context: Context) : MediaRouteProvider(context) {

        companion object {
            // ...
            private val CONTROL_FILTERS_BASIC: ArrayList<IntentFilter> = run {
                // [START_EXCLUDE silent]
                /*
                // [END_EXCLUDE]
                val videoPlayback: IntentFilter = ...
                // [START_EXCLUDE silent]
                 */
                val videoPlayback = IntentFilter()
                // [END_EXCLUDE]
                // ...
                val playControls = IntentFilter().apply {
                    addCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                    addAction(MediaControlIntent.ACTION_SEEK)
                    addAction(MediaControlIntent.ACTION_GET_STATUS)
                    addAction(MediaControlIntent.ACTION_PAUSE)
                    addAction(MediaControlIntent.ACTION_RESUME)
                    addAction(MediaControlIntent.ACTION_STOP)
                }
                arrayListOf(videoPlayback, playControls)
            }
        }
        // ...
    }
    // [END android_media_routing_provider_playback_controls]
}

private object ProviderDescriptor {
    // [START android_media_routing_provider_descriptor]
    class SampleMediaRouteProvider(context: Context) : MediaRouteProvider(context) {

        init {
            publishRoutes()
        }

        private fun publishRoutes() {
            val resources = context.resources
            val routeName: String = resources.getString(R.string.variable_volume_basic_route_name)
            val routeDescription: String = resources.getString(R.string.sample_route_description)
            // Create a route descriptor using previously created IntentFilters.
            val routeDescriptor: MediaRouteDescriptor =
                MediaRouteDescriptor.Builder(VARIABLE_VOLUME_BASIC_ROUTE_ID, routeName)
                    .setDescription(routeDescription)
                    .addControlFilters(CONTROL_FILTERS_BASIC)
                    .setPlaybackStream(AudioManager.STREAM_MUSIC)
                    .setPlaybackType(MediaRouter.RouteInfo.PLAYBACK_TYPE_REMOTE)
                    .setVolumeHandling(MediaRouter.RouteInfo.PLAYBACK_VOLUME_VARIABLE)
                    .setVolumeMax(VOLUME_MAX)
                    .setVolume(mVolume)
                    .build()
            // Add the route descriptor to the provider descriptor.
            val providerDescriptor: MediaRouteProviderDescriptor =
                MediaRouteProviderDescriptor.Builder()
                    .addRoute(routeDescriptor)
                    .build()

            // Publish the descriptor to the framework.
            descriptor = providerDescriptor
        }
        // [START_EXCLUDE]
        private var mVolume = 0

        companion object {
            private const val VARIABLE_VOLUME_BASIC_ROUTE_ID = "variable_basic"
            private const val VOLUME_MAX = 10
            private val CONTROL_FILTERS_BASIC: ArrayList<IntentFilter> = IntentFilter().run {
                addCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                arrayListOf(this)
            }
        }
        // [END_EXCLUDE]
    }
    // [END android_media_routing_provider_descriptor]
}

private object ProviderControlRequest {
    // [START android_media_routing_provider_control_request]
    private class SampleRouteController : MediaRouteProvider.RouteController() {
        // [START_EXCLUDE]
        private val sessionManager = SessionManager()

        private fun handlePlay(intent: Intent, callback: ControlRequestCallback?) = false
        private fun handleEnqueue(intent: Intent, callback: ControlRequestCallback?) = false
        private fun handleRemove(intent: Intent, callback: ControlRequestCallback?) = false
        private fun handleSeek(intent: Intent, callback: ControlRequestCallback?) = false
        private fun handleGetStatus(intent: Intent, callback: ControlRequestCallback?) = false
        private fun handlePause(intent: Intent, callback: ControlRequestCallback?) = false
        private fun handleResume(intent: Intent, callback: ControlRequestCallback?) = false
        private fun handleStop(intent: Intent, callback: ControlRequestCallback?) = false
        private fun handleStartSession(intent: Intent, callback: ControlRequestCallback?) = false
        private fun handleGetSessionStatus(intent: Intent, callback: ControlRequestCallback?) = false
        private fun handleEndSession(intent: Intent, callback: ControlRequestCallback?) = false
        // [END_EXCLUDE]

        override fun onControlRequest(
            intent: Intent,
            callback: MediaRouter.ControlRequestCallback?
        ): Boolean {
            return if (intent.hasCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)) {
                val action = intent.action
                when (action) {
                    MediaControlIntent.ACTION_PLAY -> handlePlay(intent, callback)
                    MediaControlIntent.ACTION_ENQUEUE -> handleEnqueue(intent, callback)
                    MediaControlIntent.ACTION_REMOVE -> handleRemove(intent, callback)
                    MediaControlIntent.ACTION_SEEK -> handleSeek(intent, callback)
                    MediaControlIntent.ACTION_GET_STATUS -> handleGetStatus(intent, callback)
                    MediaControlIntent.ACTION_PAUSE -> handlePause(intent, callback)
                    MediaControlIntent.ACTION_RESUME -> handleResume(intent, callback)
                    MediaControlIntent.ACTION_STOP -> handleStop(intent, callback)
                    MediaControlIntent.ACTION_START_SESSION -> handleStartSession(intent, callback)
                    MediaControlIntent.ACTION_GET_SESSION_STATUS ->
                        handleGetSessionStatus(intent, callback)
                    MediaControlIntent.ACTION_END_SESSION -> handleEndSession(intent, callback)
                    else -> false
                }.also {
                    Log.d(TAG, sessionManager.toString())
                }
            } else {
                false
            }
        }
        // ...
    }
    // [END android_media_routing_provider_control_request]

    private class SessionManager
}
