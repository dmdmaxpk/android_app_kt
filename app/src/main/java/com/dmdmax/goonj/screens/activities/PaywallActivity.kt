package com.dmdmax.goonj.screens.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.PaywallView

class PaywallActivity : BaseActivity(), PaywallView.Listener {

    companion object {
        var ARG_PAYWALL_SLUG: String = "paywallSlug";
    }

    private lateinit var mView: PaywallView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getPaywallViewImpl(null);
        setContentView(mView.getRootView());
        mView.initialize(PlayerActivity.ARGS_CHANNEL, intent.extras?.getString(ARG_PAYWALL_SLUG)!!);
    }

    override fun goBack() {
        onBackPressed();
    }
}