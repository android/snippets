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

// Stub interface representing AIDL generated interface
interface IMyService {
    fun getData(): String
    fun modifyData(newData: String)
}

// [START android_security_service_enforce_calling_permission]
class FineGrainedBoundService : Service() {

    private val binder = object : Binder(), IMyService {
        override fun getData(): String {
            // Enforce read permission on the caller
            enforceCallingPermission(
                "com.example.snippets.permission.READ_DATA",
                "Caller does not have READ_DATA permission"
            )
            return "Sensitive data from service"
        }

        override fun modifyData(newData: String) {
            // Enforce write permission on the caller
            enforceCallingPermission(
                "com.example.snippets.permission.WRITE_DATA",
                "Caller does not have WRITE_DATA permission"
            )
            // Perform modification
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}
// [END android_security_service_enforce_calling_permission]
