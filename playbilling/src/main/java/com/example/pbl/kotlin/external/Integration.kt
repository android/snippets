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

package com.example.pbl.kotlin.external

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingProgram
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingProgramAvailabilityDetails
import com.android.billingclient.api.BillingProgramAvailabilityListener
import com.android.billingclient.api.BillingProgramReportingDetails
import com.android.billingclient.api.BillingProgramReportingDetailsListener
import com.android.billingclient.api.BillingProgramReportingDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.LaunchExternalLinkParams
import com.android.billingclient.api.LaunchExternalLinkResponseListener
import com.android.billingclient.api.createBillingProgramReportingDetails
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext

private class Integration(
    private val context: Context,
    private val activity: Activity,
    private val yourLinkUri: Uri,
    private val coroutineContext: CoroutineContext
) {

    private fun initBillingClient() {
        // [START android_playbilling_external_offers_init]
        val billingClient = BillingClient.newBuilder(context)
            .enableBillingProgram(BillingProgram.EXTERNAL_OFFER)
            .build()
        // [END android_playbilling_external_offers_init]
    }

    private fun checkAvailability(billingClient: BillingClient) {
        // [START android_playbilling_external_offers_check_availability]
        billingClient.isBillingProgramAvailableAsync(
            BillingProgram.EXTERNAL_OFFER,
            object : BillingProgramAvailabilityListener {
                override fun onBillingProgramAvailabilityResponse(
                    billingResult: BillingResult,
                    billingProgramAvailabilityDetails: BillingProgramAvailabilityDetails
                ) {
                    if (billingResult.responseCode != BillingResponseCode.OK) {
                        // Handle failures such as retrying due to network errors,
                        // handling external offers unavailable, etc.
                        return
                    }

                    // External offers are available. Continue with steps in the
                    // guide.
                }
            }
        )
        // [END android_playbilling_external_offers_check_availability]
    }

    private fun prepareToken(billingClient: BillingClient) {
        // [START android_playbilling_external_offers_prepare_token]
        val params =
            BillingProgramReportingDetailsParams.newBuilder()
                .setBillingProgram(BillingProgram.EXTERNAL_OFFER)
                .build()

        billingClient.createBillingProgramReportingDetailsAsync(
            params,
            object : BillingProgramReportingDetailsListener {
                override fun onCreateBillingProgramReportingDetailsResponse(
                    billingResult: BillingResult,
                    billingProgramReportingDetails: BillingProgramReportingDetails?
                ) {
                    if (billingResult.responseCode != BillingResponseCode.OK) {
                        // Handle failures such as retrying due to network errors.
                        return
                    }
                    val externalTransactionToken =
                        billingProgramReportingDetails?.externalTransactionToken
                    // Persist the transaction token in your backend. You may pass it
                    // to the external website when calling the launchExternalLink API.
                }
            }
        )
        // [END android_playbilling_external_offers_prepare_token]
    }

    private suspend fun prepareTokenSuspend(billingClient: BillingClient, params: BillingProgramReportingDetailsParams) {
        // [START android_playbilling_external_offers_prepare_token_suspend]
        val createBillingProgramReportingDetailsResult =
            withContext(coroutineContext) {
                billingClient
                    .createBillingProgramReportingDetails(params)
            }
        // Process the result
        // [END android_playbilling_external_offers_prepare_token_suspend]
    }

    private fun launchFlow(billingClient: BillingClient) {
        // [START android_playbilling_external_offers_launch_flow]
        // An activity reference from which the external offers flow will be launched.
        val activity = this.activity

        val params =
            LaunchExternalLinkParams.newBuilder()
                .setBillingProgram(BillingProgram.EXTERNAL_OFFER)
                // You can pass along the external transaction token from
                // BillingProgramReportingDetails as a URL parameter in the URI
                .setLinkUri(yourLinkUri)
                .setLinkType(LaunchExternalLinkParams.LinkType.LINK_TO_APP_DOWNLOAD)
                .setLaunchMode(
                    LaunchExternalLinkParams.LaunchMode.LAUNCH_IN_EXTERNAL_BROWSER_OR_APP
                )
                .build()

        val listener: LaunchExternalLinkResponseListener =
            LaunchExternalLinkResponseListener { billingResult ->
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    // Proceed with the rest of the external offer flow. If the user
                    // purchases an item, be sure to report the transaction to Google Play.
                } else {
                    // Handle failures such as retrying due to network errors.
                }
            }

        billingClient.launchExternalLink(activity, params, listener)
        // [END android_playbilling_external_offers_launch_flow]
    }
}
