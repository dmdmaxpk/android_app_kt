package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface SubscriptionStatusView: ObservableView<SubscriptionStatusView.Listener> {

    interface Listener {
        fun goBack();
    }

    fun  initialize();
}