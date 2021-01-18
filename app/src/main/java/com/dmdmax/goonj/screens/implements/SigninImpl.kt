package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.screens.views.LoginView
import com.dmdmax.goonj.screens.views.SigninView

class SigninImpl: BaseObservableView<SigninView.Listener>, SigninView, View.OnClickListener {

    private lateinit var mNext: FrameLayout;
    private lateinit var mBAckArrow: ImageButton;
    private lateinit var mScreenTitle: TextView;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_signin, parent, false));
    }

    override fun initialize() {
        mNext = findViewById(R.id.next);
        mNext.setOnClickListener(this);

        mBAckArrow = findViewById(R.id.back_arrow);
        mBAckArrow.setOnClickListener(this);

        mScreenTitle = findViewById(R.id.screen_title);
        mScreenTitle.text = getString(R.string.sing_in)
    }

    override fun onClick(v: View?) {
        when(v){
            mNext -> {
                for (listener in getListeners()) {
                    listener.next();
                }
            }

            mBAckArrow -> {
                for (listener in getListeners()) {
                    listener.goBack();
                }
            }
        }
    }
}