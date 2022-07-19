package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.events.MessageEvent
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.network.CONNECTED
import com.dmdmax.goonj.network.DISCONNECTED
import com.dmdmax.goonj.network.NetWorkManger
import com.dmdmax.goonj.payments.BinjeePaymentHelper
import com.dmdmax.goonj.payments.ComedyPaymentHelper
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.WelcomeView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.DeepLinkingManager
import com.dmdmax.goonj.utility.GoonjAdManager
import com.dmdmax.goonj.utility.Logger
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import org.greenrobot.eventbus.EventBus

class WelcomeActivity : BaseActivity(), WelcomeView.Listener {

    private lateinit var mView: WelcomeView;
    private lateinit var mPrefs: GoonjPrefs;
    private var mFullScreenListener: FullScreenListener? = null;

    interface FullScreenListener{
        fun onFullScreen(isFull: Boolean);
    }

    fun setFullScreenListener(fullScreenListener: FullScreenListener?){
        this.mFullScreenListener = fullScreenListener;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getWelcomeView(null);
        setContentView(mView.getRootView());
        initialize()
    }

    private fun initialize(){
        var firstNetworkStatusBroadcast = true;
        mPrefs = GoonjPrefs(this);
        mView.initialize();
        mView.bindBottomAdapter()
        onBottomClick(0);

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
        MobileAds.initialize(this);

        Handler().postDelayed(Runnable {
            processDeepLinks();
        }, 1000);
    }

    override fun onResume() {
        super.onResume()
        if(mPrefs.getMsisdn(PaywallGoonjFragment.SLUG) != null){
            PaymentHelper(this,null).checkBillingStatus(mPrefs.getMsisdn(PaywallGoonjFragment.SLUG)!!, null);
        }

        if(mPrefs.getMsisdn(PaywallComedyFragment.SLUG) != null){
            ComedyPaymentHelper(this).checkBillingStatus(mPrefs.getMsisdn(PaywallComedyFragment.SLUG)!!, null);
        }

        if(mPrefs.getMsisdn(PaywallBinjeeFragment.SLUG) != null){
            BinjeePaymentHelper(this).checkBillingStatus(mPrefs.getMsisdn(PaywallBinjeeFragment.SLUG)!!, null);
        }
    }

    override fun onBackPressed() {
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            return;
        }
        if(mView.currentBottomIndex() != 0){
            onBottomClick(mView.currentBottomIndex()-1)
        }else{
            finish()
        }
    }

    override fun onStart() {
        super.onStart();
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop();
        mView.unregisterListener(this);
    }

    override fun onSearchClick() {
        mView.getToaster().printToast(this, "Search");
    }

    override fun onUserClick() {
        ContextCompat.startActivity(this, Intent(this, MyProfileActivity::class.java), null);
    }

    override fun onBottomClick(position: Int) {
        mView.setCurrentBottomIndex(position)
        when(position) {
            0 -> {
                EventManager.getInstance(this).fireEvent(EventManager.Events.BOTTOM_MENU_HOME_CLICKED);
                getCompositionRoot().getViewFactory().toHomePage(null);
            }

            1 -> {
                EventManager.getInstance(this).fireEvent(EventManager.Events.BOTTOM_MENU_LIVE_CLICKED);
                getCompositionRoot().getViewFactory().toBottomLiveTvPage(null);
            }

            2 -> {
                EventManager.getInstance(this).fireEvent(EventManager.Events.BOTTOM_MENU_VOD_CLICKED);
                getCompositionRoot().getViewFactory().toVodPage(null);
            }

            3 -> {
                EventManager.getInstance(this).fireEvent(EventManager.Events.BOTTOM_MENU_MORE_CLICKED);
                getCompositionRoot().getViewFactory().toBottomSettings(null);
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mView.getLogger().println("onRequestPermissionsResult - Activity")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if(mView.currentBottomIndex() == 0 || mView.currentBottomIndex() == 1){
            super.onConfigurationChanged(newConfig)
            val isFull = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;

            if(isFull){
                hideSystemUI()
            }else{
                showSystemUI()
            }

            mView.setFullScreen(isFull)
            mFullScreenListener?.onFullScreen(isFull)
        }
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

    private fun processDeepLinks(){
        val action = intent.getStringExtra("action").toString();
        if(action == DeepLinkingManager.Mapper.OPEN_UN_SUB){
            startActivity(Intent(this@WelcomeActivity, SubscriptionActivity::class.java))
        }
    }
}