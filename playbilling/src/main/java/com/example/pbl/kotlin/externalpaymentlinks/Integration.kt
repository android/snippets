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

package com.example.pbl.kotlin.externalpaymentlinks

import android.content.Context
import androidx.core.net.toUri
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingProgram
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingProgramAvailabilityDetails
import com.android.billingclient.api.BillingProgramAvailabilityListener
import com.android.billingclient.api.BillingProgramReportingDetails
import com.android.billingclient.api.BillingProgramReportingDetailsListener
import com.android.billingclient.api.BillingProgramReportingDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.DeveloperBillingOptionParams
import com.android.billingclient.api.DeveloperProvidedBillingListener
import com.android.billingclient.api.EnableBillingProgramParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.PurchasesUpdatedListener

private class Integration(private val context: Context) {

    private fun initBillingClient() {
        // [START android_playbilling_external_payments_init]
        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                // Handle new Google Play purchase.
            }

        val developerProvidedBillingListener =
            DeveloperProvidedBillingListener { details ->
                // Handle user selection for developer provided billing option.
            }

        val billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .enableBillingProgram(
                EnableBillingProgramParams.newBuilder()
                    .setBillingProgram(BillingProgram.EXTERNAL_PAYMENTS)
                    .setDeveloperProvidedBillingListener(developerProvidedBillingListener)
                    .build()
            )
            .build()
        // [END android_playbilling_external_payments_init]
    }

    private fun checkAvailability(billingClient: BillingClient) {
        // [START android_playbilling_external_payments_check_availability]
        billingClient.isBillingProgramAvailableAsync(
            BillingProgram.EXTERNAL_PAYMENTS,
            object : BillingProgramAvailabilityListener {
                override fun onBillingProgramAvailabilityResponse(
                    billingResult: BillingResult,
                    billingProgramAvailabilityDetails: BillingProgramAvailabilityDetails
                ) {
                    if (billingResult.responseCode != BillingResponseCode.OK) {
                        // Handle failures such as retrying due to network errors,
                        // handling external payments unavailable, etc.
                        return
                    }

                    // External payments are available. Can proceed with generating an
                    // external transaction token.
                }
            }
        )
        // [END android_playbilling_external_payments_check_availability]
    }

    private fun prepareToken(billingClient: BillingClient) {
        // [START android_playbilling_external_payments_prepare_token]
        val params =
            BillingProgramReportingDetailsParams.newBuilder()
                .setBillingProgram(BillingProgram.EXTERNAL_PAYMENTS)
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
                    // Persist the external transaction token locally. Pass it to
                    // the external website using DeveloperBillingOptionParams when
                    // launchBillingFlow is called.
                }
            }
        )
        // [END android_playbilling_external_payments_prepare_token]
    }

    private fun buildParams() {
        // [START android_playbilling_external_payments_build_params]
        val developerBillingOptionParams =
            DeveloperBillingOptionParams.newBuilder()
                .setBillingProgram(BillingProgram.EXTERNAL_PAYMENTS)
                .setLinkUri("https://www.example.com/external/purchase".toUri())
                .setLaunchMode(
                    DeveloperBillingOptionParams.LaunchMode.LAUNCH_IN_EXTERNAL_BROWSER_OR_APP
                )
                .build()
        // [END android_playbilling_external_payments_build_params]
    }
}
