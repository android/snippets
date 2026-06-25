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

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryPurchasesParams
import kotlin.coroutines.resume
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BillingClientWrapper(private val context: Context) : PurchasesUpdatedListener {
    // [START android_playbilling_errors_simple_retry]
    // Initialize the BillingClient.
    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    private val coroutineScope = kotlinx.coroutines.CoroutineScope(
        kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.Main.immediate
    )

    private var connectionJob: kotlinx.coroutines.Job? = null

    // Establish a connection to Google Play.
    fun startBillingConnection() {
        connectionJob?.cancel()
        connectionJob = coroutineScope.launch {
            connectWithRetry()
        }
    }

    // Suspended helper to perform a single connection attempt
    private suspend fun connectBilling(): BillingResult =
        kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (continuation.isActive) {
                        continuation.resume(billingResult)
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.e(TAG, "Google Play Billing Service disconnected")
                    if (continuation.isActive) {
                        continuation.resume(
                            BillingResult.newBuilder()
                                .setResponseCode(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)
                                .setDebugMessage("Service disconnected during connection setup")
                                .build()
                        )
                    } else {
                        startBillingConnection()
                    }
                }
            })
        }

    // Billing connection retry logic. This is a simple max retry pattern
    private suspend fun connectWithRetry() {
        val maxTries = 3
        var tries = 1
        var isConnectionEstablished = false
        while (tries <= maxTries && !isConnectionEstablished) {
            val billingResult = connectBilling()
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                isConnectionEstablished = true
                Log.d(TAG, "Billing response OK")
            } else {
                Log.e(TAG, "Billing connection retry failed: ${billingResult.debugMessage}")
                tries++
                if (tries <= maxTries) {
                    delay(2000L) // Wait 2 seconds before retrying
                }
            }
        }
    }

    fun cleanUp() {
        coroutineScope.cancel()
    }
    // ...
// [END android_playbilling_errors_simple_retry]

    companion object {
        private const val TAG = "BillingClientWrapper"
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        // Process purchases
    }

    // [START android_playbilling_errors_exponential_backoff]
    private suspend fun acknowledge(purchaseToken: String): BillingResult =
        kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params) { billingResult ->
                continuation.resumeWith(Result.success(billingResult))
            }
        }

    private suspend fun queryPurchases(productType: String): Pair<BillingResult, List<Purchase>> =
        kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(productType)
                .build()
            billingClient.queryPurchasesAsync(params) { billingResult, purchaseList ->
                continuation.resumeWith(Result.success(Pair(billingResult, purchaseList)))
            }
        }

    suspend fun acknowledgePurchase(purchaseToken: String) {
        val retryDelayMs = 2000L
        val retryFactor = 2
        val maxTries = 3

        var tries = 1
        var currentDelay = retryDelayMs
        var acknowledgePurchaseResult: BillingResult

        do {
            acknowledgePurchaseResult = acknowledge(purchaseToken)
            val playBillingResponseCode = acknowledgePurchaseResult.responseCode

            when (playBillingResponseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    Log.i(TAG, "Acknowledgement was successful")
                    return
                }

                BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                    Log.d(TAG, "Acknowledgement failed with ITEM_NOT_OWNED")
                    val (billingResult, purchaseList) = queryPurchases(BillingClient.ProductType.SUBS)
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        purchaseList.forEach { purchase ->
                            acknowledge(purchase.purchaseToken)
                        }
                    }
                    return
                }

                in setOf(
                    BillingClient.BillingResponseCode.ERROR,
                    BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
                    BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
                ) -> {
                    Log.d(
                        TAG,
                        "Acknowledgement failed, but can be retried -- " +
                            "Response Code: ${acknowledgePurchaseResult.responseCode} -- " +
                            "Debug Message: ${acknowledgePurchaseResult.debugMessage}"
                    )
                    if (tries < maxTries) {
                        delay(currentDelay)
                        currentDelay *= retryFactor
                        tries++
                    } else {
                        break
                    }
                }

                else -> {
                    Log.e(
                        TAG,
                        "Acknowledgement failed and cannot be retried -- " +
                            "Response Code: ${acknowledgePurchaseResult.responseCode} -- " +
                            "Debug Message: ${acknowledgePurchaseResult.debugMessage}"
                    )
                    throw Exception("Failed to acknowledge the purchase!")
                }
            }
        } while (tries <= maxTries)

        throw Exception("Failed to acknowledge the purchase after $maxTries attempts!")
    }
    // [END android_playbilling_errors_exponential_backoff]

    private fun enableAutoServiceReconnection(listener: PurchasesUpdatedListener) {
        // [START android_playbilling_errors_auto_reconnection]
        val billingClient = BillingClient.newBuilder(context)
            .setListener(listener)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            )
            .enableAutoServiceReconnection() // Enable automatic service reconnection
            .build()
        // [END android_playbilling_errors_auto_reconnection]
    }

    private fun featureSupport(activity: Activity) {
        // [START android_playbilling_errors_feature_support]
        when {
            billingClient.isReady -> {
                val billingResult =
                    billingClient.isFeatureSupported(BillingClient.FeatureType.IN_APP_MESSAGING)
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // use Feature
                }
            }
        }
        // [END android_playbilling_errors_feature_support]
    }
}
