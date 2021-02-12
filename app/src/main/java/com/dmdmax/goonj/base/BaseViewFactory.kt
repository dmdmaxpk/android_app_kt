package com.dmdmax.goonj.base

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dmdmax.goonj.screens.implements.*
import com.dmdmax.goonj.screens.views.*

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

    fun getVerificationImpl(parent: ViewGroup?): VerificationView {
        return VerificationImpl(mLayoutInflater, parent);
    }

    fun getUserContentPrefsImpl(parent: ViewGroup?): UserContentPrefsView {
        return UserContentPrefsImpl(mLayoutInflater, parent);
    }

    fun getWelcomeView(parent: ViewGroup?): WelcomeView {
        return Welcomelmpl(mLayoutInflater, parent);
    }
}