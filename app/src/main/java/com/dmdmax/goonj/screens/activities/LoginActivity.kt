package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.screens.views.LoginView

class LoginActivity : BaseActivity(), LoginView.Listener {

    private lateinit var mView: LoginView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getLoginImpl(null);
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

    override fun goWithTelenor() {
        startActivity(Intent(this, SigninActivity::class.java))
    }

    override fun goWithEasypaisa() {
        mView.getToaster().printToast(this, "EP");
    }

    override fun goBack() {
        finish();
    }
}