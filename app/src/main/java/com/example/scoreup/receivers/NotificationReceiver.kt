package com.example.scoreup.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.scoreup.R
import com.example.scoreup.ui.fragments.UpcomingFragment
import com.example.scoreup.utils.Constants

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val receiverIntent = Intent(context, UpcomingFragment::class.java)
        intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        val receiverPendingIntent = PendingIntent.getActivity(context, 0, receiverIntent, 0)

        val notification = NotificationCompat.Builder(context!!, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.score_up_logo)
            .setContentTitle("Reminder")
            .setContentText("Match Details Here")
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(receiverPendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(Constants.NOTIFICATION_ID, notification.build())
    }

}