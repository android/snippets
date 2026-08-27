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

package com.example.media

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

// [START android_media_ai_enhancement_get_started_checks]
// Verifies if host hardware supports NPU/GPU acceleration
suspend fun EnhancementClient.isDeviceSupportedAsync(): Boolean = suspendCancellableCoroutine { continuation ->
    this.isDeviceSupported()
        .addOnSuccessListener { result -> continuation.resume(result) }
        .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
}
// Verifies the presence of required neural network models
suspend fun EnhancementClient.isModuleInstalledAsync(): Boolean = suspendCancellableCoroutine { continuation ->
    this.isModuleInstalled()
        .addOnSuccessListener { result -> continuation.resume(result) }
        .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
}
// [END android_media_ai_enhancement_get_started_checks]
