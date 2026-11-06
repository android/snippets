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

package com.example.compose.snippets.intents

import android.app.Activity
import android.content.Intent
import android.net.Uri

private class GoogleMapsIntentsActivity : Activity() {
    fun requestStreetView() {
        // [START android_maps_intents_street_view_request]
        // Create a Uri from an intent string. Use the result to create an Intent.
        val gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988")
        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW.
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        // Make the Intent explicit by setting the Google Maps package.
        mapIntent.setPackage("com.google.android.apps.maps")
        // Attempt to start an activity that can handle the Intent.
        startActivity(mapIntent)
        // [END android_maps_intents_street_view_request]
    }

    fun checkResolveActivity(mapIntent: Intent) {
        // [START android_maps_intents_resolve_activity]
        mapIntent.resolveActivity(packageManager)?.let {
            // ...
        }
        // [END android_maps_intents_resolve_activity]
    }

    fun showSanFranciscoWithResolve() {
        // [START android_maps_intents_san_francisco_resolve]
        val gmmIntentUri = Uri.parse("geo:37.7749,-122.4194")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.resolveActivity(packageManager)?.let {
            startActivity(mapIntent)
        }
        // [END android_maps_intents_san_francisco_resolve]
    }

    fun encodeQuery() {
        // [START android_maps_intents_uri_encode]
        val gmmIntentUri =
            Uri.parse("geo:37.7749,-122.4192?q=" + Uri.encode("1st & Pike, Seattle"))
        // [END android_maps_intents_uri_encode]
    }

    fun displayMap() {
        // [START android_maps_intents_display_map]
        // Creates an Intent that loads a map of San Francisco.
        val gmmIntentUri = Uri.parse("geo:37.7749,-122.4194")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [END android_maps_intents_display_map]
    }

    fun categoricalSearchNearby() {
        // [START android_maps_intents_categorical_search]
        // Search for restaurants nearby.
        val gmmIntentUri = Uri.parse("geo:0,0?q=restaurants")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [START_EXCLUDE silent]
    }

    fun categoricalSearchSanFrancisco() {
        // [END_EXCLUDE]
        // Search for restaurants in San Francisco.
        val gmmIntentUri =
            Uri.parse("geo:37.7749,-122.4194?q=restaurants")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [END android_maps_intents_categorical_search]
    }

    fun categoricalSearchZoom() {
        // [START android_maps_intents_categorical_search_zoom]
        val gmmIntentUri =
            Uri.parse("geo:37.7749,-122.4194?z=10&q=restaurants")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [END android_maps_intents_categorical_search_zoom]
    }

    fun locationSearch() {
        // [START android_maps_intents_location_search]
        val gmmIntentUri =
            Uri.parse("geo:0,0?q=1600 Amphitheatre Parkway, Mountain+View, California")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [END android_maps_intents_location_search]
    }

    fun locationSearchUnbiased() {
        // [START android_maps_intents_location_search_unbiased]
        // Searching for 'Main Street' returns too many results.
        val gmmIntentUri = Uri.parse("geo:0,0?q=101+main+street")
        // [END android_maps_intents_location_search_unbiased]
    }

    fun locationSearchBiased() {
        // [START android_maps_intents_location_search_biased]
        // Searches for 'Main Street' near San Francisco.
        val gmmIntentUri =
            Uri.parse("geo:37.7749,-122.4194?q=101+main+street")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [END android_maps_intents_location_search_biased]
    }

    fun locationSearchLabel() {
        // [START android_maps_intents_location_search_label]
        // Display a label at the location of Google's Sydney office.
        val gmmIntentUri =
            Uri.parse("geo:0,0?q=-33.8666,151.1957(Google+Sydney)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [END android_maps_intents_location_search_label]
    }

    fun locationSearchPlusCode() {
        // [START android_maps_intents_plus_code]
        // Display the location of Google, San Francisco using a global plus code.
        var gmmIntentUri = Uri.parse("http://plus.codes/849VQJQ5+XX")
        // Equivalently, define the same location using a local plus code.
        gmmIntentUri = Uri.parse("https://plus.codes/QJQ5+XX,San%20Francisco")
        // Construct and use the Intent as in the preceding examples.
        // [END android_maps_intents_plus_code]
    }

    fun navigateTarongaZoo() {
        // [START android_maps_intents_navigation_taronga_zoo]
        val gmmIntentUri =
            Uri.parse("google.navigation:q=Taronga+Zoo,+Sydney+Australia")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [END android_maps_intents_navigation_taronga_zoo]
    }

    fun navigateAvoidTollsFerries() {
        // [START android_maps_intents_navigation_avoid_tolls_ferries]
        val gmmIntentUri =
            Uri.parse("google.navigation:q=Taronga+Zoo,+Sydney+Australia&avoid=tf")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [END android_maps_intents_navigation_avoid_tolls_ferries]
    }

    fun navigateBicycling() {
        // [START android_maps_intents_navigation_bicycling]
        val gmmIntentUri =
            Uri.parse("google.navigation:q=Taronga+Zoo,+Sydney+Australia&mode=b")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [END android_maps_intents_navigation_bicycling]
    }

    fun navigateTwoWheeler() {
        // [START android_maps_intents_navigation_two_wheeler]
        val gmmIntentUri =
            Uri.parse("google.navigation:q=Connaught+Place,+New+Delhi,Delhi&mode=l")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [END android_maps_intents_navigation_two_wheeler]
    }

    fun streetViewSwissAlps() {
        // [START android_maps_intents_street_view_examples]
        // Displays an image of the Swiss Alps.
        val gmmIntentUri =
            Uri.parse("google.streetview:cbll=46.414382,10.013988")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [START_EXCLUDE silent]
    }

    fun streetViewMaroubra() {
        // [END_EXCLUDE]
        // Uses a PanoID to show an image from Maroubra beach in Sydney, Australia.
        val gmmIntentUri =
            Uri.parse("google.streetview:panoid=Iaa2JyfIggYAAAQfCZU9KQ")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [START_EXCLUDE silent]
    }

    fun streetViewGiza() {
        // [END_EXCLUDE]
        // Opens Street View between two Pyramids in Giza. The values passed to the
        // cbp parameter angles the camera slightly up, and towards the east.
        val gmmIntentUri =
            Uri.parse("google.streetview:cbll=29.9774614,31.1329645&cbp=0,30,0,0,-15")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        // [END android_maps_intents_street_view_examples]
    }
}
