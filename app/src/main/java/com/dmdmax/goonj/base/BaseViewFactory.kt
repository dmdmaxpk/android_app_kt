package com.dmdmax.goonj.base

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dmdmax.goonj.screens.implements.GetStartedImpl
import com.dmdmax.goonj.screens.implements.LoginImpl
import com.dmdmax.goonj.screens.implements.SigninImpl
import com.dmdmax.goonj.screens.implements.SplashViewImpl
import com.dmdmax.goonj.screens.views.GetStartedView
import com.dmdmax.goonj.screens.views.LoginView
import com.dmdmax.goonj.screens.views.SigninView
import com.dmdmax.goonj.screens.views.SplashView

class BaseViewFactory {
    private lateinit var mLayoutInflater: LayoutInflater;

    constructor(layoutInflater: LayoutInflater) {
        mLayoutInflater = layoutInflater
    }

    fun getSplashViewImpl(parent: ViewGroup?): SplashView {
        return SplashViewImpl(mLayoutInflater, parent);
    }

    fun getStarted1Impl(parent: ViewGroup?): GetStartedView {
        return GetStartedImpl(mLayoutInflater, parent);
    }

    fun getLoginImpl(parent: ViewGroup?): LoginView {
        return LoginImpl(mLayoutInflater, parent);
    }

    fun getSigninImpl(parent: ViewGroup?): SigninView {
        return SigninImpl(mLayoutInflater, parent);
    }
}