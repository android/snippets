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

package com.example.pbl.kotlin

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.android.billingclient.api.DeveloperProvidedBillingDetails

private fun migrateGpblv9(context: Context, details: DeveloperProvidedBillingDetails) {
    // [START android_playbilling_migrate_v9_link_uri_nullability]
    val linkUri = details.getLinkUri()
    if (!linkUri.isNullOrEmpty()) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUri))
        context.startActivity(intent)
    }
    // [END android_playbilling_migrate_v9_link_uri_nullability]
}
