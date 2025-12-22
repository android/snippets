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

package com.example.xr.projected

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GlassesUiState(
    val areVisualsOn: Boolean = true
)
class GlassesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GlassesUiState())
    val uiState: StateFlow<GlassesUiState> = _uiState.asStateFlow()

    fun updateVisuals(visualsOn: Boolean) {
        _uiState.update { it.copy(areVisualsOn = visualsOn) }
    }
}
