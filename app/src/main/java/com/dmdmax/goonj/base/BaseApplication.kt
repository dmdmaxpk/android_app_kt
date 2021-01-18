package com.dmdmax.goonj.base

import android.app.Application
import com.dmdmax.goonj.utility.Logger
import com.google.firebase.FirebaseApp

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate();

        // Initialize firebase app.
        FirebaseApp.initializeApp(applicationContext);
        Logger.println("Firebase App Initialized!");
    }
}