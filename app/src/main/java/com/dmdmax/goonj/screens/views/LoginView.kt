package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface LoginView: ObservableView<LoginView.Listener> {

    interface Listener {
        fun goWithTelenor()
        fun goWithEasypaisa()
        fun goBack();
    }

    fun  initialize();
}