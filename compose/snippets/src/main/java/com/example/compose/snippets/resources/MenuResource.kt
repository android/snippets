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

package com.example.compose.snippets.resources

import android.view.Menu
import android.view.MenuItem
import androidx.activity.ComponentActivity
import com.example.compose.snippets.R

private class MenuResourceActivity : ComponentActivity() {
    // [START android_resources_menu_inflate]
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.example_menu, menu)
        return true
    }

    fun onGroupItemClick(item: MenuItem) {
        // One of the group items (using the onClick attribute) was clicked.
        // The item parameter passed here indicates which item it is.
        // All other menu item clicks are handled by Activity.onOptionsItemSelected.
    }
    // [END android_resources_menu_inflate]
}
