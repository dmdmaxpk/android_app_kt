package com.dmdmax.goonj.receivers;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dmdmax.goonj.R
import com.dmdmax.goonj.screens.activities.SplashActivity
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Logger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.lang.Exception
import java.net.URL

class NotificationListener : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Logger.println("FcmNotificationReceiver: "+remoteMessage.data);

        // Check if message contains a data payload.
        Logger.println("onMessageReceived")
        if (remoteMessage.data.isNotEmpty()) {
            Logger.println("Message data payload: " + remoteMessage.data)
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            var img: String? = null
            var url: String? = null
            if (remoteMessage.data.containsKey("img")) {
                img = remoteMessage.data["img"]
            }
            if (remoteMessage.data.containsKey("url")) {
                url = remoteMessage.data["url"]
            }
            sendNotification(title, body, img, url)
        }

        /*if(remoteMessage.getNotification() != null){
            Logger.println("ChannelId: "+remoteMessage.getNotification().getChannelId());
        }*/
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Logger.println("onDeletedMessages")
    }

    override fun onMessageSent(s: String) {
        super.onMessageSent(s)
        Logger.println("onMessageSent")
    }

    override fun onSendError(s: String, e: Exception) {
        super.onSendError(s, e)
        Logger.println("onSendError")
    }

    /*@Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sendRegistrationToServer(s);
        Logger.println("onNewToken");
    }*/
    private fun sendRegistrationToServer(token: String) {
        // TODO: Implement this method to send token to your app server.
        GoonjPrefs(this).setFcmToken(token)
    }

    private fun sendNotification(title: String?, body: String?, img: String?, url: String?) {
        val intent = Intent(this, SplashActivity::class.java)
        if (url != null) intent.data = Uri.parse(url)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        try {
            val notificationBuilder: NotificationCompat.Builder = if (img != null) {
                val imgUrl = URL(img)
                val image = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream())
                NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri).setStyle(
                        NotificationCompat.BigPictureStyle()
                            .bigPicture(image)
                    )
                    .setContentIntent(pendingIntent)
            } else {
                NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentTitle(title)
                    .setContentText(body)
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
}