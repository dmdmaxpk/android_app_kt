package com.dmdmax.goonj.screens.activities

import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.MediaModel
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.PlayerView
import com.dmdmax.goonj.utility.Utility

class PlayerActivity : BaseActivity(), PlayerView.Listener {

    companion object {
        var ARGS_CHANNELS: ArrayList<Channel> = arrayListOf();
        var ARGS_CHANNEL: Channel? = null;
        var ARGS_VIDEO: Video? = null;
    }

    private lateinit var mView: PlayerView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getPlayerViewImpl(null);
        setContentView(mView.getRootView());
        mView.getLogger().println("PlayerActivity - onCreate")
        init();
    }

    private fun init(){
        if(ARGS_CHANNEL != null){
            mView.getLogger().println("ARGS_CHANNEL")
            mView.initialize(MediaModel.getLiveMediaModel(ARGS_CHANNEL!!, mView.getPrefs().getGlobalBitrate()!!), ARGS_CHANNELS);
        }else if(ARGS_VIDEO != null){
            mView.getLogger().println("ARGS_VIDEO")
            mView.initialize(MediaModel.getVodMediaModel(ARGS_VIDEO!!, mView.getPrefs().getGlobalBitrate()!!), null);
        }
    }

    override fun onResume() {
        super.onResume()
        mView.startStreaming()
    }

    override fun onStart() {
        super.onStart();
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop();
        mView.unregisterListener(this);
        mView.pauseStreaming();
    }

    override fun onPause() {
        super.onPause()
        mView.pauseStreaming();
    }

    override fun onDestroy() {
        PlayerActivity.ARGS_VIDEO = null;
        PlayerActivity.ARGS_CHANNEL = null;
        super.onDestroy()
    }

    override fun goBack() {
        PlayerActivity.ARGS_VIDEO = null;
        PlayerActivity.ARGS_CHANNEL = null;
        onBackPressed()
    }

    override fun onLiveChannelClick(channel: Channel) {
        ARGS_CHANNEL = channel;
        if(mView.getPrefs().getSubscriptionStatus(PaywallGoonjFragment.SLUG) == PaymentHelper.Companion.PaymentStatus.STATUS_BILLED){
            getCompositionRoot().getViewFactory().toPlayerScreen(channel, mView.getPrefs().getChannels());
            finish()
        }else{
            getCompositionRoot().getViewFactory().toPaywallScreen(channel, PaywallGoonjFragment.SLUG);
            finish()
        }
    }

    override fun onVodClick(video: Video) {
        TODO("Not yet implemented")
    }
}