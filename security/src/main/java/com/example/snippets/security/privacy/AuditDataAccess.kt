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

package com.example.snippets.security.privacy

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.AsyncNotedAppOp
import android.app.SyncNotedAppOp
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

private const val MY_APP_TAG = "MyAppTag"

@SuppressLint("MissingSuperCall")
private class AuditDataAccessActivity : ComponentActivity() {
    // [START android_security_privacy_audit_data_access_callback]
    override fun onCreate(savedInstanceState: Bundle?) {
        val appOpsCallback = object : AppOpsManager.OnOpNotedCallback() {
            private fun logPrivateDataAccess(opCode: String, trace: String) {
                Log.i(
                    MY_APP_TAG,
                    "Private data accessed. " +
                        "Operation: $opCode\nStack Trace:\n$trace"
                )
            }

            override fun onNoted(syncNotedAppOp: SyncNotedAppOp) {
                logPrivateDataAccess(
                    syncNotedAppOp.op,
                    Throwable().stackTrace.toString()
                )
            }

            override fun onSelfNoted(syncNotedAppOp: SyncNotedAppOp) {
                logPrivateDataAccess(
                    syncNotedAppOp.op,
                    Throwable().stackTrace.toString()
                )
            }

            override fun onAsyncNoted(asyncNotedAppOp: AsyncNotedAppOp) {
                logPrivateDataAccess(asyncNotedAppOp.op, asyncNotedAppOp.message)
            }
        }

        val appOpsManager =
            getSystemService(AppOpsManager::class.java) as AppOpsManager
        appOpsManager.setOnOpNotedCallback(mainExecutor, appOpsCallback)
    }
    // [END android_security_privacy_audit_data_access_callback]
}

@SuppressLint("MissingSuperCall")
private object SharePhotoLocationSnippet {
    // [START android_security_privacy_audit_data_access_attribution_context]
    class SharePhotoLocationActivity : AppCompatActivity() {
        lateinit var attributionContext: Context

        override fun onCreate(savedInstanceState: Bundle?) {
            attributionContext = createAttributionContext("sharePhotos")
        }

        fun getLocation() {
            val locationManager = attributionContext.getSystemService(
                LocationManager::class.java
            ) as LocationManager
            // Use "locationManager" to access device location information.
        }
    }
    // [END android_security_privacy_audit_data_access_attribution_context]
}

private fun auditDataAccessWithAttributionTag() {
    // [START android_security_privacy_audit_data_access_log_attribution]
    val appOpsCallback = object : AppOpsManager.OnOpNotedCallback() {
        private fun logPrivateDataAccess(
            opCode: String,
            attributionTag: String?,
            trace: String
        ) {
            Log.i(
                MY_APP_TAG,
                "Private data accessed. " +
                    "Operation: $opCode\n " +
                    "Attribution Tag:$attributionTag\nStack Trace:\n$trace"
            )
        }

        override fun onNoted(syncNotedAppOp: SyncNotedAppOp) {
            logPrivateDataAccess(
                syncNotedAppOp.op,
                syncNotedAppOp.attributionTag,
                Throwable().stackTrace.toString()
            )
        }

        override fun onSelfNoted(syncNotedAppOp: SyncNotedAppOp) {
            logPrivateDataAccess(
                syncNotedAppOp.op,
                syncNotedAppOp.attributionTag,
                Throwable().stackTrace.toString()
            )
        }

        override fun onAsyncNoted(asyncNotedAppOp: AsyncNotedAppOp) {
            logPrivateDataAccess(
                asyncNotedAppOp.op,
                asyncNotedAppOp.attributionTag,
                asyncNotedAppOp.message
            )
        }
    }
    // [END android_security_privacy_audit_data_access_log_attribution]
}
