package com.example.media.camera

import android.content.Context
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.takePicture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CameraPreviewViewModel(private val appContext: Context): ViewModel() {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private lateinit var processCameraProvider: ProcessCameraProvider
    private val previewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.value = newSurfaceRequest
        }
    }
    private val captureUseCase = ImageCapture.Builder().build()
    private val useCaseGroup = UseCaseGroup.Builder().apply {
        addUseCase(previewUseCase)
        addUseCase(captureUseCase)
    }.build()

    private var runningCameraJob: Job? = null

    fun startCamera() {
        stopCamera()
        runningCameraJob = viewModelScope.launch {
            processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
            processCameraProvider.runWith(CameraSelector.DEFAULT_BACK_CAMERA, useCaseGroup) {
                awaitCancellation()
            }
        }
    }

    fun stopCamera() {
        runningCameraJob?.apply {
            if (isActive) {
                cancel()
            }
        }
    }

    fun takePicture() {
        viewModelScope.launch {
            val imageProxy = captureUseCase.takePicture()
            // Do something with the image
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreviewScreen(modifier: Modifier = Modifier) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        CameraPreviewContent(modifier)
    } else {
        Column(modifier) {
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "The camera is important for this app. Please grant the permission."
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Camera permission required for this feature to be available. " +
                    "Please grant the permission"
            }
            Text(textToShow)
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}

@Composable
fun CameraPreviewContent(modifier: Modifier = Modifier) {
    val viewModel = CameraPreviewViewModel(LocalContext.current.applicationContext)
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()

    LifecycleStartEffect(Unit) {
        viewModel.startCamera()
        onStopOrDispose {
            viewModel.stopCamera()
        }
    }

    surfaceRequest?.let { CameraPreview(modifier, it) }
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    surfaceRequest: SurfaceRequest
) {
    CameraXViewfinder(surfaceRequest, modifier)
}