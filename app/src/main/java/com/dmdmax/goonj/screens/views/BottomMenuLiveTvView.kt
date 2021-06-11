package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView
import java.nio.channels.Channel

interface BottomMenuLiveTvView: ObservableView<BottomMenuLiveTvView.Listener> {

    interface Listener {
        fun goBack();
        fun goToPaywall();
    }

    fun  initialize();
    fun pauseStreaming();
}