package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView

interface LiveTvView: ObservableView<LiveTvView.Listener> {

    interface Listener {
        fun goBack();
    }

    fun  initialize();
    fun  displaySlider();
    fun displayPrayerTime();
    fun cancelTimer();
}