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

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.mediarouter.app.MediaRouteActionProvider
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import androidx.mediarouter.media.RemotePlaybackClient

private const val TAG = "MediaRouterOverview"

private object MediaRouterSelector {
    // [START android_media_routing_mediarouter_selector]
    class MediaRouterPlaybackActivity : AppCompatActivity() {

        private var mSelector: MediaRouteSelector? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            // Create a route selector for the type of routes your app supports.
            mSelector = MediaRouteSelector.Builder()
                // These are the framework-supported intents.
                .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                .build()
        }
    }
    // [END android_media_routing_mediarouter_selector]
}

private class MediaRouterOptionsMenuActivity : AppCompatActivity() {
    private val selector: MediaRouteSelector? = null

    // [START android_media_routing_mediarouter_options_menu]
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        // Inflate the menu and configure the media router action provider.
        menuInflater.inflate(R.menu.sample_media_router_menu, menu)

        // Attach the MediaRouteSelector to the menu item.
        val mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item)
        val mediaRouteActionProvider =
            MenuItemCompat.getActionProvider(mediaRouteMenuItem) as MediaRouteActionProvider

        // Attach the MediaRouteSelector that you built in onCreate().
        selector?.also(mediaRouteActionProvider::setRouteSelector)

        // Return true to show the menu.
        return true
    }
    // [END android_media_routing_mediarouter_options_menu]
}

private object MediaRouterCallbacks {
    // [START android_media_routing_mediarouter_callbacks]
    class MediaRouterPlaybackActivity : AppCompatActivity() {

        private var mediaRouter: MediaRouter? = null
        private var mSelector: MediaRouteSelector? = null

        // Variables to hold the currently selected route and its playback client.
        private var mRoute: MediaRouter.RouteInfo? = null
        private var remotePlaybackClient: RemotePlaybackClient? = null

        // Define the Callback object and its methods, save the object in a class variable.
        private val mediaRouterCallback = object : MediaRouter.Callback() {

            override fun onRouteSelected(router: MediaRouter, route: MediaRouter.RouteInfo) {
                Log.d(TAG, "onRouteSelected: route=$route")
                if (route.supportsControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)) {
                    // Stop local playback (if necessary).
                    // ...

                    // Save the new route.
                    mRoute = route

                    // Attach a new playback client.
                    remotePlaybackClient =
                        RemotePlaybackClient(this@MediaRouterPlaybackActivity, route)

                    // Start remote playback (if necessary).
                    // ...
                }
            }

            override fun onRouteUnselected(
                router: MediaRouter,
                route: MediaRouter.RouteInfo,
                reason: Int
            ) {
                Log.d(TAG, "onRouteUnselected: route=$route")
                if (route.supportsControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)) {

                    // Changed route: tear down previous client.
                    mRoute?.also {
                        remotePlaybackClient?.release()
                        remotePlaybackClient = null
                    }

                    // Save the new route.
                    mRoute = route

                    when {
                        reason != MediaRouter.UNSELECT_REASON_ROUTE_CHANGED -> {
                            // Resume local playback (if necessary).
                            // ...
                        }
                    }
                }
            }
        }

        // Retain a pointer to the MediaRouter.
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            // Get the media router service.
            mediaRouter = MediaRouter.getInstance(this)
            // ...
        }

        // Use this callback to run your MediaRouteSelector to generate the
        // list of available media routes.
        override fun onStart() {
            mSelector?.also { selector ->
                mediaRouter?.addCallback(
                    selector, mediaRouterCallback,
                    MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY
                )
            }
            super.onStart()
        }

        // Remove the selector on stop to tell the media router that it no longer
        // needs to discover routes for your app.
        override fun onStop() {
            mediaRouter?.removeCallback(mediaRouterCallback)
            super.onStop()
        }
        // ...
    }
    // [END android_media_routing_mediarouter_callbacks]
}
