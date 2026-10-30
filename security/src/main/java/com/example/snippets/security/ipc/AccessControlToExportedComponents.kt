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

package com.example.snippets.security.ipc

import android.Manifest
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

private object ExportedComponentAccessControl {
    // [START android_security_ipc_exported_components_enforce_permission]
    class MyExportService : Service() {

        private val binder = MyExportBinder()

        override fun onBind(intent: Intent): IBinder? {
            // Enforce calling app has the required permission.
            enforceCallingPermission(Manifest.permission.READ_CONTACTS, "Calling app doesn't have READ_CONTACTS permission.")
            // Permission is enforced, proceed with export logic.
            return binder
        }

        // Inner class for your Binder implementation.
        private inner class MyExportBinder : Binder() {
            // Permission is enforced, proceed with export logic.
        }
    }
    // [END android_security_ipc_exported_components_enforce_permission]
}
