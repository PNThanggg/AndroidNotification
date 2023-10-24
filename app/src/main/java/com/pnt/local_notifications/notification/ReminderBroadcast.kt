package com.pnt.local_notifications.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pnt.local_notifications.R

class ReminderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Notify.build(context)
            .setTitle("Notification")
            .setContent("Content notification")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setLargeIcon("https://images.pexels.com/photos/139829/pexels-photo-139829.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=150&w=440")
            .largeCircularIcon()
            .setPicture("https://images.pexels.com/photos/1058683/pexels-photo-1058683.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940")
            .setColor(R.color.colorPrimary)
            .show()
    }
}