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

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.subspace.SpatialGltfModel
import androidx.xr.compose.subspace.SpatialGltfModelSource
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.rememberSpatialGltfModelState
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Quaternion
import androidx.xr.runtime.math.Vector4
import androidx.xr.scenecore.AlphaMode
import androidx.xr.scenecore.KhronosPbrMaterial
import androidx.xr.scenecore.Texture
import java.nio.file.Paths
import kotlin.io.path.Path

@Composable
fun SpatialGltfModelExample(){
    val xrSession = checkNotNull(LocalSession.current)
    val degrees = 1f

    // [START androidxr_compose_SpatialGltfModelState]
    val modelState = rememberSpatialGltfModelState(
        source = SpatialGltfModelSource.fromPath(
            Paths.get("models/bugdroid_animated_wave.glb")
        )
    )
    // [END androidxr_compose_SpatialGltfModelState]

    // [START androidxr_compose_SpatialGltfModelMaterial]
    // Retrieve the list of nodes (individual components/meshes) defined within the glTF model.
    val entityNodes = modelState.nodes

    // Find a specific node by name to apply modifications, such as material overrides.
    val node = entityNodes.find { it.name == "node_name" }

    // Maintain a reference to the custom material to avoid re-creating it on every recomposition.
    var pbrMaterial by remember { mutableStateOf<KhronosPbrMaterial?>(null) }

    // Create and apply the custom material once the session is ready and the target node is available.
    LaunchedEffect(node) {
        val material = pbrMaterial ?: KhronosPbrMaterial.create(
            session = xrSession,
            alphaMode = AlphaMode.OPAQUE
        ).also {
            pbrMaterial = it
            // Load a texture
            val texture = Texture.create(
                session = xrSession,
                path = Path("textures/texture_name.png")
            )

            // Configure occlusion to define how the material surface handles ambient lighting.
            it.setOcclusionTexture(
                texture = texture,
                strength = 1.0f
            )

            // Apply a base color factor (RGBA) to tint the model.
            it.setBaseColorFactor(
                Vector4(
                    x = 0.5f,
                    y = 0.5f,
                    z = 1.0f,
                    w = 1.0f
                )
            )
        }

        // Apply the custom PBR material to the specific node, overriding its original glTF material.
        node?.setMaterialOverride(
            material = material
        )
    }
    // [END androidxr_compose_SpatialGltfModelMaterial]

    // [START androidxr_compose_SpatialGltfModelIntrospection]
    val arrows = modelState.nodes.find {
        it.name == "Arrows"
    }

    LaunchedEffect(arrows, degrees) {
        val rotation = Quaternion.fromEulerAngles(degrees, 0f, degrees)
        arrows?.localPose =
            Pose(arrows.localPose.translation, rotation)
    }
    // [END androidxr_compose_SpatialGltfModelIntrospection]

    // [START androidxr_compose_SpatialGltfModelLoad]
    // Render the 3D model into the spatial subspace.
    SpatialGltfModel(state = modelState, modifier = SubspaceModifier)
    // [END androidxr_compose_SpatialGltfModelLoad]
}
