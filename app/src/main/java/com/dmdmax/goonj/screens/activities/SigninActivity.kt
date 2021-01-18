package com.dmdmax.goonj.screens.activities

import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.screens.views.LoginView
import com.dmdmax.goonj.screens.views.SigninView

class SigninActivity : BaseActivity(), SigninView.Listener {

    private lateinit var mView: SigninView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getSigninImpl(null);
        setContentView(mView.getRootView());
        initialize();
    }

    private fun initialize(){
        mView.initialize();
    }

    override fun onStart() {
        super.onStart();
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop();
        mView.unregisterListener(this);
    }

    override fun next() {
        mView.getToaster().printToast(this, "Next!");
    }

    override fun goBack() {
        finish();
    }
}