package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface SigninView: ObservableView<SigninView.Listener> {

    interface Listener {
        fun next(msisdn: String);
        fun goBack();
        fun help();
        fun viewPrivacyPolicy();
    }

    fun  initialize(subscriptionSource: String?, packageId: String?);
}