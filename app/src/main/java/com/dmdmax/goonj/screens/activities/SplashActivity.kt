package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.screens.views.SplashView
import com.dmdmax.goonj.utility.Logger

class SplashActivity : BaseActivity(), SplashView.Listener {

    private lateinit var mView: SplashView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        mView = getCompositionRoot().getViewFactory().getSplashViewImpl(null);
        setContentView(mView.getRootView());
        mView.getRemoteConfigs();
    }

    override fun onStart() {
        super.onStart();
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop();
        mView.unregisterListener(this);
    }

    override fun onCompleted() {
        Logger.println("onCompleted - SplashActivity");
        if(mView.getPrefs().isOtpValidated()){
            if(mView.getPrefs().isInterestedTopicDone()){
                startActivity(Intent(this, WelcomeActivity::class.java));
                finish();
            }else{
                startActivity(Intent(this, UserContentPrefsActivity::class.java));
                finish();
            }
        }else{
            startActivity(Intent(this, GetStartedActivity::class.java));
            finish();
        }
    }
}