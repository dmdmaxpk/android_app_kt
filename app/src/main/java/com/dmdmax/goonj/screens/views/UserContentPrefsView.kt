package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface UserContentPrefsView: ObservableView<UserContentPrefsView.Listener> {

    interface Listener {
        fun next();
        fun goBack();
    }

    fun  initialize();
}