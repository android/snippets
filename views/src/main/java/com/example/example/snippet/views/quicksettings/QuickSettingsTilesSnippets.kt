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

package com.example.example.snippet.views.quicksettings

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

// [START android_views_quicksettings_tile_service]
class MyQSTileService : TileService() {

    // Called when the user adds your tile.
    override fun onTileAdded() {
        super.onTileAdded()
    }
    // Called when your app can update your tile.
    override fun onStartListening() {
        super.onStartListening()
    }

    // Called when your app can no longer update your tile.
    override fun onStopListening() {
        super.onStopListening()
    }

    // Called when the user taps on your tile in an active or inactive state.
    override fun onClick() {
        super.onClick()
    }
    // Called when the user removes your tile.
    override fun onTileRemoved() {
        super.onTileRemoved()
    }
}
// [END android_views_quicksettings_tile_service]

class UpdateTileService : TileService() {

    // [START android_views_quicksettings_update_tile]
    data class StateModel(val enabled: Boolean, val label: String, val icon: Icon)

    override fun onStartListening() {
        super.onStartListening()
        val state = getStateFromService()
        qsTile.label = state.label
        qsTile.contentDescription = state.label
        qsTile.state = if (state.enabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.icon = state.icon
        qsTile.updateTile()
    }
    // [END android_views_quicksettings_update_tile]

    private fun getStateFromService(): StateModel = TODO("Read the state your tile reflects.")
}

class HandleTapsTileService : TileService() {

    // [START android_views_quicksettings_handle_taps]
    var counter = 0
    override fun onClick() {
        super.onClick()
        counter++
        qsTile.state = if (counter % 2 == 0) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.label = "Clicked $counter times"
        qsTile.contentDescription = qsTile.label
        qsTile.updateTile()
    }
    // [END android_views_quicksettings_handle_taps]
}
