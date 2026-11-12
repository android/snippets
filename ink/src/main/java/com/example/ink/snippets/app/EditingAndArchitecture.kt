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

package com.example.ink.snippets.app

import android.graphics.Matrix as AndroidMatrix
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix as ComposeMatrix
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.brush.compose.createWithComposeColor
import androidx.ink.geometry.AffineTransform
import androidx.ink.geometry.ImmutableAffineTransform
import androidx.ink.geometry.Intersection.intersects
import androidx.ink.geometry.MutableAffineTransform
import androidx.ink.geometry.MutableParallelogram
import androidx.ink.geometry.MutableSegment
import androidx.ink.geometry.MutableVec
import androidx.ink.geometry.compose.from
import androidx.ink.geometry.compose.populateFrom
import androidx.ink.geometry.compose.toOffset
import androidx.ink.geometry.compose.toRect
import androidx.ink.geometry.populateFrom
import androidx.ink.geometry.toRectF
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// [START android_compose_stylus_ink_app_drawing_ui_state]
data class DrawingUiState(
    val strokes: List<Stroke> = emptyList(),
    val documentTitle: String = "",
)
// [END android_compose_stylus_ink_app_drawing_ui_state]

// [START android_compose_stylus_ink_app_drawing_view_model]
class DrawingViewModel(
    private val repository: StrokeRepository,
    val textureStore: AppTextureBitmapStore,
    private val documentId: Long = 1L,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DrawingUiState())
    val uiState: StateFlow<DrawingUiState> = _uiState.asStateFlow()

    private val _selectedBrush = MutableStateFlow(
        Brush.createWithComposeColor(
            family = StockBrushes.pressurePen(),
            color = Color.Black,
            size = 5f,
            epsilon = 0.1f,
        )
    )
    val currentBrush: StateFlow<Brush> = _selectedBrush.asStateFlow()

    fun getCurrentBrush(): Brush = _selectedBrush.value

    private val _isEraserMode = MutableStateFlow(false)
    val isEraserMode: StateFlow<Boolean> = _isEraserMode.asStateFlow()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    private val history = mutableListOf<List<Stroke>>()
    private var historyIndex = -1
    private val maxHistorySize = 50

    private var previousPoint: MutableVec? = null
    private val eraserPadding = 50f

    init {
        viewModelScope.launch {
            val initialStrokes = repository.loadStrokes(documentId)
            _uiState.update { it.copy(strokes = initialStrokes) }
            history.clear()
            history.add(initialStrokes)
            historyIndex = 0
            updateUndoRedoState()
        }
    }

    private fun updateStrokes(newStrokes: List<Stroke>) {
        // Truncate any future redo states when a new edit occurs after undo
        if (historyIndex < history.size - 1) {
            history.subList(historyIndex + 1, history.size).clear()
        }
        history.add(newStrokes)
        historyIndex++

        while (history.size > maxHistorySize) {
            history.removeAt(0)
            historyIndex--
        }

        _uiState.update { it.copy(strokes = newStrokes) }
        updateUndoRedoState()
    }

    private fun updateUndoRedoState() {
        _canUndo.value = historyIndex > 0
        _canRedo.value = historyIndex < history.size - 1
    }

    fun onStrokesFinished(finishedStrokes: List<Stroke>) {
        val current = history.getOrElse(historyIndex) { emptyList() }
        updateStrokes(current + finishedStrokes)
        viewModelScope.launch { saveStrokes() }
    }

    fun undo() {
        if (_canUndo.value) {
            historyIndex--
            _uiState.update { it.copy(strokes = history[historyIndex]) }
            updateUndoRedoState()
            viewModelScope.launch { saveStrokes() }
        }
    }

    fun redo() {
        if (_canRedo.value) {
            historyIndex++
            _uiState.update { it.copy(strokes = history[historyIndex]) }
            updateUndoRedoState()
            viewModelScope.launch { saveStrokes() }
        }
    }

    fun clearStrokes() {
        if (_uiState.value.strokes.isNotEmpty()) {
            updateStrokes(emptyList())
            viewModelScope.launch { saveStrokes() }
        }
    }

    fun setEraserMode(enabled: Boolean) {
        _isEraserMode.value = enabled
    }

    fun startErase() {
        previousPoint = null
    }

    fun endErase() {
        previousPoint = null
        viewModelScope.launch { saveStrokes() }
    }

    fun erase(x: Float, y: Float) {
        val strokesBefore = history.getOrElse(historyIndex) { emptyList() }
        val strokesAfter = eraseIntersectingStrokes(x, y, strokesBefore)
        if (strokesAfter.size != strokesBefore.size) {
            updateStrokes(strokesAfter)
        }
    }

    private fun eraseIntersectingStrokes(
        currentX: Float,
        currentY: Float,
        currentStrokes: List<Stroke>,
    ): List<Stroke> {
        val prev = previousPoint
        previousPoint = MutableVec(currentX, currentY)
        if (prev == null) return currentStrokes

        val segment = MutableSegment(prev, MutableVec(currentX, currentY))
        val parallelogram = MutableParallelogram()
            .populateFromSegmentAndPadding(segment, eraserPadding)

        val strokesToRemove = currentStrokes.filter { stroke ->
            stroke.shape.intersects(parallelogram, AffineTransform.IDENTITY)
        }
        return if (strokesToRemove.isNotEmpty()) {
            currentStrokes - strokesToRemove.toSet()
        } else {
            currentStrokes
        }
    }

    private suspend fun saveStrokes() {
        val strokesToSave = history.getOrElse(historyIndex) { emptyList() }
        repository.saveStrokes(documentId, strokesToSave)
    }
}
// [END android_compose_stylus_ink_app_drawing_view_model]

// [START android_compose_stylus_ink_app_geometry_erase_pan_zoom]
fun populatePointFromOffset(reusableVec: MutableVec, offset: Offset): Offset {
    reusableVec.populateFrom(offset)
    return reusableVec.toOffset()
}

fun eraseWithPanZoom(
    strokes: List<Stroke>,
    screenEraserBox: MutableParallelogram,
    worldToScreenAndroidMatrix: AndroidMatrix,
    worldToScreenComposeMatrix: ComposeMatrix? = null,
): List<Stroke> {
    val meshToEraserTransform: AffineTransform =
        if (worldToScreenComposeMatrix != null) {
            ImmutableAffineTransform.from(worldToScreenComposeMatrix) ?: AffineTransform.IDENTITY
        } else {
            MutableAffineTransform().populateFrom(worldToScreenAndroidMatrix)
        }

    return strokes.filterNot { stroke ->
        stroke.shape.intersects(screenEraserBox, meshToEraserTransform)
    }
}

fun getStrokeBounds(stroke: Stroke): Pair<Rect?, RectF?> {
    val boundingBox = stroke.shape.computeBoundingBox() ?: return null to null
    return boundingBox.toRect() to boundingBox.toRectF()
}
// [END android_compose_stylus_ink_app_geometry_erase_pan_zoom]

// [START android_compose_stylus_ink_app_drawing_screen_wiring]
interface StrokeRepository {
    suspend fun saveStrokes(documentId: Long, strokes: List<Stroke>)
    suspend fun loadStrokes(documentId: Long): List<Stroke>
}

@Composable
fun DrawingScreen(
    viewModel: DrawingViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentBrush by viewModel.currentBrush.collectAsStateWithLifecycle()
    val isEraserMode by viewModel.isEraserMode.collectAsStateWithLifecycle()

    val textureStore = viewModel.textureStore
    val cacheGen by textureStore.generation.collectAsStateWithLifecycle()
    val renderer = remember(textureStore, cacheGen) {
        CanvasStrokeRenderer.create(textureStore)
    }

    CompositionLocalProvider(LocalTextureStore provides textureStore) {
        DrawingSurface(
            strokes = uiState.strokes,
            canvasStrokeRenderer = renderer,
            currentBrush = currentBrush,
            onGetNextBrush = { viewModel.getCurrentBrush() },
            onStrokesFinished = { viewModel.onStrokesFinished(it) },
            isEraserMode = isEraserMode,
            modifier = modifier,
            onErase = { x, y -> viewModel.erase(x, y) },
            onEraseStart = { viewModel.startErase() },
            onEraseEnd = { viewModel.endErase() },
        )
    }
}
// [END android_compose_stylus_ink_app_drawing_screen_wiring]
