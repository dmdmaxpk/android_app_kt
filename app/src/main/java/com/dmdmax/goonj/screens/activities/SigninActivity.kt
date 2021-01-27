package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.network.client.RestClient
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

    override fun next(msisdn: String) {
        val intent = Intent(this, VerificationActivity::class.java)
        intent.putExtra("msisdn", msisdn)
        startActivity(intent);
    }

    override fun goBack() {
        finish();
    }

    override fun help() {
        mView.getToaster().printToast(this, "Help!");
    }

    override fun viewPrivacyPolicy() {
        mView.getToaster().printToast(this, "Privacy Policy!");
    }
}