package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface BottomMenuLiveTvView: ObservableView<BottomMenuLiveTvView.Listener> {

    interface Listener {
        fun goBack();
    }

    fun  initialize();
    fun pauseStreaming();
}