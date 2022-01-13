package com.dmdmax.goonj.screens.activities

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.events.MessageEvent
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.MediaModel
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.network.CONNECTED
import com.dmdmax.goonj.network.DISCONNECTED
import com.dmdmax.goonj.network.NetWorkManger
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.PlayerView
import com.dmdmax.goonj.utility.Logger
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

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
        mView.getLogger().println("onCreate")
        init();
    }

    private fun init(){
        var firstNetworkStatusBroadcast = true;
        if(ARGS_CHANNEL != null){
            mView.getLogger().println("ARGS_CHANNEL")
            mView.initialize(MediaModel.getLiveMediaModel(ARGS_CHANNEL!!, mView.getPrefs().getGlobalBitrate()!!), ARGS_CHANNELS);
        }else if(ARGS_VIDEO != null){
            mView.getLogger().println("ARGS_VIDEO")
            mView.initialize(MediaModel.getVodMediaModel(ARGS_VIDEO!!, mView.getPrefs().getGlobalBitrate()!!), null);
        }

        NetWorkManger.networkStatus.observe(this, Observer {
            val event = MessageEvent(MessageEvent.EventNames.NETWORK_CONNECTED, null);
            when (it) {
                CONNECTED -> {
                    //Logger.println("Internet is connected")
                    event.value = true;
                }
                DISCONNECTED -> {
                    //Logger.println("Internet disconnected")
                    event.value = false;
                }
            }
            if(!firstNetworkStatusBroadcast) {
                EventBus.getDefault().post(event);
            }
            firstNetworkStatusBroadcast = false;
        })
    }

    override fun onResume() {
        super.onResume()
        mView.startStreaming()
    }

    override fun onStart() {
        super.onStart();
        mView.registerListener(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    override fun onStop() {
        super.onStop();
        mView.unregisterListener(this);
        mView.pauseStreaming();
        mView.releasePlayer()
        EventBus.getDefault().unregister(this);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                window.navigationBarColor = getColor(android.R.color.transparent)
                it.hide(WindowInsets.Type.systemBars())
            }
        } else {
            val decorView: View = this.window.decorView
            val uiOptions = decorView.systemUiVisibility
            var newUiOptions = uiOptions
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = newUiOptions
        }
    }

    private fun showSystemUI() {
        val decorView: View = this.window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        } else {

            val uiOptions = decorView.systemUiVisibility
            var newUiOptions = uiOptions
            newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_LOW_PROFILE.inv()
            newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
            newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
            newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE.inv()
            newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
            decorView.systemUiVisibility = newUiOptions
        }
    }

    @Subscribe
    fun onEventReceive(event: MessageEvent){
        if(event.name == MessageEvent.EventNames.NETWORK_CONNECTED){
            mView.updateNetworkState(event.value as Boolean);
        }
    }
}