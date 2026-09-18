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

package com.example.compose.snippets.touchinput.scroll

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.compose.snippets.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.roundToInt

// [START android_compose_touchinput_scroll_nested_scroll_interop_compose_parent]
@Composable
private fun NestedScrollInteropComposeParentWithAndroidChildExample() {
    val toolbarHeightPx = with(LocalDensity.current) { ToolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }

    // Sets up the nested scroll connection between the Box composable parent
    // and the child AndroidView containing the RecyclerView
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Updates the toolbar offset based on the scroll to enable
                // collapsible behaviour
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.value + delta
                toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        TopAppBar(
            // [START_EXCLUDE silent]
            title = {},
            // [END_EXCLUDE]
            modifier = Modifier
                .height(ToolbarHeight)
                .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) }
        )

        AndroidView(
            { context ->
                LayoutInflater.from(context)
                    .inflate(R.layout.view_in_compose_nested_scroll_interop, null).apply {
                        with(findViewById<RecyclerView>(R.id.main_list)) {
                            layoutManager = LinearLayoutManager(context, VERTICAL, false)
                            adapter = NestedScrollInteropAdapter()
                        }
                    }.also {
                        // Nested scrolling interop is enabled when
                        // nested scroll is enabled for the root View
                        ViewCompat.setNestedScrollingEnabled(it, true)
                    }
            },
            // ...
        )
    }
}

private class NestedScrollInteropAdapter :
    Adapter<NestedScrollInteropAdapter.NestedScrollInteropViewHolder>() {
    val items = (1..10).map { it.toString() }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NestedScrollInteropViewHolder {
        return NestedScrollInteropViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NestedScrollInteropViewHolder, position: Int) {
        // ...
    }

    class NestedScrollInteropViewHolder(view: View) : ViewHolder(view) {
        fun bind(item: String) {
            // ...
        }
    }
    // ...
    // [START_EXCLUDE silent]
    override fun getItemCount(): Int = items.size
    // [END_EXCLUDE]
}
// [END android_compose_touchinput_scroll_nested_scroll_interop_compose_parent]

// [START android_compose_touchinput_scroll_view_in_compose_interop]
@Composable
fun ViewInComposeNestedScrollInteropExample() {
    Box(
        Modifier
            .fillMaxSize()
            .scrollable(
                rememberScrollableState {
                    // View component deltas should be reflected in Compose
                    // components that participate in nested scrolling
                    it
                },
                Orientation.Vertical
            )
    ) {
        AndroidView(
            { context ->
                LayoutInflater.from(context)
                    .inflate(R.layout.list_item, null)
                    .apply {
                        // Nested scrolling interop is enabled when
                        // nested scroll is enabled for the root View
                        ViewCompat.setNestedScrollingEnabled(this, true)
                    }
            }
        )
    }
}
// [END android_compose_touchinput_scroll_view_in_compose_interop]

// [START android_compose_touchinput_scroll_bottom_sheet_interop]
class BottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_bottom_sheet, container, false)

        rootView.findViewById<ComposeView>(R.id.compose_view).apply {
            setContent {
                val nestedScrollInterop = rememberNestedScrollInteropConnection()
                LazyColumn(
                    Modifier
                        .nestedScroll(nestedScrollInterop)
                        .fillMaxSize()
                ) {
                    item {
                        Text(text = "Bottom sheet title")
                    }
                    items(10) {
                        Text(
                            text = "List item number $it",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            return rootView
        }
    }
}
// [END android_compose_touchinput_scroll_bottom_sheet_interop]

private val ToolbarHeight = 48.dp
