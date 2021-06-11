package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView
import com.dmdmax.goonj.models.Video

interface VodView: ObservableView<VodView.Listener> {

    interface Listener {
        fun onBanner(video: Video);
        fun onVodClick(video: Video);
    }

    fun  initialize();
}