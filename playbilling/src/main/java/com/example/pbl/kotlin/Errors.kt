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
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.PendingPurchasesParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

// [START android_playbilling_errors_simple_retry]
class BillingClientWrapper(private val context: Context) : PurchasesUpdatedListener {
  // Initialize the BillingClient.
  private val billingClient = BillingClient.newBuilder(context)
    .setListener(this)
    .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
    .build()

  // Establish a connection to Google Play.
  fun startBillingConnection() {
    billingClient.startConnection(object : BillingClientStateListener {
      override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
          Log.d(TAG, "Billing response OK")
          // The BillingClient is ready. You can now query Products Purchases.
        } else {
          Log.e(TAG, billingResult.debugMessage)
          retryBillingServiceConnection()
        }
      }

      override fun onBillingServiceDisconnected() {
        Log.e(TAG, "GBPL Service disconnected")
        retryBillingServiceConnection()
      }
    })
  }

  // Billing connection retry logic. This is a simple max retry pattern
  private fun retryBillingServiceConnection() {
    val maxTries = 3
    var tries = 1
    var isConnectionEstablished = false
    do {
      try {
        billingClient.startConnection(object : BillingClientStateListener {
          override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
              isConnectionEstablished = true
              Log.d(TAG, "Billing connection retry succeeded.")
            } else {
              Log.e(
                TAG,
                "Billing connection retry failed: ${billingResult.debugMessage}"
              )
            }
          }

          override fun onBillingServiceDisconnected() {
            // Retry logic or logging
          }
        })
      } catch (e: Exception) {
        e.message?.let { Log.e(TAG, it) }
      } finally {
        tries++
      }
    } while (tries <= maxTries && !isConnectionEstablished)
  }
  // ...
// [END android_playbilling_errors_simple_retry]

  companion object {
      private const val TAG = "BillingClientWrapper"
  }

  override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
      // Process purchases
  }

  // [START android_playbilling_errors_exponential_backoff]
  private fun acknowledge(purchaseToken: String): BillingResult {
    val params = AcknowledgePurchaseParams.newBuilder()
      .setPurchaseToken(purchaseToken)
      .build()
    var ackResult = BillingResult()
    billingClient.acknowledgePurchase(params) { billingResult ->
      ackResult = billingResult
    }
    return ackResult
  }

  suspend fun acknowledgePurchase(purchaseToken: String) {

    val retryDelayMs = 2000L
    val retryFactor = 2
    val maxTries = 3

    withContext(Dispatchers.IO) {
      acknowledge(purchaseToken)
    }

    AcknowledgePurchaseResponseListener { acknowledgePurchaseResult ->
      val playBillingResponseCode = acknowledgePurchaseResult.responseCode
      when (playBillingResponseCode) {
        BillingClient.BillingResponseCode.OK -> {
          Log.i(TAG, "Acknowledgement was successful")
        }
        BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
          // This is possibly related to a stale Play cache.
          // Querying purchases again.
          Log.d(TAG, "Acknowledgement failed with ITEM_NOT_OWNED")
          billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
              .setProductType(BillingClient.ProductType.SUBS)
              .build()
          )
          { billingResult, purchaseList ->
            when (billingResult.responseCode) {
              BillingClient.BillingResponseCode.OK -> {
                purchaseList.forEach { purchase ->
                  acknowledge(purchase.purchaseToken)
                }
              }
            }
          }
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
          runBlocking {
            exponentialRetry(
              maxTries = maxTries,
              initialDelay = retryDelayMs,
              retryFactor = retryFactor
            ) { acknowledge(purchaseToken) }
          }
        }
        in setOf(
           BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
           BillingClient.BillingResponseCode.DEVELOPER_ERROR,
           BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
         ) -> {
          Log.e(
            TAG,
            "Acknowledgement failed and cannot be retried -- " +
            "Response Code: ${acknowledgePurchaseResult.responseCode} -- " +
            "Debug Message: ${acknowledgePurchaseResult.debugMessage}"
          )
          throw Exception("Failed to acknowledge the purchase!")
        }
      }
    }
  }

  private suspend fun <T> exponentialRetry(
    maxTries: Int = Int.MAX_VALUE,
    initialDelay: Long = Long.MAX_VALUE,
    retryFactor: Int = Int.MAX_VALUE,
    block: suspend () -> T
  ): T? {
    var currentDelay = initialDelay
    var retryAttempt = 1
    do {
      runCatching {
        delay(currentDelay)
        block()
      }
        .onSuccess {
          Log.d(TAG, "Retry succeeded")
          return@onSuccess
        }
        .onFailure { throwable ->
          Log.e(
            TAG,
            "Retry Failed -- Cause: ${throwable.cause} -- Message: ${throwable.message}"
          )
        }
      currentDelay *= retryFactor
      retryAttempt++
    } while (retryAttempt < maxTries)

    return block() // last attempt
  }
  // [END android_playbilling_errors_exponential_backoff]

  private fun dummy(listener: PurchasesUpdatedListener) {
      // [START android_playbilling_errors_auto_reconnection]
      val billingClient = BillingClient.newBuilder(context)
          .setListener(listener)
          .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
          .enableAutoServiceReconnection() // Enable automatic service reconnection
          .build()
      // [END android_playbilling_errors_auto_reconnection]
  }

  private fun featureSupport(activity: Activity) {
      // [START android_playbilling_errors_feature_support]
    when {
      billingClient.isReady -> {
        val billingResult =
          billingClient.isFeatureSupported(BillingClient.FeatureType.IN_APP_MESSAGING);
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
          // use Feature
        }
      }
    }
      // [END android_playbilling_errors_feature_support]
  }
}
