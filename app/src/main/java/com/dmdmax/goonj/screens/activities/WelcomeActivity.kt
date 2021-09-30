package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
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
import com.dmdmax.goonj.utility.Logger
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
        if(mView.currentBottomIndex() == 1){
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