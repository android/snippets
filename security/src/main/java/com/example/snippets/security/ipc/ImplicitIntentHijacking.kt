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

import android.content.Intent
import androidx.activity.ComponentActivity

private class ExplicitPackageIntentActivity : ComponentActivity() {
    fun launchDocumentCreator() {
        // [START android_security_ipc_implicit_intent_hijacking_set_package]
        val intent = Intent("android.intent.action.CREATE_DOCUMENT").apply {
            addCategory("android.intent.category.OPENABLE")
            setPackage("com.some.packagename")
            setType("*/*")
            putExtra("android.intent.extra.LOCAL_ONLY", true)
            putExtra("android.intent.extra.TITLE", "Some Title")
        }
        startActivity(intent)
        // [END android_security_ipc_implicit_intent_hijacking_set_package]
    }
}
