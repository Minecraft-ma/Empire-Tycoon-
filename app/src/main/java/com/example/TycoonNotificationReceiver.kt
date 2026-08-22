package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class TycoonNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        val index = intent?.getIntExtra("notification_index", -1) ?: -1
        Log.d("TycoonReceiver", "Received alarm trigger for notification index: $index")
        val finalIndex = if (index >= 0) index else (0..29).random()
        TycoonNotificationHelper.sendDailyNotification(context, finalIndex)
    }
}
