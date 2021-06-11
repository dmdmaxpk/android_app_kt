package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.SliderModel
import com.dmdmax.goonj.models.Video

interface LiveTvView: ObservableView<LiveTvView.Listener> {

    interface Listener {
        fun onSliderClick(model: SliderModel, position: Int);
        fun goBack();
        fun onChannelClick(channel: Channel, paywallSlug: String);
        fun onComedyClick(video: Video, paywallSlug: String);
        fun onBinjeeClick(video: Video, paywallSlug: String);
    }

    fun  initialize();
    fun  displaySlider();
    fun displayPrayerTime();
    fun cancelTimer();
}