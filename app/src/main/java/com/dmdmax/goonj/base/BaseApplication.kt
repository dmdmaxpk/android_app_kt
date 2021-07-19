package com.dmdmax.goonj.base

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process.myPid
import androidx.multidex.MultiDex
import com.dmdmax.goonj.network.ConnectivityProvider
import com.dmdmax.goonj.utility.Logger
import com.google.android.exoplayer2.util.Util
import com.google.firebase.FirebaseApp
import java.net.CookieHandler
import java.net.CookieManager

class BaseApplication: Application() {

    protected var userAgent: String? = null
    private var defaultCookieManager: CookieManager? = null

    override fun onCreate() {
        super.onCreate();

        // Initialize firebase app.
        FirebaseApp.initializeApp(applicationContext);
        Logger.println("Firebase App Initialized!");

        userAgent = Util.getUserAgent(this, "ExoPlayerDemo")

        if (isMainProcess()) {
            ConnectivityProvider.createProvider(this).subscribe()
        }
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
}