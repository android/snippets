package com.example.snippets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.core.content.ContextCompat;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

// Warning for reader: This file has the Java code snippets for completeness of the corresponding
// documentation page, but these snippets are not part of the actual sample. Refer to the Kotlin
// code for the actual sample.
public class BroadcastReceiverJavaSnippets {

    // [START android_broadcast_receiver_2_class_java]
    public static class MyBroadcastReceiver extends BroadcastReceiver {

        @Inject
        DataRepository dataRepository;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "com.example.snippets.ACTION_UPDATE_DATA")) {
                String data = intent.getStringExtra("com.example.snippets.DATA");
                // Do something with the data, for example send it to a data repository:
                if (data != null) { dataRepository.updateData(data); }
            }
        }
    }
    // [END android_broadcast_receiver_2_class_java]

    /** @noinspection ConstantValue, unused */
    public static class BroadcastReceiverViewModel {
        Context context;

        public BroadcastReceiverViewModel(@ApplicationContext Context context) {
            this.context = context;
        }

        // [START android_broadcast_receiver_3_definition_java]
        MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
        // [END android_broadcast_receiver_3_definition_java]

        public void registerBroadcastReceiver() {
            // [START android_broadcast_receiver_4_intent_filter_java]
            IntentFilter filter = new IntentFilter("com.example.snippets.ACTION_UPDATE_DATA");
            // [END android_broadcast_receiver_4_intent_filter_java]
            // [START android_broadcast_receiver_5_exported_java]
            boolean listenToBroadcastsFromOtherApps = false;
            int receiverFlags = listenToBroadcastsFromOtherApps
                    ? ContextCompat.RECEIVER_EXPORTED
                    : ContextCompat.RECEIVER_NOT_EXPORTED;
            // [END android_broadcast_receiver_5_exported_java]

            // [START android_broadcast_receiver_6_register_java]
            ContextCompat.registerReceiver(context, myBroadcastReceiver, filter, receiverFlags);
            // [END android_broadcast_receiver_6_register_java]

            // [START android_broadcast_receiver_12_register_with_permission_java]
            ContextCompat.registerReceiver(
                    context, myBroadcastReceiver, filter,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    null, // scheduler that defines thread, null means run on main thread
                    receiverFlags
            );
            // [END android_broadcast_receiver_12_register_with_permission_java]
        }

        public void unregisterBroadcastReceiver() {
            context.unregisterReceiver(myBroadcastReceiver);
        }

        public void sendBroadcast(String newData, boolean usePermission) {
            if(usePermission) {
                // [START android_broadcast_receiver_8_send_java]
                Intent intent = new Intent("com.example.snippets.ACTION_UPDATE_DATA");
                intent.putExtra("com.example.snippets.DATA", newData);
                intent.setPackage("com.example.snippets");
                context.sendBroadcast(intent);
                // [END android_broadcast_receiver_8_send_java]
            } else {
                Intent intent = new Intent("com.example.snippets.ACTION_UPDATE_DATA");
                intent.putExtra("com.example.snippets.DATA", newData);
                intent.setPackage("com.example.snippets");
                // [START android_broadcast_receiver_9_send_with_permission_java]
                context.sendBroadcast(intent, android.Manifest.permission.ACCESS_COARSE_LOCATION);
                // [END android_broadcast_receiver_9_send_with_permission_java]
            }
        }
    }
}

