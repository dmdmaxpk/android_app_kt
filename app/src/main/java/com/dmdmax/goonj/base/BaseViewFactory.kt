package com.dmdmax.goonj.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.screens.activities.PaywallActivity
import com.dmdmax.goonj.screens.activities.PlayerActivity
import com.dmdmax.goonj.screens.fragments.BottomMenuLiveTvFragment
import com.dmdmax.goonj.screens.fragments.HomeFragment
import com.dmdmax.goonj.screens.fragments.SettingsFragment
import com.dmdmax.goonj.screens.fragments.VodFragment
import com.dmdmax.goonj.screens.implements.*
import com.dmdmax.goonj.screens.views.*
import com.dmdmax.goonj.screens.views.GenericCategoryView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.FragmentFrameHelper

class BaseViewFactory {
    private var mLayoutInflater: LayoutInflater;
    private lateinit var mFragmentFrameHelper: FragmentFrameHelper

    constructor(layoutInflater: LayoutInflater, fragmentHelper: FragmentFrameHelper) {
        mLayoutInflater = layoutInflater
        mFragmentFrameHelper = fragmentHelper;
    }

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

    fun getPaywallViewImpl(parent: ViewGroup?): PaywallView {
        return PaywallViewImpl(mLayoutInflater, parent);
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
        return com.dmdmax.goonj.screens.implements.GenericCategoryViewImpl(mLayoutInflater, parent);
    }

    fun getBottomMenuLiveTvView(parent: ViewGroup): BottomMenuLiveTvView {
        return BottomMenuLiveTvImpl(mLayoutInflater, parent);
    }

    fun getBottomSettingsView(parent: ViewGroup): SettingsView {
        return SettingsViewImpl(mLayoutInflater, parent);
    }

    fun getVodImpl(parent: ViewGroup): VodView {
        return VodImpl(mLayoutInflater, parent);
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

    fun toVodPage(bundle: Bundle?) {
        mFragmentFrameHelper.replaceFragment(VodFragment.newInstance(bundle))
    }

    fun toBottomSettings(bundle: Bundle?) {
        mFragmentFrameHelper.replaceFragment(SettingsFragment.newInstance(bundle))
    }

    fun getPlayerViewImpl(parent: ViewGroup?): PlayerView {
        return PlayerViewImpl(mLayoutInflater, parent);
    }

    fun toPaywallScreen(channel: Channel?, paywallSlug: String) {
        if(channel != null)
            PlayerActivity.ARGS_CHANNEL = channel;

        val intent = Intent(mLayoutInflater.context, PaywallActivity::class.java);
        intent.putExtra(PaywallActivity.ARG_PAYWALL_SLUG, paywallSlug);
        (mLayoutInflater.context as BaseActivity).startActivity(intent);
    }

    fun toPaywallScreen(video: Video, paywallSlug: String) {
        PlayerActivity.ARGS_VIDEO = video;
        val intent = Intent(mLayoutInflater.context, PaywallActivity::class.java);
        intent.putExtra(PaywallActivity.ARG_PAYWALL_SLUG, paywallSlug);
        (mLayoutInflater.context as BaseActivity).startActivity(intent);
    }

    fun toPlayerScreen(channel: Channel?, channels: ArrayList<Channel>?) {
        if(channel != null){
            PlayerActivity.ARGS_CHANNEL = channel
            PlayerActivity.ARGS_VIDEO = null;
        }

        if(channels != null){
            PlayerActivity.ARGS_CHANNELS = channels;
        }else{
            PlayerActivity.ARGS_CHANNELS = GoonjPrefs(mLayoutInflater.context).getChannels();
        }

        val intent = Intent(mLayoutInflater.context, PlayerActivity::class.java)
        (mLayoutInflater.context as BaseActivity).startActivity(intent);
    }

    fun toPlayerScreen(video: Video?) {
        if(video != null){
            PlayerActivity.ARGS_VIDEO = video
            PlayerActivity.ARGS_CHANNEL = null;
        }

        val intent = Intent(mLayoutInflater.context, PlayerActivity::class.java)
        (mLayoutInflater.context as BaseActivity).startActivity(intent);
    }
}