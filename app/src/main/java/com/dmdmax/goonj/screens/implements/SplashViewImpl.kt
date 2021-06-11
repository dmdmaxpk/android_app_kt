package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
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
    }

    private fun workingCompleted() {
        for (listener in getListeners()) {
            listener.onCompleted()
        }
    }

}