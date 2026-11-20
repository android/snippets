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

package com.example.identity.credentialmanager

import android.content.Context
import com.google.android.gms.appset.AppSet
import com.google.android.gms.appset.AppSetIdClient
import com.google.android.gms.appset.AppSetIdInfo
import com.google.android.gms.tasks.Task

fun fetchAppSetId(applicationContext: Context) {
    // [START app_set_id]
    val client = AppSet.getClient(applicationContext) as AppSetIdClient
    val task: Task<AppSetIdInfo> = client.appSetIdInfo as Task<AppSetIdInfo>

    task.addOnSuccessListener({
        // Determine current scope of app set ID.
        val scope: Int = it.scope

        // Read app set ID value, which uses version 4 of the
        // universally unique identifier (UUID) format.
        val id: String = it.id
    })
    // [END app_set_id]
}
