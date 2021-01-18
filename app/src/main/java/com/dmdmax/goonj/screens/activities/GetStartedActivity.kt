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
        setContentView(mView.getRootView());
        initialize();
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
        mView.getToaster().printToast(this, "Skipping intro...");
        startActivity(Intent(this, LoginActivity::class.java));
    }

    override fun next() {
        startActivity(Intent(this, LoginActivity::class.java));
    }
}