package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.screens.views.UserContentPrefsView
import com.dmdmax.goonj.screens.views.VerificationView

class UserContentPrefsActivity : BaseActivity(), UserContentPrefsView.Listener {

    private lateinit var mView: UserContentPrefsView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getUserContentPrefsImpl(null);
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
        mView.getPrefs().setIsInterestedTopicDone(true);
        startActivity(Intent(this, WelcomeActivity::class.java));
        finish();
    }

    override fun goBack() {
        finish();
    }


}