package com.dmdmax.goonj.base

import android.app.Application
import android.content.Context
import android.os.Handler
import androidx.multidex.MultiDex
import com.dmdmax.goonj.utility.Logger
import com.google.android.exoplayer2.util.Util
import com.google.firebase.FirebaseApp
import java.net.CookieHandler
import java.net.CookieManager

class BaseApplication: Application() {

    protected var userAgent: String? = null
    private lateinit var mInstance: BaseApplication;
    private var defaultCookieManager: CookieManager? = null

    override fun onCreate() {
        super.onCreate();

        // Initialize firebase app.
        FirebaseApp.initializeApp(applicationContext);
        Logger.println("Firebase App Initialized!");

        this.mInstance = this
        userAgent = Util.getUserAgent(this, "ExoPlayerDemo")
    }

    fun setCookies() {
        defaultCookieManager = CookieManager()
        CookieHandler.setDefault(defaultCookieManager)
    }

    fun expireCookies() {
        if (defaultCookieManager != null) defaultCookieManager!!.getCookieStore().removeAll()
    }

    @Synchronized
    fun getInstance(): BaseApplication {
        return this.mInstance
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}