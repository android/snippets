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

package com.example.compose.snippets.activities

import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import java.util.concurrent.Executor

private const val TAG = "SecureBal"
private const val REQUEST_CODE = 100

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
private fun senderOptInSnippet(myPendingIntent: PendingIntent) {
    // [START android_activities_secure_bal_sender_opt_in]
    // Sender Side
    val options = ActivityOptions.makeBasic().apply {
        pendingIntentBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOW_IF_VISIBLE
    }

    try {
        myPendingIntent.send(options.toBundle())
    } catch (e: PendingIntent.CanceledException) {
        Log.e(TAG, "The PendingIntent was canceled", e)
    }
    // [END android_activities_secure_bal_sender_opt_in]
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
private fun creatorOptInSnippet(context: Context) {
    // [START android_activities_secure_bal_creator_opt_in]
    // Creator Side
    val intent = Intent(context, MyActivity::class.java)
    val options = ActivityOptions.makeBasic().apply {
        pendingIntentCreatorBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
    }

    val pendingIntent = PendingIntent.getActivity(
        context, REQUEST_CODE, intent,
        PendingIntent.FLAG_IMMUTABLE, options.toBundle()
    )
    // [END android_activities_secure_bal_creator_opt_in]
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
private fun startIntentSenderSnippet(
    context: Context,
    myIntentSender: IntentSender,
    fillInIntent: Intent?,
    flagsMask: Int,
    flagsValues: Int,
    extraFlags: Int
) {
    // [START android_activities_secure_bal_start_intent_sender]
    val options = ActivityOptions.makeBasic().apply {
        pendingIntentBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
    }

    context.startIntentSender(
        myIntentSender, fillInIntent, flagsMask,
        flagsValues, extraFlags, options.toBundle()
    )
    // [END android_activities_secure_bal_start_intent_sender]
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
private fun sendIntentSnippet(
    context: Context,
    myIntentSender: IntentSender,
    code: Int,
    intent: Intent?,
    onFinished: IntentSender.OnFinished?,
    executor: Executor?,
    requiredPermission: String?
) {
    // [START android_activities_secure_bal_send_intent]
    val options = ActivityOptions.makeBasic().apply {
        pendingIntentBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
    }

    myIntentSender.sendIntent(
        context, code, intent, requiredPermission,
        options.toBundle(), executor, onFinished
    )
    // [END android_activities_secure_bal_send_intent]
}

private class StrictModeActivity : ComponentActivity() {
    // [START android_activities_secure_bal_strict_mode]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectBlockedBackgroundActivityLaunch()
                .penaltyLog()
                .build()
        )
    }
    // [END android_activities_secure_bal_strict_mode]
}

private class MyActivity : ComponentActivity()
