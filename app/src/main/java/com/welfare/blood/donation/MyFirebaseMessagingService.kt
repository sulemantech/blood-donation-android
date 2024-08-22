package com.welfare.blood.donation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.welfare.blood.donation.fragments.ReceivedRequestsFragment
import java.util.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Toast.makeText(this, "New message received", Toast.LENGTH_SHORT).show()

        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"]
            val message = remoteMessage.data["message"]
            val activity = remoteMessage.data["activity"]
            val extraData = remoteMessage.data["extraData"]

            saveNotificationLocally(title, message, activity, extraData)

            showNotification(title, message, activity, extraData)
        }

        remoteMessage.notification?.let {
            val title = it.title
            val message = it.body

            saveNotificationLocally(title, message, null, null)

            showNotification(title, message, null, null)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun saveNotificationLocally(title: String?, message: String?, activity: String?, extraData: String?) {
        val notification = NotificationEntity(
            title = title,
            message = message,
            activity = activity,
            extraData = extraData
        )

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(applicationContext)
            db.notificationDao().insertNotification(notification)
        }
    }

    private fun showNotification(title: String?, message: String?, activity: String?, extraData: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000)

        val intent = if (activity != null) {
            Intent(this, Class.forName(activity)).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("extraData", extraData)
            }
        } else {
            Intent(this, ReceivedRequestsFragment::class.java)
        }

        val openPendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = Intent(this, NotificationCancelReceiver::class.java).apply {
            putExtra("notification_id", notificationID)
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            cancelIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "FCM Notification"
            val channel = NotificationChannel("default", channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val sharedPreferences = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        val notificationCount = sharedPreferences.getInt("notification_count", 0) + 1

        val notificationBuilder = NotificationCompat.Builder(this, "default")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.noti)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .setNumber(notificationCount)  // Display the count on the notification
            .addAction(R.drawable.baseline_open_in_full_24, "Open", openPendingIntent)
            .addAction(R.drawable.baseline_cancel_24, "Cancel", cancelPendingIntent)

        notificationManager.notify(notificationID, notificationBuilder.build())

        sharedPreferences.edit().putInt("notification_count", notificationCount).apply()
    }
}
