package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface HomeView: ObservableView<HomeView.Listener> {

    interface Listener {
        fun goBack();
    }

    fun  initialize();
}