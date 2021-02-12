package com.dmdmax.goonj.screens.activities

import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.screens.views.WelcomeView

class WelcomeActivity : BaseActivity(), WelcomeView.Listener {

    private lateinit var mView: WelcomeView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getWelcomeView(null);
        setContentView(mView.getRootView());
        initialize();
    }

    private fun initialize(){
        mView.initialize();
        mView.bindBottomAdapter()
    }

    override fun onStart() {
        super.onStart();
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop();
        mView.unregisterListener(this);
    }

    override fun onSearchClick() {
        mView.getToaster().printToast(this, "Search");
    }

    override fun onUserClick() {
        mView.getToaster().printToast(this, "User");
    }

    override fun onBottomClick(position: Number) {

    }
}