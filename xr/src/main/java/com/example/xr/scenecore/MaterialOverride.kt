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

package com.example.xr.scenecore

import androidx.xr.runtime.Session
import androidx.xr.runtime.math.Vector4
import androidx.xr.scenecore.AlphaMode
import androidx.xr.scenecore.GltfModelEntity
import androidx.xr.scenecore.KhronosPbrMaterial
import androidx.xr.scenecore.Texture
import kotlin.io.path.Path

private class MaterialOverride(val xrSession: Session) {
    private suspend fun createMaterial() {
        // [START androidxr_scenecore_material_override_createMaterial]
        val material = KhronosPbrMaterial.create(
            session = xrSession,
            alphaMode = AlphaMode.OPAQUE
        )
        // [END androidxr_scenecore_material_override_createMaterial]
    }

    private fun setBaseColor(pbrMaterial: KhronosPbrMaterial) {
        // [START androidxr_scenecore_material_override_setBaseColor]
        pbrMaterial.setBaseColorFactor(
            Vector4(
                x = 0.5f,
                y = 0.0f,
                z = 0.5f,
                w = 0.0f
            )
        )
        // [END androidxr_scenecore_material_override_setBaseColor]
    }

    private suspend fun createTexture() {
        // [START androidxr_scenecore_material_override_createTexture]
        val texture = Texture.create(
            session = xrSession,
            path = Path("textures/white.png")
        )
        // [END androidxr_scenecore_material_override_createTexture]
    }

    private fun setOcclusionTexture(pbrMaterial: KhronosPbrMaterial, texture: Texture) {
        // [START androidxr_scenecore_material_override_setOcclusionTexture]
        pbrMaterial.setOcclusionTexture(
            texture = texture,
            strength = 1.0f
        )
        // [END androidxr_scenecore_material_override_setOcclusionTexture]
    }

    private fun setMaterialOverride(entity: GltfModelEntity, pbrMaterial: KhronosPbrMaterial) {
        // [START androidxr_scenecore_material_override_setMaterialOverride]
        entity.setMaterialOverride(
            material = newMaterial,
            nodeName = "Node Name"
        )
        // [END androidxr_scenecore_material_override_setMaterialOverride]
    }

    private fun clearMaterialOverride(entity: GltfModelEntity) {
        // [START androidxr_scenecore_material_override_clearMaterialOverride]
        entity.clearMaterialOverride(
            nodeName = "Node Name"
        )
        // [END androidxr_scenecore_material_override_clearMaterialOverride]
    }
}
