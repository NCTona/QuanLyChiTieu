package com.example.jetpackcompose.app.features.readNotificationTransaction

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class RestartServiceReceiver : BroadcastReceiver() {
    @SuppressLint("NewApi")
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, ReadTransactionNoti::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}