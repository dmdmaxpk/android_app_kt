package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.firebase_events.EventManager
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

        EventManager.getInstance(this).fireEvent(EventManager.Events.GET_STARTED_VIEW);
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
        EventManager.getInstance(this).fireEvent(EventManager.Events.GET_STARTED_SKIP);

        mView.getPrefs().setIsInterestedTopicDone(true);
        startActivity(Intent(this, WelcomeActivity::class.java));
        finish();
    }

    override fun next() {
        EventManager.getInstance(this).fireEvent(EventManager.Events.GET_STARTED_JOIN_NOW);

        mView.getPrefs().setIsInterestedTopicDone(true);
        startActivity(Intent(this, WelcomeActivity::class.java));
        finish();
    }
}