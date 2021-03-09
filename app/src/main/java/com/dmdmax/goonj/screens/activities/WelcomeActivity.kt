package com.dmdmax.goonj.screens.activities

import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.screens.views.WelcomeView
import com.dmdmax.goonj.utility.Logger
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class WelcomeActivity : BaseActivity(), WelcomeView.Listener {

    private lateinit var mView: WelcomeView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getWelcomeView(null);
        setContentView(mView.getRootView());
        initialize()
    }

    private fun initialize(){
        mView.initialize();
        mView.bindBottomAdapter()
        onBottomClick(0);
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
        when(position) {
            0 -> {
                getCompositionRoot().getViewFactory().toHomePage(null);
            }

            1 -> {
                getCompositionRoot().getViewFactory().toBottomLiveTvPage(null);
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mView.getLogger().println("onRequestPermissionsResult - Activity")
    }
}