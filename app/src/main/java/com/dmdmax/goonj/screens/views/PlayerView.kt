package com.dmdmax.goonj.screens.views

import com.dmdmax.goonj.base.ObservableView
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.MediaModel
import com.dmdmax.goonj.models.Video

interface PlayerView: ObservableView<PlayerView.Listener> {

    interface Listener {
        fun goBack();
        fun onLiveChannelClick(channel: Channel);
        fun onVodClick(video: Video);
    }

    fun  initialize(model: MediaModel, list: ArrayList<Channel>?);
    fun pauseStreaming();
    fun startStreaming();
    fun getPlayer(): com.google.android.exoplayer2.ui.PlayerView;
    fun setFullscreen(isFull: Boolean)
}