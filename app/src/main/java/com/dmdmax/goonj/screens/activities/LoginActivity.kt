package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.firebase_events.EventManager
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
        EventManager.getInstance(this).fireEvent("Login${EventManager.Events.VIEW}");
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
        mView.getToaster().printToast(this, "Support of easypaisa not available for now!");
    }

    override fun goBack() {
        finish();
    }
}