package com.dmdmax.goonj.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.dmdmax.goonj.screens.fragments.BottomMenuLiveTvFragment
import com.dmdmax.goonj.screens.fragments.HomeFragment
import com.dmdmax.goonj.screens.fragments.SettingsFragment
import com.dmdmax.goonj.screens.implements.*
import com.dmdmax.goonj.screens.views.*
import com.dmdmax.goonj.utility.FragmentFrameHelper

class BaseViewFactory {
    private var mLayoutInflater: LayoutInflater;
    private var mFragmentFrameHelper: FragmentFrameHelper

    constructor(layoutInflater: LayoutInflater, fragmentHelper: FragmentFrameHelper) {
        mLayoutInflater = layoutInflater
        mFragmentFrameHelper = fragmentHelper;
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

    fun getHomeView(parent: ViewGroup, childFragmentManager: FragmentManager): HomeView {
        return HomeViewImpl(mLayoutInflater, parent, childFragmentManager);
    }

    fun getLiveTvView(parent: ViewGroup): LiveTvView {
        return LiveTvImpl(mLayoutInflater, parent);
    }

    fun getGenericCategoryView(parent: ViewGroup): GenericCategoryView {
        return GenericCategoryImpl(mLayoutInflater, parent);
    }

    fun getBottomMenuLiveTvView(parent: ViewGroup): BottomMenuLiveTvView {
        return BottomMenuLiveTvImpl(mLayoutInflater, parent);
    }

    fun getBottomSettingsView(parent: ViewGroup): SettingsView {
        return SettingsViewImpl(mLayoutInflater, parent);
    }

    fun getSubscriptinStatusView(parent: ViewGroup?): SubscriptionStatusView {
        return SubscriptionStatusViewImpl(mLayoutInflater, parent);
    }

    fun toHomePage(bundle: Bundle?) {
        mFragmentFrameHelper.replaceFragment(HomeFragment.newInstance(bundle))
    }

    fun toBottomLiveTvPage(bundle: Bundle?) {
        mFragmentFrameHelper.replaceFragment(BottomMenuLiveTvFragment.newInstance(bundle))
    }

    fun toBottomSettings(bundle: Bundle?) {
        mFragmentFrameHelper.replaceFragment(SettingsFragment.newInstance(bundle))
    }

    fun getPlayerViewImpl(parent: ViewGroup?): PlayerView {
        return PlayerViewImpl(mLayoutInflater, parent);
    }
}