package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.PackageModel

interface PaywallView: ObservableView<PaywallView.Listener> {

    interface Listener {
        fun goBack();
    }

    fun  initialize(channel: Channel?, paywall: String, packageModel: PackageModel?);
}