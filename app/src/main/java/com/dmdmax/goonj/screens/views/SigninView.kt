package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface SigninView: ObservableView<SigninView.Listener> {

    interface Listener {
        fun next();
        fun goBack();
    }

    fun  initialize();
}