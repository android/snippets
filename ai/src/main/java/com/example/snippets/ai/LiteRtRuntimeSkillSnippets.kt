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

package com.example.snippets.ai

// [START android_litert_runtime_skill_imports]
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel
import com.google.ai.edge.litert.Environment
import com.google.ai.edge.litert.LiteRtException
import com.google.ai.edge.litert.TensorBuffer
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
// [END android_litert_runtime_skill_imports]

// [START android_litert_runtime_skill_classifier]
// Replace with your model's input element count (here 224 x 224 x 3 RGB).
const val INPUT_SIZE = 224 * 224 * 3
private val environment by lazy { Environment.create() }

class Classifier(context: Context, accelerator: Accelerator) : AutoCloseable {
    private val model = CompiledModel.create(
        context.assets,
        "model.tflite", // File in app/src/main/assets/.
        CompiledModel.Options(accelerator),
        environment
    )
    private val inputs = mutableListOf<TensorBuffer>()
    private val outputs = mutableListOf<TensorBuffer>()

    init {
        try {
            inputs += model.createInputBuffers()
            outputs += model.createOutputBuffers()
            infer(FloatArray(INPUT_SIZE))
        } catch (e: Exception) {
            close()
            throw e
        }
    }

    fun infer(input: FloatArray): FloatArray {
        inputs[0].writeFloat(input)
        model.run(inputs, outputs)
        return outputs[0].readFloat()
    }

    override fun close() {
        (inputs + outputs).forEach { it.close() }
        model.close()
    }
}
// [END android_litert_runtime_skill_classifier]

// [START android_litert_runtime_skill_file_path]
// Loads a model from an on-disk path, such as a fast-follow or on-demand AI
// pack directory or a file downloaded into context.filesDir, in place of the
// asset call in Classifier. Pass the process-wide Environment so the GPU
// accelerator library loads once.
fun createCompiledModelFromFile(
    filePath: String,
    accelerator: Accelerator,
    environment: Environment
): CompiledModel = CompiledModel.create(
    filePath,
    CompiledModel.Options(accelerator),
    environment
)
// [END android_litert_runtime_skill_file_path]

// [START android_litert_runtime_skill_viewmodel]
data class UiState(
    val ready: Boolean = false,
    val accelerator: Accelerator = Accelerator.CPU,
    val result: List<Float>? = null,
    val error: String? = null
)

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val executor = Executors.newSingleThreadExecutor()
    private val scope = CoroutineScope(
        SupervisorJob() + executor.asCoroutineDispatcher()
    )
    private var classifier: Classifier? = null
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(preferred: Accelerator = Accelerator.GPU) {
        scope.launch {
            if (classifier != null) return@launch
            val appContext = getApplication<Application>()
            val selected = try {
                classifier = Classifier(appContext, preferred)
                preferred
            } catch (e: LiteRtException) {
                if (preferred != Accelerator.CPU) {
                    try {
                        classifier = Classifier(appContext, Accelerator.CPU)
                        Accelerator.CPU
                    } catch (cpuError: LiteRtException) {
                        _uiState.value = UiState(error = cpuError.message)
                        return@launch
                    }
                } else {
                    _uiState.value = UiState(error = e.message)
                    return@launch
                }
            }
            _uiState.value = UiState(ready = true, accelerator = selected)
        }
    }

    fun classify(input: FloatArray) {
        scope.launch {
            try {
                classifier?.infer(input)?.let { result ->
                    _uiState.update {
                        it.copy(result = result.toList(), error = null)
                    }
                }
            } catch (e: LiteRtException) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    override fun onCleared() {
        scope.launch { classifier?.close() }.invokeOnCompletion {
            scope.cancel()
        }
        executor.shutdown()
    }
}
// [END android_litert_runtime_skill_viewmodel]

// [START android_litert_runtime_skill_screen]
@Composable
fun MainScreen(
    bitmap: Bitmap,
    viewModel: MainViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load(Accelerator.GPU) }
    Column {
        state.error?.let { Text(it) }
        Button(
            onClick = { viewModel.classify(preprocess(bitmap)) },
            enabled = state.ready
        ) {
            Text("Run (${state.accelerator})")
        }
        state.result?.let { scores ->
            Text("Top class: ${scores.indices.maxBy { i -> scores[i] }}")
        }
    }
}
// [END android_litert_runtime_skill_screen]

// [START android_litert_runtime_skill_preprocess]
fun preprocess(
    bitmap: Bitmap,
    size: Int = 224,
    mean: Float = 127.5f,
    std: Float = 127.5f
): FloatArray {
    val scaled = checkNotNull(
        Bitmap.createScaledBitmap(bitmap, size, size, true)
            .copy(Bitmap.Config.ARGB_8888, false)
    )
    val pixels = IntArray(size * size).also {
        scaled.getPixels(it, 0, size, 0, 0, size, size)
    }
    val out = FloatArray(size * size * 3)
    pixels.forEachIndexed { i, p ->
        out[i * 3] = ((p shr 16 and 0xFF) - mean) / std
        out[i * 3 + 1] = ((p shr 8 and 0xFF) - mean) / std
        out[i * 3 + 2] = ((p and 0xFF) - mean) / std
    }
    return out
}
// [END android_litert_runtime_skill_preprocess]
