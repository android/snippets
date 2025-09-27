package com.example.android.views.notifications;

import android.app.Activity;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import com.example.android.views.R;

public class NotificationSnippets {
    private static final String CHANNEL_ID = "channel_id";
    private static final String textTitle = "Notification Title";
    private static final String textContent = "Notification Content";

    public static class MainActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // [START android_views_notifications_build_basic]
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(textTitle)
                    .setContentText(textContent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            // [END android_views_notifications_build_basic]
        }
    }
}
