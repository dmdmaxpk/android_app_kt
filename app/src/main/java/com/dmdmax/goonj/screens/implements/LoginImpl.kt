package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.screens.views.LoginView

class LoginImpl: BaseObservableView<LoginView.Listener>, LoginView, View.OnClickListener {

    private lateinit var mTpLayout: LinearLayout;
    private lateinit var mEpLayout: LinearLayout;
    private lateinit var mBAckArrow: ImageButton;
    private lateinit var mScreenTitle: TextView;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.paywall_goonj_fragment, parent, false));
    }

    override fun initialize() {
        mTpLayout = findViewById(R.id.telenor_number);
        mTpLayout.setOnClickListener(this);

        mEpLayout = findViewById(R.id.ep_number);
        mEpLayout.setOnClickListener(this);

        mBAckArrow = findViewById(R.id.back_arrow);
        mBAckArrow.setOnClickListener(this);

        mScreenTitle = findViewById(R.id.screen_title);
        mScreenTitle.text = getString(R.string.login_with);
    }

    override fun onClick(v: View?) {
        when(v){
            mEpLayout -> {
                for (listener in getListeners()) {
                    listener.goWithEasypaisa();
                }
            }

            mTpLayout -> {
                for (listener in getListeners()) {
                    listener.goWithTelenor();
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