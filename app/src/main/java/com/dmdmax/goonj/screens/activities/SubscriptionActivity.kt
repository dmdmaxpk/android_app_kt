package com.dmdmax.goonj.screens.activities

import android.os.Bundle
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.screens.views.SubscriptionStatusView

class SubscriptionActivity : BaseActivity() {

    private lateinit var mView: SubscriptionStatusView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = getCompositionRoot().getViewFactory().getSubscriptinStatusView(null);
        setContentView(mView.getRootView());
    }

    override fun onResume() {
        super.onResume()
        mView.initialize();

        EventManager.getInstance(this).fireEvent("Subscription_Status${EventManager.Events.VIEW}");
    }
}