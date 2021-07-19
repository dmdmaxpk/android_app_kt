package com.dmdmax.goonj.screens.activities

import android.content.Intent
import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.PackageModel
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.SigninView

class SigninActivity : BaseActivity(), SigninView.Listener {

    private lateinit var mView: SigninView;
    private lateinit var mPaywallSource: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getSigninImpl(null);
        setContentView(mView.getRootView());
        initialize();
        EventManager.getInstance(this).fireEvent("SignIn${EventManager.Events.VIEW}");
    }

    private fun initialize(){
        mPaywallSource =
            intent.extras?.getString(PaywallGoonjFragment.ARG_SUBSCRIPTION_SOURCE).toString();
        mView.initialize(mPaywallSource, intent.extras?.getSerializable(PaywallGoonjFragment.ARGS_DEFAULT_PACKAGE) as PackageModel);
    }

    override fun onStart() {
        super.onStart();
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop();
        mView.unregisterListener(this);
    }

    override fun next(msisdn: String) {
        val intent = Intent(this, VerificationActivity::class.java)

        intent.putExtra("msisdn", msisdn)
        intent.putExtra(PaywallGoonjFragment.ARG_SUBSCRIPTION_SOURCE, getIntent().extras?.getString(PaywallGoonjFragment.ARG_SUBSCRIPTION_SOURCE));
        intent.putExtra(PaywallGoonjFragment.ARG_PAYMENT_SOURCE, getIntent().extras?.getString(PaywallGoonjFragment.ARG_PAYMENT_SOURCE));
        intent.putExtra(PaywallGoonjFragment.ARGS_DEFAULT_PACKAGE, getIntent().extras?.getSerializable(PaywallGoonjFragment.ARGS_DEFAULT_PACKAGE) as PackageModel);
        startActivity(intent);
        finish();
    }

    override fun goBack() {
        finish();
    }

    override fun help() {
        mView.getToaster().printToast(this, "Help!");
    }

    override fun viewPrivacyPolicy() {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("page", "privacy-policy")
        intent.putExtra("slug", mPaywallSource)
        startActivity(intent);
    }
}