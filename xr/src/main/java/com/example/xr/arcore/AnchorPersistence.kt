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

package com.example.xr.arcore

import androidx.xr.arcore.Anchor
import androidx.xr.arcore.AnchorCreateSuccess
import androidx.xr.runtime.Session
import java.util.UUID

private suspend fun persistAnchor(anchor: Anchor) {
    // [START androidxr_arcore_anchor_persist]
    val uuid = anchor.persist()
    // [END androidxr_arcore_anchor_persist]
}

private fun loadAnchor(session: Session, uuid: UUID) {
    // [START androidxr_arcore_anchor_load]
    when (val result = Anchor.load(session, uuid)) {
        is AnchorCreateSuccess -> {
            // Loading was successful. The anchor is stored in result.anchor.
        }
        else -> {
            // handle failure
        }
    }
    // [END androidxr_arcore_anchor_load]
}

private fun unpersistAnchor(session: Session, uuid: UUID) {
    // [START androidxr_arcore_anchor_unpersist]
    Anchor.unpersist(session, uuid)
    // [END androidxr_arcore_anchor_unpersist]
}

private fun getPersistedAnchorUuids(session: Session) {
    // [START androidxr_arcore_anchor_get_uuids]
    val uuids = Anchor.getPersistedAnchorUuids(session)
    // [END androidxr_arcore_anchor_get_uuids]
}
