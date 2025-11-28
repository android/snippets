# Java Code Samples in google3/third_party/devsite/android/en/google/play/billing

This document lists Java code samples found in the Google Play Billing documentation.

## From `google3/third_party/devsite/android/en/google/play/billing/integrate.md`

### Initialize BillingClient (Java)
// [START android_playbilling_initialize_java]

private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
@Override
public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
// To be implemented in a later section.
}
};

private BillingClient billingClient = BillingClient.newBuilder(context)
.setListener(purchasesUpdatedListener)
// Configure other settings.
.build();

// [END android_playbilling_initialize_java]

### Start Connection (Java)

// [START android_playbilling_startconnection_java]

billingClient.startConnection(new BillingClientStateListener() {
@Override
public void onBillingSetupFinished(BillingResult billingResult) {
if (billingResult.getResponseCode() ==  BillingResponseCode.OK) {
// The BillingClient is ready. You can query purchases here.
}
}
@Override
public void onBillingServiceDisconnected() {
// Try to restart the connection on the next request to
// Google Play by calling the startConnection() method.
}
});

// [END android_playbilling_startconnection_java]

### Enable Automatic Reconnection (Java)
// [START android_playbilling_enableautoreconnect_java]

BillingClient billingClient = BillingClient.newBuilder(context)
.setListener(listener)
.enablePendingPurchases()
.enableAutoServiceReconnection() // Add this line to enable reconnection
.build();

// [END android_playbilling_enableautoreconnect_java]

### Query Product Details (Java)
// [START android_playbilling_queryproductdetails_java]

QueryProductDetailsParams queryProductDetailsParams =
QueryProductDetailsParams.newBuilder()
.setProductList(
ImmutableList.of(
Product.newBuilder()
.setProductId("product_id_example")
.setProductType(ProductType.SUBS)
.build()))
.build();

billingClient.queryProductDetailsAsync(
queryProductDetailsParams,
new ProductDetailsResponseListener() {
public void onProductDetailsResponse(BillingResult billingResult,
QueryProductDetailsResult queryProductDetailsResult) {
if (billingResult.getResponseCode() == BillingResponseCode.OK) {
for (ProductDetails productDetails : queryProductDetailsResult().getProductDetailsList()) {
// Process success retrieved product details here.
}

               for (UnfetchedProduct unfetchedProduct : queryproductDetailsResult.getUnfetchedProductList()) {
                 // Handle any unfetched products as appropriate.
               }
            }
        }
    }
)

// [END android_playbilling_queryproductdetails_java]

### onPurchasesUpdated (Java)
// [START android_playbilling_onpurchasesupdated_java]

@Override
void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
if (billingResult.getResponseCode() == BillingResponseCode.OK
&& purchases != null) {
for (Purchase purchase : purchases) {
// Process the purchase as described in the next section.
}
} else if (billingResult.getResponseCode() == BillingResponseCode.USER_CANCELED) {
// Handle an error caused by a user canceling the purchase flow.
} else {
// Handle any other error codes.
}
}

// [END android_playbilling_onpurchasesupdated_java]

### Consume Product (Java)
// [START android_playbilling_consumeproduct_java]

    ConsumeParams consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

    ConsumeResponseListener listener = new ConsumeResponseListener() {
        @Override
        public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
            if (billingResult.getResponseCode() == BillingResponseCode.OK) {
                // Handle the success of the consume operation.
            }
        }
    };

    billingClient.consumeAsync(consumeParams, listener);

// [END android_playbilling_consumeproduct_java]

### Acknowledge Purchase (Java)
// [START android_playbilling_acknowledge_java]

BillingClient client = ...
AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = ...

AcknowledgePurchaseParams acknowledgePurchaseParams =
AcknowledgePurchaseParams.newBuilder()
.setPurchaseToken(purchase.getPurchaseToken())
.build();
client.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);

// [END android_playbilling_acknowledge_java]

### Handle Purchase Recap (Java)
// [START android_playbilling_handlepurchaserecap_java]

void handlePurchase(Purchase purchase) {
// Purchase retrieved from BillingClient#queryPurchasesAsync or your
// onPurchasesUpdated.
Purchase purchase = ...;

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
    // discussed in the processing purchases section.
}

// [END android_playbilling_handlepurchaserecap_java]

### Get Billing Config Async (Java)
// [START android_playbilling_getbillingconfig_java]

// Use the default GetBillingConfigParams.
GetBillingConfigParams getBillingConfigParams = GetBillingConfigParams.newBuilder().build();
billingClient.getBillingConfigAsync(getBillingConfigParams,
new BillingConfigResponseListener() {
public void onBillingConfigResponse(
BillingResult billingResult, BillingConfig billingConfig) {
if (billingResult.getResponseCode() == BillingResponseCode.OK
&& billingConfig != null) {
String countryCode = billingConfig.getCountryCode();
...
} else {
// TODO: Handle errors
}
}
});

// [END android_playbilling_getbillingconfig_java]

## From `google3/third_party/devsite/android/en/google/play/billing/subscriptions.md`

### Change Subscription Plan using SubscriptionProductReplacementParams (Java)
// [START android_playbilling_subscription_replace_java]

BillingClient billingClient = …;

int replacementModeForBasePlan =…;
int replacementModeForAddon =…;
// ProductDetails obtained from queryProductDetailsAsync().
ProductDetailsParams productDetails1 =
ProductDetailsParams.newBuilder()
.setSubscriptionProductReplacementParams(
SubscriptionProductReplacementParams.newBuilder()
.setOldProductId("old_product_id_1")
.setReplacementMode(replacementModeForBasePlan))
.build();
ProductDetailsParams productDetails2 =
ProductDetailsParams.newBuilder()
.setSubscriptionProductReplacementParams(
SubscriptionProductReplacementParams.newBuilder()
.setOldProductId("old_product_id_2")
.setReplacementMode(replacementModeForAddon))
.build();
ProductDetailsParams productDetails3 = ...;

ArrayList<ProductDetailsParams> newProductDetailsList = new ArrayList<>();
newProductDetailsList.add(productDetails1);
newProductDetailsList.add(productDetails2);
newProductDetailsList.add(productDetails3);

BillingFlowParams billingFlowParams =
BillingFlowParams.newBuilder()
.setSubscriptionUpdateParams(
SubscriptionUpdateParams.newBuilder()
.setOldPurchaseToken(purchaseTokenOfExistingSubscription)
.build())
.setProductDetailsParamsList(productDetailsList)
.build();

billingClient.launchBillingFlow(billingFlowParams);

// [END android_playbilling_subscription_replace_java]

### Change Subscription Plan using SubscriptionUpdateParams (Deprecated) (Java)
// [START android_playbilling_subscription_update_deprecated_java]

String offerToken = productDetails
.getSubscriptionOfferDetails(selectedOfferIndex)
.getOfferToken();

BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
.setProductDetailsParamsList(
ImmuableList.of(
ProductDetailsParams.newBuilder()
// fetched via queryProductDetailsAsync
.setProductDetails(productDetails)
// offerToken can be found in
// ProductDetails=>SubscriptionOfferDetails
.setOfferToken(offerToken)
.build()))
.setSubscriptionUpdateParams(
SubscriptionUpdateParams.newBuilder()
// purchaseToken can be found in Purchase#getPurchaseToken
.setOldPurchaseToken("old_purchase_token")
.setSubscriptionReplacementMode(ReplacementMode.CHARGE_FULL_PRICE)
.build())
.build();

BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
// ...

// [END android_playbilling_subscription_update_deprecated_java]

### Integrate In-App Messaging (Java)
// [START android_playbilling_inappmessaging_java]

InAppMessageParams inAppMessageParams = InAppMessageParams.newBuilder()
.addInAppMessageCategoryToShow(InAppMessageCategoryId.TRANSACTIONAL)
.build();

billingClient.showInAppMessages(activity,
inAppMessageParams,
new InAppMessageResponseListener() {
@Override
public void onInAppMessageResponse(InAppMessageResult inAppMessageResult) {
if (inAppMessageResult.responseCode
== InAppMessageResponseCode.NO_ACTION_NEEDED) {
// The flow has finished and there is no action needed from developers.
} else if (inAppMessageResult.responseCode
== InAppMessageResponseCode.SUBSCRIPTION_STATUS_UPDATED) {
// The subscription status changed. For example, a subscription
// has been recovered from a suspend state. Developers should
// expect the purchase token to be returned with this response
// code and use the purchase token with the Google Play
// Developer API.
}
}
});

// [END android_playbilling_inappmessaging_java]
