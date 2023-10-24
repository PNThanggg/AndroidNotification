package com.pnt.local_notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pnt.local_notifications.databinding.ActivityMainBinding
import com.pnt.local_notifications.notification.ReminderBroadcast

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonShow.setOnClickListener {
            val intent = Intent(this@MainActivity, ReminderBroadcast::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext, 0, intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 2 * 1000, pendingIntent
            )
        }
    }
}