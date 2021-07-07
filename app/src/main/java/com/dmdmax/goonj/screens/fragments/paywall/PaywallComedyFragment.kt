package com.dmdmax.goonj.screens.fragments.paywall

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.PackageModel
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.activities.SigninActivity
import com.dmdmax.goonj.screens.views.PaywallBillingView
import com.dmdmax.goonj.storage.GoonjPrefs

class PaywallComedyFragment: BaseFragment(), PaywallBillingView, View.OnClickListener {

    companion object {
        val SLUG = "comedy";
        val ARGS_TAB = "tab";
        fun newInstance(args: Bundle?): PaywallComedyFragment {
            val fragment =  PaywallComedyFragment();
            if (args != null) {
                fragment.arguments = args;
            }
            return fragment;
        }

        val COMEDY_HARD_CODE_PACKAGE = "Comedy Weekly";
    }

    private lateinit var mSubscribeNow: LinearLayout;

    private lateinit var mMainLayout: ConstraintLayout;

    private lateinit var mPrefs: GoonjPrefs;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.paywall_comedy_fragment, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSubscribeNow = view.findViewById(R.id.telenor_number)

        mMainLayout = view.findViewById(R.id.main_layout);

        mPrefs = GoonjPrefs(context);
        mSubscribeNow.setOnClickListener(this)
    }

    override fun fetchPackages() {
        TODO("Not yet implemented")
    }

    override fun processBilling(source: String) {
        try{
            EventManager.getInstance(context!!).fireEvent(EventManager.Events.COMEDY_PAYWALL_PAY_CLICK);

            val intent = Intent(context, SigninActivity::class.java);
            intent.putExtra(PaywallGoonjFragment.ARG_SUBSCRIPTION_SOURCE, SLUG);
            intent.putExtra(PaywallGoonjFragment.ARG_PAYMENT_SOURCE, source);

            // Package for package name to shoot facebook events.
            val mPackage = PackageModel()
            mPackage.name = COMEDY_HARD_CODE_PACKAGE;

            intent.putExtra(PaywallGoonjFragment.ARGS_DEFAULT_PACKAGE, mPackage);

            startActivity(intent);
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            mSubscribeNow.id -> {
                processBilling(PaymentHelper.PAYMENT_TELENOR);
            }
        }
    }

}