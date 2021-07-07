package com.dmdmax.goonj.models

import com.dmdmax.goonj.screens.activities.PlayerActivity
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.implements.VodImpl
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Utility

class MediaModel {

    public var id: String? = null
    public var title: String? = null
    public var url: String? = null
    public var filename: String? = null;
    public var isLive = false;
    public var slug: String? = null;
    public var shouldMaintainState = false
    public var category: String? = null;

    companion object {
        fun getVodMediaModel(video: Video, bitrate: String): MediaModel {
            val model = MediaModel();
            model.isLive = false;
            model.url =
                if(
                    video.getSlug().equals(PaywallComedyFragment.SLUG) ||
                    video.getSlug().equals(PaywallGoonjFragment.SLUG)) video.getVideoUrl()
                else if(video.getCategory().equals(VodImpl.SLUG_DRAMA)) ""
                else if(video.getSlug().equals(VodImpl.SLUG_DRAMA)) Utility.generateVodUrl(bitrate, video.getFileName()!!)
                else Utility.generateVodUrl(bitrate, video.getFileName()!!);
            model.slug = video.getSlug();
            model.id = video.getId();
            model.title = video.getTitle();
            model.filename = video.getFileName();
            model.category = video.getCategory();
            return model;
        }

        fun getLiveMediaModel(channel: Channel, bitrate: String): MediaModel {
            val model = MediaModel();
            model.isLive = true;
            model.url = Utility.generateLiveUrl(bitrate, channel.getHlsLink());
            model.id = channel.getId();
            model.title = channel.getName();
            model.category = "live"
            return model;
        }
    }
}