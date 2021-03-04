package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView
import com.dmdmax.goonj.models.Channel

interface PlayerView: ObservableView<PlayerView.Listener> {

    interface Listener {
        fun goBack();
    }

    fun  initialize(id: String, name: String, thumbnail: String, hls: String, list: ArrayList<Channel>?);
    fun pauseStreaming();
}