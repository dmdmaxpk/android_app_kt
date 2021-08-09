package com.dmdmax.goonj.receivers

import com.dmdmax.goonj.utility.Logger
import com.onesignal.OSNotification
import com.onesignal.OneSignal

class OneSignalNotificationReceiver: OneSignal.NotificationReceivedHandler {

    override fun notificationReceived(notification: OSNotification) {
        Logger.println("One Signal Notification Received: $notification")
    }
}