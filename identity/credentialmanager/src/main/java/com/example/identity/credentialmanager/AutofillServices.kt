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

import android.app.Activity
import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.content.ContentResolver
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillContext
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.service.autofill.SaveRequest
import android.util.Log
import android.view.autofill.AutofillId
import android.view.autofill.AutofillManager.EXTRA_ASSIST_STRUCTURE
import android.view.autofill.AutofillManager.EXTRA_AUTHENTICATION_RESULT
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.core.content.IntentCompat

private const val MY_EXTRA_DATASET_NAME = "my_extra_dataset_name"

private class BasicAutofillService : AutofillService() {

    // [START android_identity_autofill_on_fill_request]
    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        // Get the structure from the request.
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        // Traverse the structure looking for nodes to fill out.
        val parsedStructure: ParsedStructure = parseStructure(structure)

        // Fetch user data that matches the fields.
        val (username: String, password: String) = fetchUserData(parsedStructure)

        // Build the presentation of the datasets.
        val usernamePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
        usernamePresentation.setTextViewText(android.R.id.text1, "my_username")
        val passwordPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
        passwordPresentation.setTextViewText(android.R.id.text1, "Password for my_username")

        // Add a dataset to the response.
        val fillResponse: FillResponse = FillResponse.Builder()
            .addDataset(Dataset.Builder()
                .setValue(
                    parsedStructure.usernameId,
                    AutofillValue.forText(username),
                    usernamePresentation
                )
                .setValue(
                    parsedStructure.passwordId,
                    AutofillValue.forText(password),
                    passwordPresentation
                )
                .build())
            .build()

        // If there are no errors, call onSuccess() and pass the response.
        callback.onSuccess(fillResponse)
    }

    data class ParsedStructure(var usernameId: AutofillId, var passwordId: AutofillId)

    data class UserData(var username: String, var password: String)
    // [END android_identity_autofill_on_fill_request]

    private fun buildMultipleDatasetsResponse(
        parsedStructure: ParsedStructure,
        user1Data: UserData,
        user2Data: UserData,
        username1Presentation: RemoteViews,
        password1Presentation: RemoteViews,
        username2Presentation: RemoteViews,
        password2Presentation: RemoteViews
    ) {
        // [START android_identity_autofill_multiple_datasets]
        // Add multiple datasets to the response.
        val fillResponse: FillResponse = FillResponse.Builder()
            .addDataset(Dataset.Builder()
                .setValue(parsedStructure.usernameId,
                    AutofillValue.forText(user1Data.username), username1Presentation)
                .setValue(parsedStructure.passwordId,
                    AutofillValue.forText(user1Data.password), password1Presentation)
                .build())
            .addDataset(Dataset.Builder()
                .setValue(parsedStructure.usernameId,
                    AutofillValue.forText(user2Data.username), username2Presentation)
                .setValue(parsedStructure.passwordId,
                    AutofillValue.forText(user2Data.password), password2Presentation)
                .build())
            .build()
        // [END android_identity_autofill_multiple_datasets]
    }

    // [START android_identity_autofill_traverse_structure]
    fun traverseStructure(structure: AssistStructure) {
        val windowNodes: List<AssistStructure.WindowNode> =
            structure.run {
                (0 until windowNodeCount).map { getWindowNodeAt(it) }
            }

        windowNodes.forEach { windowNode: AssistStructure.WindowNode ->
            val viewNode: ViewNode? = windowNode.rootViewNode
            traverseNode(viewNode)
        }
    }

    fun traverseNode(viewNode: ViewNode?) {
        if (viewNode?.autofillHints?.isNotEmpty() == true) {
            // If the client app provides autofill hints, you can obtain them using
            // viewNode.getAutofillHints();
        } else {
            // Or use your own heuristics to describe the contents of a view
            // using methods such as getText() or getHint()
        }

        val children: List<ViewNode>? =
            viewNode?.run {
                (0 until childCount).map { getChildAt(it) }
            }

        children?.forEach { childNode: ViewNode ->
            traverseNode(childNode)
        }
    }
    // [END android_identity_autofill_traverse_structure]

    // [START android_identity_autofill_on_save_request]
    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        // Get the structure from the request.
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        // Traverse the structure looking for data to save.
        traverseStructure(structure)

        // Persist the data - if there are no errors, call onSuccess()
        callback.onSuccess()
    }
    // [END android_identity_autofill_on_save_request]

    private fun buildAuthResponse(autofillIds: Array<AutofillId>) {
        // [START android_identity_autofill_auth_response_request]
        val authPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
            setTextViewText(android.R.id.text1, "requires authentication")
        }
        val authIntent = Intent(this, AuthActivity::class.java).apply {
            // Send any additional data required to complete the request.
            putExtra(MY_EXTRA_DATASET_NAME, "my_dataset")
        }

        val intentSender: IntentSender = PendingIntent.getActivity(
            this,
            1001,
            authIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
        ).intentSender

        // Build a FillResponse object that requires authentication.
        val fillResponse: FillResponse = FillResponse.Builder()
            .setAuthentication(autofillIds, intentSender, authPresentation)
            .build()
        // [END android_identity_autofill_auth_response_request]
    }

    private fun parseStructure(structure: AssistStructure): ParsedStructure =
        throw NotImplementedError()

    private fun fetchUserData(parsedStructure: ParsedStructure): UserData =
        throw NotImplementedError()
}

private class SaveInfoAutofillService : AutofillService() {

    private lateinit var parsedStructure: BasicAutofillService.ParsedStructure

    // [START android_identity_autofill_save_info]
    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        // ...
        // Builder object requires a non-null presentation.
        val notUsed = RemoteViews(packageName, android.R.layout.simple_list_item_1)

        val fillResponse: FillResponse = FillResponse.Builder()
            .addDataset(
                Dataset.Builder()
                    .setValue(parsedStructure.usernameId, null, notUsed)
                    .setValue(parsedStructure.passwordId, null, notUsed)
                    .build()
            )
            .setSaveInfo(
                SaveInfo.Builder(
                    SaveInfo.SAVE_DATA_TYPE_USERNAME or SaveInfo.SAVE_DATA_TYPE_PASSWORD,
                    arrayOf(parsedStructure.usernameId, parsedStructure.passwordId)
                ).build()
            )
            .build()
        // ...
    }
    // [END android_identity_autofill_save_info]

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {}
}

private class AuthActivity : ComponentActivity() {

    private fun completeAuthentication() {
        // [START android_identity_autofill_auth_response_result]
        // The data sent by the service and the structure are included in the intent.
        val datasetName: String? = intent.getStringExtra(MY_EXTRA_DATASET_NAME)
        val structure: AssistStructure = IntentCompat.getParcelableExtra(intent, EXTRA_ASSIST_STRUCTURE, AssistStructure::class.java) ?: return
        val parsedStructure: ParsedStructure = parseStructure(structure)
        val (username, password) = fetchUserData(parsedStructure)

        // Build the presentation of the datasets.
        val usernamePresentation =
            RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
                setTextViewText(android.R.id.text1, "my_username")
            }
        val passwordPresentation =
            RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
                setTextViewText(android.R.id.text1, "Password for my_username")
            }

        // Add the dataset to the response.
        val fillResponse: FillResponse = FillResponse.Builder()
            .addDataset(Dataset.Builder()
                .setValue(
                    parsedStructure.usernameId,
                    AutofillValue.forText(username),
                    usernamePresentation
                )
                .setValue(
                    parsedStructure.passwordId,
                    AutofillValue.forText(password),
                    passwordPresentation
                )
                .build()
            ).build()

        val replyIntent = Intent().apply {
            // Send the data back to the service.
            putExtra(MY_EXTRA_DATASET_NAME, datasetName)
            putExtra(EXTRA_AUTHENTICATION_RESULT, fillResponse)
        }

        setResult(Activity.RESULT_OK, replyIntent)
        // [END android_identity_autofill_auth_response_result]
    }

    private fun parseStructure(structure: AssistStructure): ParsedStructure =
        throw NotImplementedError()

    private fun fetchUserData(parsedStructure: ParsedStructure): UserData =
        throw NotImplementedError()
}

private object PaymentAutofill {

    data class ParsedStructure(var creditCardId: AutofillId, var expDateId: AutofillId)

    data class Payment(
        var bank: String,
        var creditCardNumber: String,
        var expirationDate: String
    )

    class PaymentAutofillService : AutofillService() {

        private fun buildCvcAuthResponse(structure: AssistStructure) {
            // [START android_identity_autofill_auth_dataset_cvc_request]
            // Parse the structure and fetch payment data.
            val parsedStructure: ParsedStructure = parseStructure(structure)
            val paymentData: Payment = fetchPaymentData(parsedStructure)

            // Build the presentation that shows the bank and the last four digits of the
            // credit card number, such as 'Bank-1234'.
            val maskedPresentation: String = "${paymentData.bank}-" +
                paymentData.creditCardNumber.substring(paymentData.creditCardNumber.length - 4)
            val authPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
                setTextViewText(android.R.id.text1, maskedPresentation)
            }

            // Prepare an intent that displays the UI that asks for the CVC.
            val cvcIntent = Intent(this, CvcActivity::class.java)
            val cvcIntentSender: IntentSender = PendingIntent.getActivity(
                this,
                1001,
                cvcIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
            ).intentSender

            // Build a FillResponse object that includes a Dataset that requires authentication.
            val fillResponse: FillResponse = FillResponse.Builder()
                .addDataset(
                    Dataset.Builder()
                        // The values in the dataset are replaced by the actual
                        // data once the user provides the CVC.
                        .setValue(parsedStructure.creditCardId, null, authPresentation)
                        .setValue(parsedStructure.expDateId, null, authPresentation)
                        .setAuthentication(cvcIntentSender)
                        .build()
                ).build()
            // [END android_identity_autofill_auth_dataset_cvc_request]
        }

        override fun onFillRequest(
            request: FillRequest,
            cancellationSignal: CancellationSignal,
            callback: FillCallback
        ) {}

        override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {}
    }

    class CvcActivity : ComponentActivity() {

        private fun returnCvcDataset(structure: AssistStructure) {
            // [START android_identity_autofill_auth_dataset_cvc_result]
            // Parse the structure and fetch payment data.
            val parsedStructure: ParsedStructure = parseStructure(structure)
            val paymentData: Payment = fetchPaymentData(parsedStructure)

            // Build a non-null RemoteViews object to use as the presentation when
            // creating the Dataset object. This presentation isn't actually used, but the
            // Builder object requires a non-null presentation.
            val notUsed = RemoteViews(packageName, android.R.layout.simple_list_item_1)

            // Create a dataset with the credit card number and expiration date.
            val responseDataset: Dataset = Dataset.Builder()
                .setValue(
                    parsedStructure.creditCardId,
                    AutofillValue.forText(paymentData.creditCardNumber),
                    notUsed
                )
                .setValue(
                    parsedStructure.expDateId,
                    AutofillValue.forText(paymentData.expirationDate),
                    notUsed
                )
                .build()

            val replyIntent = Intent().apply {
                putExtra(EXTRA_AUTHENTICATION_RESULT, responseDataset)
            }
            // [END android_identity_autofill_auth_dataset_cvc_result]
        }
    }

    private fun parseStructure(structure: AssistStructure): ParsedStructure =
        throw NotImplementedError()

    private fun fetchPaymentData(parsedStructure: ParsedStructure): Payment =
        throw NotImplementedError()
}

private class ChromeAutofillSettingsActivity : ComponentActivity() {

    private fun readChromeSettings() {
        // [START android_identity_autofill_chrome_read_settings]
        val CHROME_CHANNEL_PACKAGE = "com.android.chrome" // Chrome Stable.
        val CONTENT_PROVIDER_NAME = ".AutofillThirdPartyModeContentProvider"
        val THIRD_PARTY_MODE_COLUMN = "autofill_third_party_state"
        val THIRD_PARTY_MODE_ACTIONS_URI_PATH = "autofill_third_party_mode"

        val uri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(CHROME_CHANNEL_PACKAGE + CONTENT_PROVIDER_NAME)
            .path(THIRD_PARTY_MODE_ACTIONS_URI_PATH)
            .build()

        val cursor = contentResolver.query(
            uri,
            arrayOf(THIRD_PARTY_MODE_COLUMN), // projection
            null, // selection
            null, // selectionArgs
            null // sortOrder
        )

        if (cursor == null) {
            // Terminate now! Chromium versions older than this don't provide this information.
        }

        cursor?.use { // Use the safe call operator and the use function for auto-closing
            if (it.moveToFirst()) { // Check if the cursor has any rows
                val index = it.getColumnIndex(THIRD_PARTY_MODE_COLUMN)
                if (index != -1) { // Check if the column exists
                    val value = it.getInt(index)
                    if (0 == value) {
                        // 0 means that the third party mode is turned off. Chrome uses its built-in
                        // password manager. This is the default for new users.
                    } else {
                        // 1 means that the third party mode is turned on. Chrome forwards all
                        // autofill requests to Android Autofill. Users have to opt-in for this.
                    }
                } else {
                    // Handle the case where the column doesn't exist.  Log a warning, perhaps.
                    Log.w("Autofill", "Column $THIRD_PARTY_MODE_COLUMN not found in cursor")
                }
            }
        } // The cursor is automatically closed here
        // [END android_identity_autofill_chrome_read_settings]
    }

    private fun openChromeSettings() {
        // [START android_identity_autofill_chrome_settings_deeplink]
        val autofillSettingsIntent = Intent(Intent.ACTION_APPLICATION_PREFERENCES)
        autofillSettingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
        autofillSettingsIntent.addCategory(Intent.CATEGORY_APP_BROWSER)
        autofillSettingsIntent.addCategory(Intent.CATEGORY_PREFERENCE)

        // Invoking the intent with a chooser allows users to select the channel they
        // want to configure. If only one browser reacts to the intent, the chooser is
        // skipped.
        val chooser = Intent.createChooser(autofillSettingsIntent, "Pick Chrome Channel")
        startActivity(chooser)

        // If the caller knows which Chrome channel they want to configure,
        // they can instead add a package hint to the intent, e.g.
        val specificChromeIntent = Intent(Intent.ACTION_APPLICATION_PREFERENCES) // Create a *new* intent
        specificChromeIntent.addCategory(Intent.CATEGORY_DEFAULT)
        specificChromeIntent.addCategory(Intent.CATEGORY_APP_BROWSER)
        specificChromeIntent.addCategory(Intent.CATEGORY_PREFERENCE)
        specificChromeIntent.setPackage("com.android.chrome") // Set the package on the *new* intent
        startActivity(specificChromeIntent) // Start the *new* intent
        // [END android_identity_autofill_chrome_settings_deeplink]
    }
}

private typealias ParsedStructure = BasicAutofillService.ParsedStructure
private typealias UserData = BasicAutofillService.UserData
