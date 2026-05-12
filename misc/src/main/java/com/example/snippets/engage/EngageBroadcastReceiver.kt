package com.example.snippets.engage

// [START android_engage_broadcast_receiver_implementation]
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.google.android.engage.service.BroadcastReceiverPermissions

// [START_EXCLUDE silent]
@SuppressWarnings("unused")
// [END_EXCLUDE]
class EngageBroadcastReceiver : BroadcastReceiver() {
    // IMPORTANT: Only trigger the specific publish job for the received intent action.
    // DO NOT publish all clusters at once.
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return
        when (intent.action) {
            com.google.android.engage.service.Intents.ACTION_PUBLISH_RECOMMENDATION
                -> EngagePublisher.publishOneTime(context,Constants.PUBLISH_TYPE_RECOMMENDATIONS)
            // Note: If app handles other publish actions (e.g. Featured, Continuation), add them here.
            com.google.android.engage.service.Intents.ACTION_PUBLISH_FEATURED
                -> EngagePublisher.publishOneTime(context, Constants.PUBLISH_TYPE_FEATURED)

            /** Note: If vertical has other intents (e.g. FOOD shopping cart, etc.), add them here.
             * com.google.android.engage.food.service.Intents.ACTION_PUBLISH_FOOD_SHOPPING_CART
             * -> EngagePublisher.publishOneTime(context, Constants.PUBLISH_TYPE_FOOD_SHOPPING_CARD)
             * com.google.android.engage.travel.service.Intents.ACTION_PUBLISH_RESERVATION
             * -> EngagePublisher.publishOneTime(context, Constants.PUBLISH_TYPE_RESERVATION )
            **/
        }
    }

    companion object {
        /**
         * Dynamically registers the receiver.
         * This is required in addition to static registration in AndroidManifest.xml.
         * Call this method in your Application's onCreate() or your main Activity's onCreate().
         */
        fun register(context: Context) {
            val appContext = context.applicationContext
            val receiver = EngageBroadcastReceiver()

            // Register Recommendation Cluster Publish Intent
            ContextCompat.registerReceiver(
                appContext,
                receiver,
                IntentFilter(com.google.android.engage.service.Intents.ACTION_PUBLISH_RECOMMENDATION),
                BroadcastReceiverPermissions.BROADCAST_REQUEST_DATA_PUBLISH_PERMISSION,
                null,
                ContextCompat.RECEIVER_EXPORTED
            )

            // Register Featured Cluster Publish Intent
            ContextCompat.registerReceiver(
                appContext,
                receiver,
                IntentFilter(com.google.android.engage.service.Intents.ACTION_PUBLISH_FEATURED),
                BroadcastReceiverPermissions.BROADCAST_REQUEST_DATA_PUBLISH_PERMISSION,
                null,
                ContextCompat.RECEIVER_EXPORTED
            )

            // Register Continuation Cluster Publish Intent
            ContextCompat.registerReceiver(
                appContext,
                receiver,
                IntentFilter(com.google.android.engage.service.Intents.ACTION_PUBLISH_CONTINUATION),
                BroadcastReceiverPermissions.BROADCAST_REQUEST_DATA_PUBLISH_PERMISSION,
                null,
                ContextCompat.RECEIVER_EXPORTED
            )
            // Note: Add vertical-specific intents here if applicable (e.g., FOOD shopping cart, etc.)
        }
    }
}
// [END android_engage_broadcast_receiver_implementation]