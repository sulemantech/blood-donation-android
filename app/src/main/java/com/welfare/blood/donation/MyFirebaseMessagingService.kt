package com.welfare.blood.donation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.welfare.blood.donation.fragments.NotificationFragment
import com.welfare.blood.donation.fragments.ReceivedRequestsFragment
import java.util.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("abcccc", "Message data: ${remoteMessage.data}")
        // Handle both data and notification messages
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"]
            val message = remoteMessage.data["message"]
            val activity = remoteMessage.data["activity"]
            val extraData = remoteMessage.data["extraData"]

            Log.d("abcccc", "data notifications:$title$message")

            // Save notification to local Room database
            saveNotificationLocally(title, message, activity, extraData)

            // Display notification
            showNotification(title, message, activity, extraData)
        }


        remoteMessage.notification?.let {


            val title = it.title
            val message = it.body

            Log.e("abcccc", "notifications:$title$message")
            // Save notification to local Room database
            saveNotificationLocally(title, message, null, null)

            // Display notification
            showNotification(title, message, null, null)
        }
    }

    private fun saveNotificationLocally(title: String?, message: String?, activity: String?, extraData: String?) {
        val notification = NotificationEntity(
            title = title,
            message = message,
            activity = activity,
            extraData = extraData
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                db.notificationDao().insert(notification)
                Log.d("NotificationSave", "Notification saved: $notification")
            } catch (e: Exception) {
                Log.e("NotificationSaveError", "Failed to save notification", e)
            }
        }
    }

    private fun showNotification(
        title: String?,
        message: String?,
        activity: String?,
        extraData: String?
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000)

        val intent = Intent(this, SplashActivity::class.java).apply {
            putExtra("openFragment", "NotificationFragment")
            putExtra("extraData", extraData)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val openPendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, "default")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.splash)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "FCM Notification"
            val channel = NotificationChannel("default", channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationID, notificationBuilder.build())
    }
}
