/* Copyright 2022 Google LLC
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
package com.google.play.billing.samples.managedcatalogue.billing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.support.v7.app.AppCompatActivity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClient.ProductType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsParams.Product;
import com.google.common.collect.ImmutableList;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;

/** Unit tests for {@link BillingServiceClient}. */
@RunWith(AndroidJUnit4.class)
public class BillingServiceClientTest {
  @Rule public final MockitoRule mockito = MockitoJUnit.rule();

  @Mock private BillingClient mockPblBillingClient;
  @Mock private BillingServiceClientListener mockListener;
  private AppCompatActivity activity;

  @Captor private ArgumentCaptor<BillingClientStateListener> billingClientStateListenerCaptor;

  private BillingServiceClient billingServiceClient;

  private static final String TEST_PRODUCT_ID = "test_preorder_item";
  private static final String TEST_OFFER_TOKEN = "test_offer_token";
  private static final Product TEST_PRODUCT =
      Product.newBuilder().setProductId(TEST_PRODUCT_ID).setProductType(ProductType.INAPP).build();
  private static final BillingResult BILLING_RESULT_OK =
      BillingResult.newBuilder().setResponseCode(BillingResponseCode.OK).build();
  private static final BillingResult BILLING_RESULT_SETUP_FAILED =
      BillingResult.newBuilder().setResponseCode(BillingResponseCode.DEVELOPER_ERROR).build();

  @Before
  public void setUp() {
    activity = Robolectric.buildActivity(AppCompatActivity.class).setup().get();
    billingServiceClient = new BillingServiceClient(activity, mockListener, mockPblBillingClient);
  }

  private ProductDetails createProductDetails(String productId) {
    try {
      return ProductDetails.fromJson("{\"productId\":\"" + productId + "\",\"type\":\"inapp\"}");
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void startBillingConnection_success_queriesProducts() {
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  BillingClientStateListener listener = invocation.getArgument(0);
                  listener.onBillingSetupFinished(BILLING_RESULT_OK);
                  return null;
                })
        .when(mockPblBillingClient)
        .startConnection(billingClientStateListenerCaptor.capture());

    billingServiceClient.startBillingConnection(ImmutableList.of(TEST_PRODUCT));

    verify(mockPblBillingClient).startConnection(any(BillingClientStateListener.class));
    verify(mockPblBillingClient)
        .queryProductDetailsAsync(
            any(QueryProductDetailsParams.class), any(ProductDetailsResponseListener.class));
  }

  @Test
  public void startBillingConnection_setupFailed_notifiesListener() {
    doAnswer(
            (Answer<Void>)
                invocation -> {
                  BillingClientStateListener listener = invocation.getArgument(0);
                  listener.onBillingSetupFinished(BILLING_RESULT_SETUP_FAILED);
                  return null;
                })
        .when(mockPblBillingClient)
        .startConnection(billingClientStateListenerCaptor.capture());

    billingServiceClient.startBillingConnection(ImmutableList.of(TEST_PRODUCT));

    verify(mockListener).onBillingSetupFailed(BILLING_RESULT_SETUP_FAILED);
    verify(mockPblBillingClient, never()).queryProductDetailsAsync(any(), any());
  }

  @Test
  public void launchPurchase_callsLaunchBillingFlow() {
    ProductDetails productDetails = createProductDetails(TEST_PRODUCT_ID);
    when(mockPblBillingClient.launchBillingFlow(eq(activity), any(BillingFlowParams.class)))
        .thenReturn(BILLING_RESULT_OK);

    billingServiceClient.launchPurchase(activity, productDetails, TEST_OFFER_TOKEN);
    verify(mockPblBillingClient).launchBillingFlow(eq(activity), any(BillingFlowParams.class));
  }
}
