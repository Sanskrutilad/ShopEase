package com.example.shopease

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class Notifications : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let  {
            sendNotification(it.title, it.body)
        }
    }
    private fun sendNotification(title: String?, message: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val channelId = if (title?.contains("Deal", ignoreCase = true) == true
        ) {
            "banner_channel"
        } else {
            "order_channel"
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title ?: "Baby Dino")
            .setContentText(message ?: "You have a new notification!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), builder.build())
    }



    override fun onNewToken(token: String) {
        // Send this token to your server if needed
    }
}

fun sendBannerNotification(context: Context, imageUrl: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) return  // Exit if permission not granted

    CoroutineScope(Dispatchers.IO).launch {
        val bitmap = getBitmapFromUrl(imageUrl)

        withContext(Dispatchers.Main) {
            val builder = NotificationCompat.Builder(context, "banner_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                .setContentTitle("🎉 New Deal!!!")
                .setContentText("Get it ASAP")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}

fun sendOrderNotification(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) return  // Exit if permission not granted

    val builder = NotificationCompat.Builder(context, "order_channel")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("🎉 Order Confirmed")
        .setContentText("Your order has been placed successfully!")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
}


private suspend fun getBitmapFromUrl(imageUrl: String): Bitmap? {
    return try {
        val input = URL(imageUrl).openStream()
        BitmapFactory.decodeStream(input)
    } catch (e: Exception) {
        null
    }
}

