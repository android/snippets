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

package com.example.compose.snippets.background

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.SystemClock
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle alarm
    }
}

// [START android_background_alarms_boot_receiver]
class SampleBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            // Set the alarm here.
        }
    }
}
// [END android_background_alarms_boot_receiver]

private class AlarmExamples(private val context: Context) {
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent
    private val requestId = 0
    private val intent = Intent(context, AlarmReceiver::class.java)

    fun cancelAlarmService() {
        // [START android_background_alarms_cancel_service]
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent =
            PendingIntent.getService(
                context,
                requestId,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
        // [END android_background_alarms_cancel_service]
    }

    fun elapsedRealtimeRepeating() {
        // [START android_background_alarms_elapsed_realtime_repeating]
        // Hopefully your alarm will have a lower frequency than this!
        alarmMgr?.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_HOUR,
            AlarmManager.INTERVAL_HALF_HOUR,
            alarmIntent
        )
        // [END android_background_alarms_elapsed_realtime_repeating]
    }

    fun elapsedRealtimeOneTime() {
        // [START android_background_alarms_elapsed_realtime_one_time]
        var alarmMgr: AlarmManager? = null
        lateinit var alarmIntent: PendingIntent
        // ...
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        alarmMgr?.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 60 * 1000,
            alarmIntent
        )
        // [END android_background_alarms_elapsed_realtime_one_time]
    }

    fun rtcRepeating() {
        // [START android_background_alarms_rtc_repeating]
        // Set the alarm to start at approximately 2:00 p.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 14)
        }

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )
        // [END android_background_alarms_rtc_repeating]
    }

    fun rtcRepeatingPrecise() {
        // [START android_background_alarms_rtc_repeating_precise]
        var alarmMgr: AlarmManager? = null
        lateinit var alarmIntent: PendingIntent
        // ...
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        // Set the alarm to start at 8:30 a.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 30)
        }

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.
        alarmMgr?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 20,
            alarmIntent
        )
        // [END android_background_alarms_rtc_repeating_precise]
    }

    fun cancelDirect() {
        // [START android_background_alarms_cancel_direct]
        // If the alarm has been set, cancel it.
        alarmMgr?.cancel(alarmIntent)
        // [END android_background_alarms_cancel_direct]
    }

    fun enableBootReceiver() {
        // [START android_background_alarms_enable_receiver]
        val receiver = ComponentName(context, SampleBootReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        // [END android_background_alarms_enable_receiver]
    }

    fun disableBootReceiver() {
        // [START android_background_alarms_disable_receiver]
        val receiver = ComponentName(context, SampleBootReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        // [END android_background_alarms_disable_receiver]
    }
}
