package com.dmdmax.goonj.screens.activities

import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.models.PackageModel
import com.dmdmax.goonj.screens.views.PaywallView

class PaywallActivity : BaseActivity(), PaywallView.Listener {

    companion object {
        var ARG_PAYWALL_SLUG: String = "paywallSlug";
        var ARG_PAYWALL_PACKAGE = "paywallPackage";
    }

    private lateinit var mView: PaywallView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getPaywallViewImpl(null);
        setContentView(mView.getRootView());

        mView.initialize(PlayerActivity.ARGS_CHANNEL, intent.extras?.getString(ARG_PAYWALL_SLUG)!!, if(intent.extras?.containsKey(ARG_PAYWALL_PACKAGE) == true) intent.extras?.getSerializable(ARG_PAYWALL_PACKAGE)!! as PackageModel else null);
    }

    override fun goBack() {
        onBackPressed();
    }
}