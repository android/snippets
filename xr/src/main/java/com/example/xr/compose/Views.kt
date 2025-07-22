/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.xr.compose

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.depth
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.width
import androidx.xr.runtime.Session
import androidx.xr.runtime.math.IntSize2d
import androidx.xr.scenecore.PanelEntity
import com.example.xr.R

private class MyCustomView(context: Context) : View(context)

private class ActivityWithSubspaceContent : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START androidxr_compose_ActivityWithSubspaceContent]
        setContent {
            Subspace {
                SpatialPanel(
                    modifier = SubspaceModifier.height(500.dp).width(500.dp).depth(25.dp)
                ) { MyCustomView(this@ActivityWithSubspaceContent) }
            }
        }
        // [END androidxr_compose_ActivityWithSubspaceContent]
    }
}

private class FragmentWithComposeView() : Fragment() {
    // [START androidxr_compose_FragmentWithComposeView]
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.example_fragment, container, false)
        view.findViewById<ComposeView>(R.id.compose_view).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                SpatialPanel(SubspaceModifier.height(500.dp).width(500.dp)) {
                    Text("Spatial Panel with Orbiter")
                }
            }
        }
        return view
    }
    // [END androidxr_compose_FragmentWithComposeView]
}

fun ComponentActivity.PanelEntityWithView(xrSession: Session) {
    // [START androidxr_compose_PanelEntityWithView]
    val panelContent = MyCustomView(this)
    val panelEntity = PanelEntity.create(
        session = xrSession,
        view = panelContent,
        pixelDimensions = IntSize2d(500, 500),
        name = "panel entity"
    )
    // [END androidxr_compose_PanelEntityWithView]
}
