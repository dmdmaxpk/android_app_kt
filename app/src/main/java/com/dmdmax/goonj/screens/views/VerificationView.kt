package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface VerificationView: ObservableView<VerificationView.Listener> {

    interface Listener {
        fun verify(otp: String);
        fun goBack();
    }

    fun  initialize(msisdn: String);
}