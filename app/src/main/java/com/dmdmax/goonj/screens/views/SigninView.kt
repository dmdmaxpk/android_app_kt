package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView
import com.dmdmax.goonj.models.PackageModel

interface SigninView: ObservableView<SigninView.Listener> {

    interface Listener {
        fun next(msisdn: String);
        fun goBack();
        fun help();
        fun viewPrivacyPolicy();
    }

    fun  initialize(subscriptionSource: String?, packageModel: PackageModel);
}