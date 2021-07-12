package com.dmdmax.goonj.screens.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.firebase_events.EventManager
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

    override fun goBack() {
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        var isFull = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (isFull) {
            hideSystemUI()
        } else {
            showSystemUI()
        }

        mView.setFullscreen(isFull);
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val decorView = window.decorView
        decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }

    private fun showSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }
}