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

package com.example.snippets.security.crypto

import android.content.Context
import android.security.ConfirmationCallback
import android.security.ConfirmationPrompt
import java.util.concurrent.Executor

private class ProtectedConfirmationSnippets {
    // [START android_security_protected_confirmation_callback]
    class MyConfirmationCallback : ConfirmationCallback() {

        override fun onConfirmed(dataThatWasConfirmed: ByteArray) {
            super.onConfirmed(dataThatWasConfirmed)
            // Sign dataThatWasConfirmed using your generated signing key.
            // By completing this process, you generate a signed statement.
        }

        override fun onDismissed() {
            super.onDismissed()
            // Handle case where user declined the prompt in the
            // confirmation dialog.
        }

        override fun onCanceled() {
            super.onCanceled()
            // Handle case where your app closed the dialog before the user
            // responded to the prompt.
        }

        override fun onError(e: Throwable?) {
            super.onError(e)
            // Handle the exception that the callback captured.
        }
    }
    // [END android_security_protected_confirmation_callback]

    fun showConfirmationDialog(context: Context) {
        // [START android_security_protected_confirmation_dialog]
        // This data structure varies by app type. This is an example.
        data class ConfirmationPromptData(
            val sender: String,
            val receiver: String,
            val amount: String
        )

        val myExtraData: ByteArray = byteArrayOf()
        val myDialogData = ConfirmationPromptData("Ashlyn", "Jordan", "$500")
        val threadReceivingCallback = Executor { runnable -> runnable.run() }
        val callback = MyConfirmationCallback()

        val dialog = ConfirmationPrompt.Builder(context)
            .setPromptText("${myDialogData.sender}, send ${myDialogData.amount} to ${myDialogData.receiver}?")
            .setExtraData(myExtraData)
            .build()
        dialog.presentPrompt(threadReceivingCallback, callback)
        // [END android_security_protected_confirmation_dialog]
    }
}
