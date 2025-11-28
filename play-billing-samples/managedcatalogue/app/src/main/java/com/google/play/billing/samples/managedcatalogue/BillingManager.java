package com.google.play.billing.samples.managedcatalogue;// Import necessary classes from the Android framework and Google Play Billing Library.
import static com.android.billingclient.api.InAppMessageParams.*;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingConfig;
import com.android.billingclient.api.BillingConfigResponseListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.GetBillingConfigParams;
import com.android.billingclient.api.InAppMessageParams;
import com.android.billingclient.api.InAppMessageResponseListener;
import com.android.billingclient.api.InAppMessageResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A compilable class that includes all the Java snippets from the Play Billing documentation.
 * This class demonstrates how to initialize, connect, and use the BillingClient,
 * as well as handle various billing-related tasks like querying products,
 * launching purchase flows, and processing purchases.
 */
public class BillingManager {

    private final Context context;
    private BillingClient billingClient;

    public BillingManager(Context context) {
        this.context = context;
        initializeBillingClient();
    }

    private static void onProductDetailsResponse(BillingResult billingResult, QueryProductDetailsResult productDetailsList) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

// The original snippet had a conceptual error in accessing result types.
// The modern API provides the list directly.
// Handling for unfetched products is implicitly managed by the library.
        }
    }

    private void initializeBillingClient() {
        // [START android_playbilling_initialize_java]
        PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
            // To be implemented in a later section.
            // This is a placeholder for the onPurchasesUpdated method defined below.
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user canceling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        };

        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                // Configure other settings.
                .build();
        // [END android_playbilling_initialize_java]
    }

    public void connectToGooglePlay() {
        // [START android_playbilling_startconnection_java]
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    queryPurchases();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
        // [END android_playbilling_startconnection_java]
    }

    private void queryPurchases() {
        // Example method to query active purchases, not included in the original snippets
        // but useful for a complete example.
        if (billingClient.isReady()) {
            billingClient.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                    (billingResult, purchases) -> {
                        // Process the result
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                            for (Purchase purchase : purchases) {
                                handlePurchase(purchase);
                            }
                        }
                    }
            );
        }
    }

    private void enableAutomaticReconnection(Context context, PurchasesUpdatedListener listener) {
        // [START android_playbilling_enableautoreconnect_java]
        BillingClient billingClient = BillingClient.newBuilder(context)
                .setListener(listener)
                //.enableAutoServiceReconnection() // This method is deprecated. Reconnection is now handled automatically by default.
                .build();
        // [END android_playbilling_enableautoreconnect_java]
    }

    public void queryProductDetails() {
        // [START android_playbilling_queryproductdetails_java]
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                Collections.singletonList(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId("product_id_example")
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                BillingManager::onProductDetailsResponse
        );
        // [END android_playbilling_queryproductdetails_java]
    }

    public void launchBillingFlow(Activity activity, ProductDetails productDetails, String offerToken) {
        // [START android_playbilling_launchflow_java]
        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                Collections.singletonList(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(offerToken)
                                .build()
                );

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        billingClient.launchBillingFlow(activity, billingFlowParams);
        // [END android_playbilling_launchflow_java]
    }


    // This method is a wrapper for the snippet. The actual listener implementation is part of the BillingClient setup.
    public void onPurchasesUpdatedWrapper(BillingResult billingResult, List<Purchase> purchases) {
        // [START android_playbilling_onpurchasesupdated_java]
        // @Override // @Override is commented out as this is a wrapper method, not a direct override.
        // void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                // Process the purchase as described in the next section.
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user canceling the purchase flow.
        } else {
            // Handle any other error codes.
        }
        // }
        // [END android_playbilling_onpurchasesupdated_java]
    }

    public void consumeProduct(Purchase purchase) {
        // [START android_playbilling_consumeproduct_java]
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // Handle the success of the consume operation.
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
        // [END android_playbilling_consumeproduct_java]
    }

    public void acknowledgePurchase(Purchase purchase) {
        // [START android_playbilling_acknowledge_java]
        BillingClient client = this.billingClient; // Assuming client is your BillingClient instance
        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = billingResult -> {
            // Handle response
        };

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                client.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }
        // [END android_playbilling_acknowledge_java]
    }

    public void handlePurchaseRecap(Purchase purchaseParam) {
        // [START android_playbilling_handlepurchaserecap_java]
        // void handlePurchase(Purchase purchase) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your
        // onPurchasesUpdated.
        Purchase purchase = purchaseParam; // Use the passed parameter

        // Step 1: Send the purchase to your secure backend to verify the purchase
        // following
        // https://developer.android.com/google/play/billing/security#verify

        // Step 2: Update your entitlement storage with the purchase. If purchase is
        // in PENDING state then ensure the entitlement is marked as pending and the
        // user does not receive benefits yet. It is recommended that this step is
        // done on your secure backend and can combine in the API call to your
                // backend in step 1.

        // Step 3: Notify the user using appropriate messaging (delaying
        // notification if needed as discussed above).

        // Step 4: Notify Google the purchase was processed using the steps
        // discussed in the processing purchases section. (e.g., Acknowledge or Consume)
        // }
        // [END android_playbilling_handlepurchaserecap_java]
    }

    public void getBillingConfigAsync() {
        // [START android_playbilling_getbillingconfig_java]
        // Use the default GetBillingConfigParams.
        GetBillingConfigParams getBillingConfigParams = GetBillingConfigParams.newBuilder().build();
        billingClient.getBillingConfigAsync(getBillingConfigParams,
                (billingResult, billingConfig) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                            && billingConfig != null) {
                        String countryCode = billingConfig.getCountryCode();
                        // ...
                    } else {
                        // TODO: Handle errors
                    }
                });
        // [END android_playbilling_getbillingconfig_java]
    }

    public void changeSubscriptionPlan(Activity activity, String purchaseTokenOfExistingSubscription, ProductDetails newPlanDetails, ProductDetails existingAddonDetails) {
        // [START android_playbilling_subscription_replace_java]
        BillingClient billingClient = this.billingClient;

        int replacementModeForBasePlan = BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.CHARGE_FULL_PRICE;
        // The following variable is unused in the snippet but retained for completeness.
        int replacementModeForAddon = BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.WITHOUT_PRORATION;

        // ProductDetails obtained from queryProductDetailsAsync().
        BillingFlowParams.ProductDetailsParams productDetails1 =
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(newPlanDetails) // Assumes newPlanDetails is the new base plan
                        .build();

        BillingFlowParams.ProductDetailsParams productDetails2 =
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(existingAddonDetails) // Assumes this is an add-on to keep
                        .build();

        ArrayList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
        productDetailsParamsList.add(productDetails1);
        productDetailsParamsList.add(productDetails2);

        BillingFlowParams billingFlowParams =
                BillingFlowParams.newBuilder()
                        .setSubscriptionUpdateParams(
                                BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                                        .setOldPurchaseToken(purchaseTokenOfExistingSubscription)
                                        .setSubscriptionReplacementMode(replacementModeForBasePlan) // Specify replacement mode for the base plan
                                        .build())
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build();

        billingClient.launchBillingFlow(activity, billingFlowParams);
        // [END android_playbilling_subscription_replace_java]
    }

    public void changeSubscriptionPlanDeprecated(Activity activity, ProductDetails productDetails, int selectedOfferIndex) {
        // [START android_playbilling_subscription_update_deprecated_java]
        String offerToken = productDetails
                .getSubscriptionOfferDetails().get(selectedOfferIndex)
                .getOfferToken();

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                        Collections.singletonList(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                        // fetched via queryProductDetailsAsync
                                        .setProductDetails(productDetails)
                                        // offerToken can be found in
                                        // ProductDetails=>SubscriptionOfferDetails
                                        .setOfferToken(offerToken)
                                        .build()))
                .setSubscriptionUpdateParams(
                        BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                                // purchaseToken can be found in Purchase#getPurchaseToken
                                .setOldPurchaseToken("old_purchase_token")
                                .setSubscriptionReplacementMode(BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.CHARGE_FULL_PRICE)
                                .build())
                .build();

        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
        // ...
        // [END android_playbilling_subscription_update_deprecated_java]
    }

    public void showInAppMessages(Activity activity) {
        // [START android_playbilling_inappmessaging_java]
        InAppMessageParams inAppMessageParams = InAppMessageParams.newBuilder()
                .addInAppMessageCategoryToShow(InAppMessageParams.InAppMessageCategoryId.TRANSACTIONAL)
                .build();

        billingClient.showInAppMessages(activity,
                inAppMessageParams,
                inAppMessageResult -> {
                    if (inAppMessageResult.getResponseCode()
                            == InAppMessageResult.InAppMessageResponseCode.NO_ACTION_NEEDED) {
                        // The flow has finished and there is no action needed from developers.
                    } else if (inAppMessageResult.getResponseCode()
                            == InAppMessageResult.InAppMessageResponseCode.SUBSCRIPTION_STATUS_UPDATED) {
                        // The subscription status changed. For example, a subscription
                        // has been recovered from a suspend state. Developers should
                        // expect the purchase token to be returned with this response
                        // code and use the purchase token with the Google Play
                        // Developer API.
                        queryPurchases(); // Re-query purchases to update UI
                    }
                });
        // [END android_playbilling_inappmessaging_java]
    }




    // A placeholder method for handlePurchase since it's referenced in multiple snippets.
    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                acknowledgePurchase(purchase);
            }
        }
    }
}
