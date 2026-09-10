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

package com.example.snippets.security.permissions

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

// Stub interface representing AIDL generated interface
interface IMyService {
    fun getData(): String
    fun modifyData(newData: String)

    abstract class Stub : Binder(), IMyService
}

// [START android_security_service_enforce_calling_permission]
class SecureBoundService : Service() {

    private val binder = object : IMyService.Stub() {
        override fun getData(): String {
            // Read-only operation guarded by manifest-level permission
            return "Confidential Data"
        }

        override fun modifyData(newData: String) {
            // MUST use enforceCallingPermission or checkCallingPermission.
            // NEVER use checkCallingOrSelfPermission or enforceCallingOrSelfPermission.
            this@SecureBoundService.enforceCallingPermission(
                "com.example.snippets.permission.WRITE_DATA",
                "Caller lacks WRITE_DATA permission"
            )
            updateInternalState(newData)
            Log.d("SecureBoundService", "Data modified to: $newData with proper WRITE_DATA permission check")
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    private fun updateInternalState(data: String) {
        // Internal state update logic
    }
}
// [END android_security_service_enforce_calling_permission]

typealias FineGrainedBoundService = SecureBoundService
