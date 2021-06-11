package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.payments.BinjeePaymentHelper
import com.dmdmax.goonj.payments.ComedyPaymentHelper
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.WelcomeView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class WelcomeActivity : BaseActivity(), WelcomeView.Listener {

    private lateinit var mView: WelcomeView;
    private lateinit var mPrefs: GoonjPrefs;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getWelcomeView(null);
        setContentView(mView.getRootView());
        initialize()
    }

    private fun initialize(){
        mPrefs = GoonjPrefs(this);
        mView.initialize();
        mView.bindBottomAdapter()
        onBottomClick(0);
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
                getCompositionRoot().getViewFactory().toHomePage(null);
            }

            1 -> {
                getCompositionRoot().getViewFactory().toBottomLiveTvPage(null);
            }

            2 -> {
                getCompositionRoot().getViewFactory().toVodPage(null);
            }

            3 -> {
                getCompositionRoot().getViewFactory().toBottomSettings(null);
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mView.getLogger().println("onRequestPermissionsResult - Activity")
    }
}