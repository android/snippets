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

package com.example.example.snippet.views.resources

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.ComponentActivity

private class ProvidingResourcesListActivity : ComponentActivity() {
    private var listAdapter: ArrayAdapter<String>? = null
    private val myarray = arrayOf("Item 1", "Item 2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START android_resources_providing_views_list_adapter]
        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, myarray)
        // [END android_resources_providing_views_list_adapter]
    }
}
