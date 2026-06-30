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

package com.example.pbl.kotlin.externalcontentlinks

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
import com.android.billingclient.api.EnableBillingProgramParams
import com.android.billingclient.api.LaunchExternalLinkParams
import com.android.billingclient.api.LaunchExternalLinkResponseListener

private class Integration(
    private val context: Context,
    private val activity: Activity
) {

    private fun initBillingClient() {
        // [START android_playbilling_external_content_links_init]
        val billingClient = BillingClient.newBuilder(context)
            .enableBillingProgram(
                EnableBillingProgramParams.newBuilder()
                    .setBillingProgram(BillingProgram.EXTERNAL_CONTENT_LINK)
                    .build()
            )
            .build()
        // [END android_playbilling_external_content_links_init]
    }

    private fun checkEligibility(billingClient: BillingClient) {
        // [START android_playbilling_external_content_links_check_eligibility]
        billingClient.isBillingProgramAvailableAsync(
            BillingProgram.EXTERNAL_CONTENT_LINK,
            object : BillingProgramAvailabilityListener {
                override fun onBillingProgramAvailabilityResponse(
                    billingResult: BillingResult,
                    billingProgramAvailabilityDetails: BillingProgramAvailabilityDetails
                ) {
                    if (billingResult.responseCode != BillingResponseCode.OK) {
                        // Handle failures such as retrying due to network errors,
                        // handling external content links unavailable, etc.
                        return
                    }

                    // External content links are available. Prepare an external
                    // transaction token.
                }
            }
        )
        // [END android_playbilling_external_content_links_check_eligibility]
    }

    private fun prepareToken(billingClient: BillingClient) {
        // [START android_playbilling_external_content_links_prepare_token]
        val params =
            BillingProgramReportingDetailsParams.newBuilder()
                .setBillingProgram(BillingProgram.EXTERNAL_CONTENT_LINK)
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
                    // Persist the external transaction token locally. Pass it to the
                    // external website when launchExternalLink is called.
                }
            }
        )
        // [END android_playbilling_external_content_links_prepare_token]
    }

    private fun launch(billingClient: BillingClient) {
        // [START android_playbilling_external_content_links_launch]
        val params =
            LaunchExternalLinkParams.newBuilder()
                .setBillingProgram(BillingProgram.EXTERNAL_CONTENT_LINK)
                .setLinkUri(Uri.parse("https://www.myapprovedsite.com"))
                .setLinkType(LaunchExternalLinkParams.LinkType.LINK_TO_APP_DOWNLOAD)
                .setLaunchMode(
                    LaunchExternalLinkParams.LaunchMode.LAUNCH_IN_EXTERNAL_BROWSER_OR_APP
                )
                .build()

        val listener: LaunchExternalLinkResponseListener =
            object : LaunchExternalLinkResponseListener {
                override fun onLaunchExternalLinkResponse(
                    billingResult: BillingResult
                ) {
                    if (billingResult.responseCode != BillingResponseCode.OK) {
                        // Handle failures such as retrying due to network errors.
                        return
                    }

                    // If Launch Mode was set to LAUNCH_IN_EXTERNAL_BROWSER_OR_APP, the
                    // user was directed outside of the app by Play. This does not give
                    // any information on the user's actions during the link out, such
                    // as if a transaction was completed.

                    // If Launch Mode was set to CALLER_WILL_LAUNCH_LINK, then your app
                    // may proceed to direct the user to the external website.
                }
            }

        billingClient.launchExternalLink(activity, params, listener)
        // [END android_playbilling_external_content_links_launch]
    }
}
