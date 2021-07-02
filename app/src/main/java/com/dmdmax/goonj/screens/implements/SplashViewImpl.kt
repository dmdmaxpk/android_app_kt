package com.dmdmax.goonj.screens.implements

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.screens.views.SplashView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings


class SplashViewImpl: BaseObservableView<SplashView.Listener>, SplashView {

    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig;
    private lateinit var mPrefs: GoonjPrefs;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_splash, parent, false));
    }

    override fun getRemoteConfigs() {
        mPrefs = GoonjPrefs(getContext());

        // HERE WE ARE TAKING THE REFERENCE OF OUR IMAGE
        // SO THAT WE CAN PERFORM ANIMATION USING THAT IMAGE
        val backgroundImage: ImageView = findViewById(R.id.splash_image)
        val slideAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.splash_screen)
        backgroundImage.startAnimation(slideAnimation)

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            mFirebaseRemoteConfig.setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().build())
            mFirebaseRemoteConfig.fetch(Constants.CONFIG_EXPIRATION_TIME_IN_SEC)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful()) {
                        Utility.setConstants(mFirebaseRemoteConfig)
                        Logger.println("Fetch successful")
                        mFirebaseRemoteConfig.activate();
                        workingCompleted();
                    }
                }.addOnFailureListener { e ->
                    Logger.println("Fetch failed: " + e.message)
                    e.printStackTrace()
                    Utility.setConstants(mFirebaseRemoteConfig)
                    workingCompleted();
                }
        }, 3000) // 3000 is the delayed time in milliseconds.
    }

    private fun workingCompleted() {
        for (listener in getListeners()) {
            listener.onCompleted()
        }
    }

}