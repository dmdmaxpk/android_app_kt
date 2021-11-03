package com.dmdmax.goonj.screens.fragments.hometabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.SliderModel
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.activities.PlayerActivity
import com.dmdmax.goonj.screens.fragments.paywall.PaywallBinjeeFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallComedyFragment
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.implements.VodImpl
import com.dmdmax.goonj.screens.views.LiveTvView
import com.dmdmax.goonj.storage.GoonjPrefs

class LiveTvFragment: BaseFragment(), LiveTvView.Listener {

    private lateinit var mPrefs: GoonjPrefs;
    private lateinit var mView: LiveTvView;
    companion object {
        fun newInstance(args: Bundle?): LiveTvFragment {
            val fragment = LiveTvFragment();
            if (args != null) {
                fragment.arguments = args;
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = getCompositionRoot().getViewFactory().getLiveTvView(container!!);
        return mView.getRootView();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView.initialize();
        mView.displayPrayerTime();
        mPrefs = GoonjPrefs(context);
    }

    override fun onStart() {
        super.onStart()
        mView.registerListener(this);
    }

    override fun onStop() {
        super.onStop()
        mView.cancelTimer();
        mView.unregisterListener(this);
    }

    override fun onSliderClick(model: SliderModel, position: Int) {
        if(model.isLive()){
            PlayerActivity.ARGS_VIDEO = null;
            PlayerActivity.ARGS_CHANNEL = model.getChannel();
            PlayerActivity.ARGS_CHANNELS = mPrefs.getChannels();
            getCompositionRoot().getViewFactory().toPlayerScreen(model.getChannel(), mPrefs.getChannels());
        }else{
            val video = Video(Video.TileType.TILE_TYPE_THUMBNAIL);
            video.setTitle(model.getName());
            video.setThumbnail(model.getThumb());
            video.setId(model.getId());
            video.setKey(VodImpl.SLUG_DRAMA);
            video.setCategory(VodImpl.SLUG_DRAMA);
            getCompositionRoot().getViewFactory().toPlayerScreen(video);
        }
    }

    override fun goBack() {

    }

    override fun onChannelClick(channel: Channel, paywallSlug: String) {
        PlayerActivity.ARGS_CHANNEL = channel;
        if((mPrefs.getSubscriptionStatus(PaywallGoonjFragment.SLUG) == PaymentHelper.Companion.PaymentStatus.STATUS_BILLED || mPrefs.getSubscriptionStatus(PaywallGoonjFragment.SLUG) == PaymentHelper.Companion.PaymentStatus.STATUS_TRIAL) && mPrefs.getStreamable(PaywallGoonjFragment.SLUG)){
            getCompositionRoot().getViewFactory().toPlayerScreen(channel, null);
        }else{
            getCompositionRoot().getViewFactory().toPaywallScreen(channel, paywallSlug);
        }
    }

    override fun onComedyClick(video: Video, paywallSlug: String) {
        video.setSlug(PaywallComedyFragment.SLUG);
        PlayerActivity.ARGS_CHANNEL = null;
        PlayerActivity.ARGS_VIDEO = video;
        if(mPrefs.getSubscriptionStatus(paywallSlug) == PaymentHelper.Companion.PaymentStatus.STATUS_BILLED){
            getCompositionRoot().getViewFactory().toPlayerScreen(video);
        }else{
            getCompositionRoot().getViewFactory().toPaywallScreen(video, paywallSlug);
        }
    }

    override fun onBinjeeClick(video: Video, paywallSlug: String) {
        video.setSlug(PaywallBinjeeFragment.SLUG);
        PlayerActivity.ARGS_CHANNEL = null;
        PlayerActivity.ARGS_VIDEO = video;
        if(mPrefs.getSubscriptionStatus(paywallSlug) == PaymentHelper.Companion.PaymentStatus.STATUS_BILLED){
            getCompositionRoot().getViewFactory().toPlayerScreen(video);
        }else{
            getCompositionRoot().getViewFactory().toPaywallScreen(video, paywallSlug);
        }
    }
}