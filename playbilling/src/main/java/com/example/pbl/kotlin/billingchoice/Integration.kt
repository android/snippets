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

package com.example.pbl.kotlin.billingchoice

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingProgramAvailabilityDetails.BillingChoiceAvailabilityDetails.ChoiceScreenType
import com.android.billingclient.api.BillingProgramReportingDetailsParams.DeveloperBillingType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.SubscriptionUpdateParams
import com.android.billingclient.api.BillingProgramReportingDetails
import com.android.billingclient.api.BillingProgramReportingDetailsListener
import com.android.billingclient.api.BillingProgramReportingDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.DeveloperBillingOptionParams
import com.android.billingclient.api.DeveloperProvidedBillingListener
import com.android.billingclient.api.EnableBillingProgramParams
import com.android.billingclient.api.GetBillingChoiceInfoParams
import com.android.billingclient.api.LaunchExternalLinkParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.createBillingProgramReportingDetails
import com.android.billingclient.api.getBillingChoiceInfo
import com.android.billingclient.api.isBillingProgramAvailable
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private class Integration(
    private val context: Context,
    private val activity: Activity,
    private val billingClient: BillingClient,
    private val developerProvidedBillingListener: DeveloperProvidedBillingListener,
    private val purchasesUpdatedListener: PurchasesUpdatedListener
) {

    private fun loadImage(url: String, imageView: ImageView) {}

    // Scenario 1A: Google-rendered, In-app
    suspend fun startConnectionGoogleRenderedInApp() {
        // [START android_playbilling_billingchoice_connect_google_rendered_in_app]
        // Build the parameters to enable the Billing Choice program and assign the listener
        // to handle user selection of the developer-provided billing option.
        val params = EnableBillingProgramParams.newBuilder()
            .setBillingProgram(BillingClient.BillingProgram.BILLING_CHOICE)
            .setDeveloperProvidedBillingListener(developerProvidedBillingListener)
            .build()

        // Build the parameters to enable support for pending purchases.
        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()

        // Construct the BillingClient instance with the purchases updated listener,
        // pending purchases support, and the billing choice params.
        val billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(pendingPurchasesParams)
            .enableBillingProgram(params)
            .build()

        // Establish a connection to Google Play
        val billingResult = suspendCancellableCoroutine<BillingResult> { continuation ->
            billingClient.startConnection(object : BillingClientStateListener {
                // Called when the connection setup process completes.
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    // Resume the coroutine and pass back the BillingResult to the caller.
                    if (continuation.isActive) {
                        continuation.resume(billingResult)
                    }
                }

                // Called if the connection to the Play Store service is dropped.
                // This prevents the await or suspension point from hanging indefinitely.
                override fun onBillingServiceDisconnected() {
                    if (continuation.isActive) {
                        continuation.resume(
                            BillingResult.newBuilder()
                                .setResponseCode(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)
                                .setDebugMessage("Billing service disconnected during connection setup")
                                .build()
                        )
                    }
                }
            })
        }
        // [END android_playbilling_billingchoice_connect_google_rendered_in_app]
    }

    suspend fun verifyAvailabilityGoogleRenderedInApp() {
        // [START android_playbilling_billingchoice_verify_google_rendered_in_app]
        val (billingResult, billingProgramAvailabilityDetails) = billingClient.isBillingProgramAvailable(BillingClient.BillingProgram.BILLING_CHOICE)

        if (billingResult.responseCode == BillingResponseCode.OK) {
            val billingChoiceAvailabilityDetails = billingProgramAvailabilityDetails.billingChoiceAvailabilityDetails
            if (billingChoiceAvailabilityDetails != null &&
                billingChoiceAvailabilityDetails.choiceScreenType == ChoiceScreenType.GOOGLE_RENDERED
            ) {
                // Billing choice is available. Query products and proceed.

            } else {
                // Fallback to other available programs.
            }
        } else {
            // Fallback to other available programs.
        }
        // [END android_playbilling_billingchoice_verify_google_rendered_in_app]
    }

    fun launchBillingFlowGoogleRenderedInApp(
        productDetailsParamsList: List<BillingFlowParams.ProductDetailsParams>
    ) {
        // [START android_playbilling_billingchoice_launch_google_rendered_in_app]
        val developerBillingOptionParams = DeveloperBillingOptionParams.newBuilder()
            .setBillingProgram(BillingClient.BillingProgram.BILLING_CHOICE)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .enableDeveloperBillingOption(developerBillingOptionParams)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        // [END android_playbilling_billingchoice_launch_google_rendered_in_app]
    }

    // Scenario 1B: Developer-rendered, In-app
    suspend fun startConnectionDeveloperRenderedInApp() {
        // [START android_playbilling_billingchoice_connect_developer_rendered_in_app]
        // Build the parameters to enable the Billing Choice program.
        val params = EnableBillingProgramParams.newBuilder()
            .setBillingProgram(BillingClient.BillingProgram.BILLING_CHOICE)
            .build()

        // Build the parameters to enable support for pending purchases.
        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()

        // Construct the BillingClient instance with the purchases updated listener,
        // pending purchases support, and the billing choice params.
        val billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(pendingPurchasesParams)
            .enableBillingProgram(params)
            .build()

        // Establish a connection to Google Play
        val billingResult = suspendCancellableCoroutine<BillingResult> { continuation ->
            billingClient.startConnection(object : BillingClientStateListener {
                // Called when the connection setup process completes.
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    // Resume the coroutine and pass back the BillingResult to the caller.
                    if (continuation.isActive) {
                        continuation.resume(billingResult)
                    }
                }

                // Called if the connection to the Play Store service is dropped.
                // This prevents the await or suspension point from hanging indefinitely.
                override fun onBillingServiceDisconnected() {
                    if (continuation.isActive) {
                        continuation.resume(
                            BillingResult.newBuilder()
                                .setResponseCode(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)
                                .setDebugMessage("Billing service disconnected during connection setup")
                                .build()
                        )
                    }
                }
            })
        }
        // [END android_playbilling_billingchoice_connect_developer_rendered_in_app]
    }

    suspend fun verifyAvailabilityDeveloperRenderedInApp() {
        // [START android_playbilling_billingchoice_verify_developer_rendered_in_app]
        val (billingResult, billingProgramAvailabilityDetails) =
            billingClient.isBillingProgramAvailable(BillingClient.BillingProgram.BILLING_CHOICE)

        if (billingResult.responseCode == BillingResponseCode.OK) {
            val billingChoiceAvailabilityDetails =
                billingProgramAvailabilityDetails.billingChoiceAvailabilityDetails

            if (billingChoiceAvailabilityDetails != null &&
                billingChoiceAvailabilityDetails.choiceScreenType == ChoiceScreenType.DEVELOPER_RENDERED
            ) {
                // Billing choice is available. Query products and proceed.
                // You can inspect details such as:
                // - billingChoiceAvailabilityDetails.choiceScreenType
                // - billingChoiceAvailabilityDetails.isExternalLinkAvailable
            } else {
                // Fallback to other available programs.
            }
        } else {
            // Fallback to other available programs.
        }
        // [END android_playbilling_billingchoice_verify_developer_rendered_in_app]
    }

    suspend fun getBillingChoiceInfoDeveloperRenderedInApp(
        playBillingLoyaltyTextView: TextView,
        playBillingImageView: ImageView
    ) {
        // [START android_playbilling_billingchoice_info_developer_rendered_in_app]
        // 1. Create the params required for the request
        val params = GetBillingChoiceInfoParams.newBuilder()
            .setBillingProgram(BillingClient.BillingProgram.BILLING_CHOICE)
            .setPlayBillingChoiceImageLayout(GetBillingChoiceInfoParams.ImageLayout.RECTANGULAR_FOUR_BY_ONE)
            .build()

        // 2. Call the suspend method on your billingClient instance
        val (billingResult, playBillingChoiceInfo) = billingClient.getBillingChoiceInfo(params)

        if (billingResult.responseCode == BillingResponseCode.OK && playBillingChoiceInfo != null) {
            // Access the URL of the image associated with the Play Billing Choice
            val imageUrl = playBillingChoiceInfo.playBillingChoiceImageUrl

            // Access the Play Loyalty string information, if available
            val loyaltyInfo = playBillingChoiceInfo.playBillingLoyaltyInfo

            // Populate your developer-rendered UI elements
            playBillingLoyaltyTextView.text = loyaltyInfo
            loadImage(imageUrl, playBillingImageView)
        } else {
            // Handle error scenarios
        }
        // [END android_playbilling_billingchoice_info_developer_rendered_in_app]
    }

    suspend fun createTokenDeveloperRenderedInApp() {
        // [START android_playbilling_billingchoice_create_token_developer_rendered_in_app]
        // Build the parameters specifying the billing program and that the billing type is IN_APP.
        val params = BillingProgramReportingDetailsParams.newBuilder()
            .setBillingProgram(BillingClient.BillingProgram.BILLING_CHOICE)
            .setDeveloperBillingType(DeveloperBillingType.IN_APP)
            .build()

        // Call the suspending extension function to request the reporting details
        val (billingResult, billingProgramReportingDetails) =
            billingClient.createBillingProgramReportingDetails(params)
            
        if (billingResult.responseCode != BillingResponseCode.OK) {
            // Handle failures such as retrying due to network errors.
            return
        }

        // Extract the transaction token from the returned reporting details
        val transactionToken = billingProgramReportingDetails?.externalTransactionToken

        // Persist the external transaction token locally. Pass it to
        // DeveloperBillingOptionParams when launchBillingFlow is called.
        // It can also be used as part of your external website
        // [END android_playbilling_billingchoice_create_token_developer_rendered_in_app]
    }

    // Scenario 2A: Google-rendered, External Link
    suspend fun verifyAvailabilityGoogleRenderedExternalLink() {
        // [START android_playbilling_billingchoice_verify_google_rendered_external_link]
        // Check the availability of the billing choice program asynchronously using coroutines
        val (billingResult, billingProgramAvailabilityDetails) =
            billingClient.isBillingProgramAvailable(BillingClient.BillingProgram.BILLING_CHOICE)

        // Ensure the response code is OK
        if (billingResult.responseCode == BillingResponseCode.OK) {
            // Retrieve the availability details specific to the billing choice program
            val billingChoiceAvailabilityDetails =
                billingProgramAvailabilityDetails.billingChoiceAvailabilityDetails

            // Check if billing choice is available, renders via Google Play, and external link is supported
            if (billingChoiceAvailabilityDetails != null &&
                billingChoiceAvailabilityDetails.choiceScreenType == ChoiceScreenType.GOOGLE_RENDERED &&
                billingChoiceAvailabilityDetails.isExternalLinkAvailable
            ) {
                // Billing choice is available and external transaction links are supported. Query products and proceed.
            } else {
                // Fallback to other available programs.
            }
        } else {
            // Fallback to other available programs.
        }
        // [END android_playbilling_billingchoice_verify_google_rendered_external_link]
    }

    suspend fun createTokenGoogleRenderedExternalLink() {
        // [START android_playbilling_billingchoice_create_token_google_rendered_external_link]
        // Build the parameters for creating reporting details
        val params =
            BillingProgramReportingDetailsParams.newBuilder()
                .setBillingProgram(BillingClient.BillingProgram.BILLING_CHOICE)
                .setDeveloperBillingType(DeveloperBillingType.EXTERNAL_LINK)
                .build()

        // Call the suspend function to create billing program reporting details
        val (billingResult, billingProgramReportingDetails) =
            billingClient.createBillingProgramReportingDetails(params)

        // Handle response failure cases
        if (billingResult.responseCode != BillingResponseCode.OK) {
            // Handle failures such as retrying due to network errors.
            return
        }

        // Retrieve the external transaction token
        val transactionToken =
            billingProgramReportingDetails?.externalTransactionToken

        // Persist the external transaction token locally. Pass it to
        // DeveloperBillingOptionParams when launchBillingFlow is called.
        // It can also be used as part of your external website
        // [END android_playbilling_billingchoice_create_token_google_rendered_external_link]
    }

    fun launchBillingFlowGoogleRenderedExternalLink(transactionToken: String) {
        // [START android_playbilling_billingchoice_launch_google_rendered_external_link]
        // Build the developer billing option parameters with the external link URI,
        // the transaction token, and browser/app launch mode.
        val developerBillingOptionParams =
            DeveloperBillingOptionParams.newBuilder()
                .setBillingProgram(BillingClient.BillingProgram.BILLING_CHOICE)
                .setLinkUri(Uri.parse("https://www.example.com/external/purchase"))
                .setExternalTransactionToken(transactionToken)
                .setLaunchMode(
                    DeveloperBillingOptionParams.LaunchMode.LAUNCH_IN_EXTERNAL_BROWSER_OR_APP
                )
                .build()
        // [END android_playbilling_billingchoice_launch_google_rendered_external_link]
    }

    // Scenario 2B: Developer-rendered, External Link
    suspend fun verifyAvailabilityDeveloperRenderedExternalLink() {
        // [START android_playbilling_billingchoice_verify_developer_rendered_external_link]
        // Check the availability of the billing choice program asynchronously using a coroutine
        val (billingResult, billingProgramAvailabilityDetails) =
            billingClient.isBillingProgramAvailable(BillingClient.BillingProgram.BILLING_CHOICE)

        // Ensure the response code is OK
        if (billingResult.responseCode == BillingResponseCode.OK) {
            // Retrieve the billing choice availability details
            val billingChoiceAvailabilityDetails =
                billingProgramAvailabilityDetails.billingChoiceAvailabilityDetails

            // Check if billing choice details are available, choice screen is developer-rendered,
            // and external transaction links are supported.
            if (billingChoiceAvailabilityDetails != null &&
                billingChoiceAvailabilityDetails.choiceScreenType == ChoiceScreenType.DEVELOPER_RENDERED &&
                billingChoiceAvailabilityDetails.isExternalLinkAvailable
            ) {
                // Billing choice is available and external transaction links are supported.
                // Query products and proceed.
            } else {
                // Fallback to other available programs.
            }
        } else {
            // Fallback to other available programs.
        }
        // [END android_playbilling_billingchoice_verify_developer_rendered_external_link]
    }

    fun launchExternalLinkDeveloperRenderedExternalLink(yourLinkUri: Uri) {
        // [START android_playbilling_billingchoice_launch_external_developer_rendered_external_link]
        // An activity reference from which the purchase flow will be launched.
        val activity: Activity = activity

        val params = LaunchExternalLinkParams.newBuilder()
            .setBillingProgram(BillingClient.BillingProgram.BILLING_CHOICE)
            // You can pass along the external transaction token from
            // BillingProgramReportingDetails as a URL parameter in the URI
            .setLinkUri(yourLinkUri)
            .setLinkType(LaunchExternalLinkParams.LinkType.LINK_TO_DIGITAL_CONTENT_OFFER)
            .setLaunchMode(
                LaunchExternalLinkParams.LaunchMode.LAUNCH_IN_EXTERNAL_BROWSER_OR_APP
            )
            .build()

        // Call launchExternalLink with a callback
        billingClient.launchExternalLink(activity, params) { billingResult ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                // Proceed with the rest of the purchase flow. If the user
                // purchases an item, be sure to report the transaction to Google
                // Play.
            } else {
                // Handle failures such as retrying due to network errors.
            }
        }
        // [END android_playbilling_billingchoice_launch_external_developer_rendered_external_link]
    }

    // Subscription Replacement
    fun subsReplacementGoogleRenderedInApp(
        externalTransactionId: String,
        productDetailsNewPlan: ProductDetails,
        offerTokenNewPlan: String
    ) {
        // [START android_playbilling_billingchoice_subs_replace_google_rendered_in_app]
        // The external transaction ID from the current
        // alternative billing subscription.
        val externalTransactionId = externalTransactionId

        val developerBillingOptionParams = DeveloperBillingOptionParams.newBuilder()
            .setBillingProgram(BillingClient.BillingProgram.BILLING_CHOICE)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        // Fetched using queryProductDetailsAsync.
                        .setProductDetails(productDetailsNewPlan)
                        // offerIdToken can be found in
                        // ProductDetails=>SubscriptionOfferDetails.
                        .setOfferToken(offerTokenNewPlan)
                        .build()
                )
            )
            .setSubscriptionUpdateParams(
                BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                    .setOriginalExternalTransactionId(externalTransactionId)
                    .build()
            )
            .enableDeveloperBillingOption(developerBillingOptionParams)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)

        // When the user selects the alternative billing flow,
        // the DeveloperProvidedBillingListener is triggered.
        // [END android_playbilling_billingchoice_subs_replace_google_rendered_in_app]
    }

    fun subsReplacementGoogleRenderedExternalLinkSample1(
        externalTransactionId: String,
        productDetailsNewPlan: ProductDetails,
        offerTokenNewPlan: String
    ) {
        // [START android_playbilling_billingchoice_subs_replace_google_rendered_external_link_sample1]
        val externalTransactionId = externalTransactionId

        // 1. Construct DeveloperBillingOptionParams indicating the billing program
        val developerBillingOptionParams = DeveloperBillingOptionParams.newBuilder()
            .setBillingProgram(BillingClient.BillingProgram.BILLING_CHOICE)
            .build()

        // 2. Build BillingFlowParams combining DeveloperBillingOptionParams and SubscriptionUpdateParams
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        // Fetched using queryProductDetailsAsync.
                        .setProductDetails(productDetailsNewPlan)
                        // offerIdToken can be found in ProductDetails=>SubscriptionOfferDetails.
                        .setOfferToken(offerTokenNewPlan)
                        .build()
                )
            )
            .setSubscriptionUpdateParams(
                SubscriptionUpdateParams.newBuilder()
                    .setOriginalExternalTransactionId(externalTransactionId)
                    .build()
            )
            .enableDeveloperBillingOption(developerBillingOptionParams)
            .build()
        // [END android_playbilling_billingchoice_subs_replace_google_rendered_external_link_sample1]
    }

    fun subsReplacementGoogleRenderedExternalLinkSample2() {
        // [START android_playbilling_billingchoice_subs_replace_google_rendered_external_link_sample2]
        val params =
            BillingProgramReportingDetailsParams.newBuilder()
                .setBillingProgram(BillingClient.BillingProgram.BILLING_CHOICE)
                .setDeveloperBillingType(DeveloperBillingType.EXTERNAL_LINK)
                .build()

        billingClient.createBillingProgramReportingDetailsAsync(params) { billingResult, billingProgramReportingDetails ->
            if (billingResult.responseCode != BillingResponseCode.OK) {
                // Handle failures such as retrying due to network errors.
                return@createBillingProgramReportingDetailsAsync
            }
            val externalTransactionToken =
                billingProgramReportingDetails?.externalTransactionToken
            // Persist the external transaction token locally. Pass it to
            // the external website using DeveloperBillingOptionParams when
            // launchBillingFlow is called.
        }
        // [END android_playbilling_billingchoice_subs_replace_google_rendered_external_link_sample2]
    }
}
