package com.dmdmax.goonj.base

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process.myPid
import androidx.multidex.MultiDex
import com.dmdmax.goonj.R
import com.dmdmax.goonj.network.ConnectivityProvider
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

        var appId = "9549eaf3-2fc1-4c28-be54-3d2d65db3bf3";

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
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(appId);

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();

        // Analytics Tracker init
        getGoogleAnalyticsTracker();
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