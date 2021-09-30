package com.dmdmax.goonj.base

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process.myPid
import androidx.multidex.MultiDex
import com.dmdmax.goonj.R
import com.dmdmax.goonj.network.ConnectivityProvider
import com.dmdmax.goonj.receivers.OneSignalNotificationReceiver
import com.dmdmax.goonj.utility.Logger
import com.facebook.ads.AudienceNetworkAds
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import com.google.firebase.FirebaseApp
import com.onesignal.OneSignal
import java.net.CookieHandler
import java.net.CookieManager

class BaseApplication: Application() {

    protected var userAgent: String? = null
    private var defaultCookieManager: CookieManager? = null
    private var mInstance: BaseApplication? = null

    override fun onCreate() {
        super.onCreate();
        mInstance = this;

        try{
            // Initialize firebase app.
            FirebaseApp.initializeApp(applicationContext);

            Logger.println("Firebase App Initialized!");
        }catch (e: Exception){
            e.printStackTrace()
            Logger.println("Exception: "+e.message)
        }

        userAgent = Util.getUserAgent(this, "ExoPlayerDemo")

        if (isMainProcess()) {
            ConnectivityProvider.createProvider(this).subscribe()
        }

        // Init One-Signal tool integration
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .setNotificationReceivedHandler(OneSignalNotificationReceiver())
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()

        // Analytics Tracker init
        getGoogleAnalyticsTracker();

        //Survicate.init(this)

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this)
    }

    fun setCookies() {
        defaultCookieManager = CookieManager()
        CookieHandler.setDefault(defaultCookieManager)
    }

    fun expireCookies() {
        if (defaultCookieManager != null) defaultCookieManager!!.getCookieStore().removeAll()
    }

    companion object {
        @Synchronized
        fun getInstance(): BaseApplication {
            return BaseApplication();
        }
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    // your package name is the same with your main process name
    private fun isMainProcess(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageName == getProcessName()
        } else packageName == getProcessNameLegacy()
    }

    // you can use this method to get current process name, you will get
    private fun getProcessNameLegacy(): String? {
        val mypid = myPid()
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = manager.runningAppProcesses
        for (info in infos) {
            if (info.pid == mypid) {
                return info.processName
            }
        }
        return null
    }

    fun getInstance(): BaseApplication? {
        return mInstance
    }

    fun getGoogleAnalyticsTracker(): Tracker? {
        return GoogleAnalytics.getInstance(this).newTracker(R.xml.analytics_tracker)
    }
}