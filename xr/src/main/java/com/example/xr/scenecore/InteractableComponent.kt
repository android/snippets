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
import androidx.xr.scenecore.Entity
import androidx.xr.scenecore.InputEvent
import androidx.xr.scenecore.InteractableComponent
import java.util.concurrent.Executors

@Suppress("RestrictedApi") // b/416066566
private fun interactableComponentExample(session: Session, entity: Entity) {
    // [START androidxr_scenecore_interactableComponentExample]
    val executor = Executors.newSingleThreadExecutor()
    val interactableComponent = InteractableComponent.create(session, executor) {
        // when the user disengages with the entity with their hands
        if (it.source == InputEvent.Source.SOURCE_HANDS && it.action == InputEvent.Action.ACTION_UP) {
            // increase size with right hand and decrease with left
            if (it.pointerType == InputEvent.Pointer.POINTER_TYPE_RIGHT) {
                entity.setScale(1.5f)
            } else if (it.pointerType == InputEvent.Pointer.POINTER_TYPE_LEFT) {
                entity.setScale(0.5f)
            }
        }
    }
    entity.addComponent(interactableComponent)
    // [END androidxr_scenecore_interactableComponentExample]
}
