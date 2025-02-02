package com.dmdmax.goonj.models

import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.implements.VodImpl
import com.dmdmax.goonj.utility.Utility

class MediaModel {

    var id: String? = null
    var title: String? = null
    var url: String? = null
    var filename: String? = null;
    var isLive = false;
    var slug: String? = null;
    var shouldMaintainState = false
    var category: String? = null;
    var subCategory: String? = null;
    var secondsLapsed: Long = 0;

    companion object {
        fun getVodMediaModel(video: Video, bitrate: String): MediaModel {
            val model = MediaModel();
            model.isLive = false;
            model.url =
                if(video.getSlug().equals(PaywallComedyFragment.SLUG) || video.getSlug().equals(PaywallGoonjFragment.SLUG)) video.getVideoUrl()
                else if(video.getCategory().equals(VodImpl.SLUG_DRAMA)) ""
                else if(video.getSlug().equals(VodImpl.SLUG_DRAMA)) Utility.generateVodUrl(bitrate, video.getFileName()!!)
                else Utility.generateVodUrl(bitrate, video.getFileName()!!);
            model.slug = video.getSlug();
            model.id = video.getId();
            model.title = video.getTitle();
            model.filename = video.getFileName();
            model.category = video.getCategory();
            model.subCategory = video.getSubCategory();
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