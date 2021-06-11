package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.os.Bundle
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.screens.views.GetStartedView

class GetStartedActivity: BaseActivity(), GetStartedView.Listener {

    private lateinit var mView: GetStartedView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getStarted1Impl(null);

        if(mView.getPrefs().isSkipped()){
            startActivity(Intent(this, UserContentPrefsActivity::class.java));
            finish();
        }else{
            setContentView(mView.getRootView());
            initialize();
        }
    }

    private fun initialize(){
        mView.initialize();
        mView.bindAdapter();
    }

    override fun onStart() {
        super.onStart();
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop();
        mView.unregisterListener(this);
    }

    override fun skip() {
        /*mView.getToaster().printToast(this, "Skipping intro...");
        mView.getPrefs().setIsSkipped(true);
        startActivity(Intent(this, UserContentPrefsActivity::class.java));
        finish();*/

        mView.getPrefs().setIsInterestedTopicDone(true);
        startActivity(Intent(this, WelcomeActivity::class.java));
        finish();
    }

    override fun next() {
        //startActivity(Intent(this, UserContentPrefsActivity::class.java));

        mView.getPrefs().setIsInterestedTopicDone(true);
        startActivity(Intent(this, WelcomeActivity::class.java));
        finish();
    }
}