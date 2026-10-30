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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

private object PendingIntentMitigations {
    fun createExplicitPendingIntent(
        context: Context,
        intentAction: String,
        packageName: String,
        className: String,
    ) {
        // [START android_security_ipc_pending_intent_explicit]
        val intent = Intent(intentAction)

        // Or other component setting APIs e.g. setComponent, setClass.
        intent.setClassName(packageName, className)

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                /* requestCode = */ 0,
                intent, /* flags = */ PendingIntent.FLAG_IMMUTABLE
            )
        // [END android_security_ipc_pending_intent_explicit]
    }

    fun createImmutablePendingIntent(context: Context, intentAction: String) {
        // [START android_security_ipc_pending_intent_immutable]
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                /* requestCode = */ 0,
                Intent(intentAction),
                PendingIntent.FLAG_IMMUTABLE
            )
        // [END android_security_ipc_pending_intent_immutable]
    }

    fun createOneShotPendingIntent(context: Context, intentAction: String) {
        // [START android_security_ipc_pending_intent_one_shot]
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                /* requestCode = */ 0,
                Intent(intentAction),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
            )
        // [END android_security_ipc_pending_intent_one_shot]
    }
}
