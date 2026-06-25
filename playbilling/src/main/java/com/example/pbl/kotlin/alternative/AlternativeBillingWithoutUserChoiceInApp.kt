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

package com.example.pbl.kotlin.alternative

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AlternativeBillingOnlyAvailabilityListener
import com.android.billingclient.api.AlternativeBillingOnlyInformationDialogListener
import com.android.billingclient.api.AlternativeBillingOnlyReportingDetails
import com.android.billingclient.api.AlternativeBillingOnlyReportingDetailsListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingResult

private class AlternativeBillingWithoutUserChoiceInApp(
    private val context: Context,
    private val activity: Activity
) {

    private fun initBillingClient() {
        // [START android_playbilling_alt_billing_only_init]
        var billingClient = BillingClient.newBuilder(context)
            .enableAlternativeBillingOnly()
            .build()
        // [END android_playbilling_alt_billing_only_init]
    }

    private fun checkAvailability(billingClient: BillingClient) {
        // [START android_playbilling_alt_billing_only_check_availability]
        billingClient.isAlternativeBillingOnlyAvailableAsync(object :
            AlternativeBillingOnlyAvailabilityListener {
            override fun onAlternativeBillingOnlyAvailabilityResponse(
                billingResult: BillingResult
            ) {
                if (billingResult.responseCode != BillingResponseCode.OK) {
                    // Handle failures such as retrying due to network errors,
                    // handling alternative billing only being unavailable, etc.
                    return
                }

                // Alternative billing only is available. Continue with steps in
                // the guide.
            }
        })
        // [END android_playbilling_alt_billing_only_check_availability]
    }

    private fun showDialog(billingClient: BillingClient) {
        // [START android_playbilling_alt_billing_only_show_dialog]
        // An activity reference from which the alternative billing only information
        // dialog will be launched.
        val activity: Activity = this.activity

        val listener: AlternativeBillingOnlyInformationDialogListener =
            AlternativeBillingOnlyInformationDialogListener { billingResult ->
                // check billingResult
            }

        val billingResult =
            billingClient.showAlternativeBillingOnlyInformationDialog(
                activity,
                listener
            )
        // [END android_playbilling_alt_billing_only_show_dialog]
    }

    private fun prepareToken(billingClient: BillingClient) {
        // [START android_playbilling_alt_billing_only_prepare_token]
        billingClient.createAlternativeBillingOnlyReportingDetailsAsync(object :
            AlternativeBillingOnlyReportingDetailsListener {
            override fun onAlternativeBillingOnlyTokenResponse(
                billingResult: BillingResult,
                alternativeBillingOnlyReportingDetails: AlternativeBillingOnlyReportingDetails?
            ) {
                if (billingResult.responseCode != BillingResponseCode.OK) {
                    // Handle failures such as retrying due to network errors.
                    return
                }

                val externalTransactionToken =
                    alternativeBillingOnlyReportingDetails?.externalTransactionToken

                // Send transaction token to backend and report to Google Play.
            }
        })
        // [END android_playbilling_alt_billing_only_prepare_token]
    }
}
