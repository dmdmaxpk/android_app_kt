package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView
import com.dmdmax.goonj.models.Channel

interface PaywallView: ObservableView<PaywallView.Listener> {

    interface Listener {
        fun goBack();
    }

    fun  initialize(channel: Channel?, paywall: String);
}