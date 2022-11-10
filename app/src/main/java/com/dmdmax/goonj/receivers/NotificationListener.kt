package com.dmdmax.goonj.receivers;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.core.app.NotificationCompat
import com.dmdmax.goonj.R
import com.dmdmax.goonj.models.Params
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.activities.SplashActivity
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.net.URL

class NotificationListener : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        /*Logger.println("NotificationListener: "+remoteMessage.data);

        // Check if message contains a data payload.
        Logger.println("onMessageReceived")
        if (remoteMessage.data.isNotEmpty()) {
            Logger.println("Message data payload: " + remoteMessage.data)
            val title = remoteMessage.data["title"]
            //val body = remoteMessage.data["body"]
            var img: String? = null
            var url: String? = null
            var notification_id: String? = null

            if (remoteMessage.data.containsKey("img")) {
                img = remoteMessage.data["img"]
            }
            if (remoteMessage.data.containsKey("url")) {
                url = remoteMessage.data["url"]
            }

            if (remoteMessage.data.containsKey("notification_id")) {
                notification_id = remoteMessage.data["notification_id"]
            }
            sendNotification(title, img, url, notification_id)
        }*/

    }

    private fun sendNotification(title: String?, img: String?, url: String?, notification_id: String?) {
        val intent = Intent(this, SplashActivity::class.java)
        if (url != null) {
            intent.data = Uri.parse(url)
        }

        val bundle = Bundle();
        if(notification_id != null){
            bundle.putString("notification_id", notification_id);
        }
        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        var pendingIntent: PendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        try {
            val notificationBuilder: NotificationCompat.Builder = if (img != null) {
                val imgUrl = URL(img)
                val image = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream())
                NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri).setStyle(
                        NotificationCompat.BigPictureStyle()
                            .bigPicture(image)
                    )
                    .setContentIntent(pendingIntent)
            } else {
                NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    getString(R.string.default_notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager?.createNotificationChannel(channel)
            }
            notificationManager?.notify(0, notificationBuilder.build())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onNewToken(token: String) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        Utility.sendRegistrationToServer(this@NotificationListener, token)
    }
}