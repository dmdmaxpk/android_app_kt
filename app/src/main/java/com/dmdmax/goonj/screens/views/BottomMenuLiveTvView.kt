package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface BottomMenuLiveTvView: ObservableView<BottomMenuLiveTvView.Listener> {

    interface Listener {
        fun goBack();
        fun goToPaywall();
    }

    fun  initialize();
    fun pauseStreaming();
    fun setFullscreen(isFull: Boolean);
    fun updateNetworkState(isConnected: Boolean);
}