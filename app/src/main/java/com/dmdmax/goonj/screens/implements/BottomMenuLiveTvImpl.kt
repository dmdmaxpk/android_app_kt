package com.dmdmax.goonj.screens.implements

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.ChannelsCarouselListAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.MediaModel
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.player.ExoPlayerManager
import com.dmdmax.goonj.screens.activities.PlayerActivity
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.screens.views.BottomMenuLiveTvView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import java.util.*
import java.util.logging.Handler


class BottomMenuLiveTvImpl: BaseObservableView<BottomMenuLiveTvView.Listener>, BottomMenuLiveTvView, View.OnClickListener {

    private lateinit var mPlayerMainLayout: LinearLayout;
    private lateinit var mProgressBar: ProgressBar;
    private lateinit var mDummy: ImageView;

    private lateinit var mChannelTitle: TextView;
    private lateinit var mHeader: FrameLayout;
    private lateinit var mPlayerManager: ExoPlayerManager;
    private lateinit var mPlayer: com.google.android.exoplayer2.ui.PlayerView;
    private lateinit var mPrefs: GoonjPrefs;

    private lateinit var mShare: LinearLayout;
    private lateinit var mRecommendedLiveChannels: RecyclerView;
    private lateinit var mList: ArrayList<Channel>;

    private var mCurrentChannel: Channel? = null;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.fragment_bottom_live, parent, false));
    }

    override fun initialize() {
        mPlayerMainLayout = findViewById(R.id.player_main_layout);
        mProgressBar = findViewById(R.id.progress_bar);

        mChannelTitle = findViewById(R.id.channel_title);
        mShare = findViewById(R.id.share);
        mHeader = findViewById(R.id.header);
        mHeader.visibility = View.GONE;
        mRecommendedLiveChannels = findViewById(R.id.recommended_live_channels);

        setRecyclerView(mRecommendedLiveChannels)
        mShare.setOnClickListener(this);
        mPlayer = findViewById(R.id.video_view);

        mDummy = findViewById(R.id.dummy);
        mPrefs = GoonjPrefs(getContext());

        // init player
        mPlayerManager = ExoPlayerManager();
        mPlayerManager.init(getContext(), mPlayer);
        mPlayerManager.hideControllers();

        mList = getPrefs().getChannels();

        mDummy.setOnClickListener {
            play(mList[0]);
        }

        // Set listener
        mRecommendedLiveChannels.adapter = ChannelsCarouselListAdapter(mList, getContext(), object : ChannelsCarouselListAdapter.OnItemClickListener {
            override fun onClick(channel: Channel, position: Int) {
                play(channel);
            }
        })


        if(getPrefs().getSubscriptionStatus(PaywallGoonjFragment.SLUG) == PaymentHelper.Companion.PaymentStatus.STATUS_BILLED){
            mPlayerMainLayout.visibility = View.VISIBLE
            mProgressBar.visibility = View.GONE


            // Default play first channel
            if(mList.size > 0){
                play((mList.get(0)));
            }else{
                Logger.println("List size is zero")
            }
        }
        else{
            mPlayerMainLayout.visibility = View.GONE
            mProgressBar.visibility = View.VISIBLE

            if(getPrefs().getMsisdn(PaywallGoonjFragment.SLUG) != null && getPrefs().getMsisdn(PaywallGoonjFragment.SLUG)!!.isNotEmpty()){
                PaymentHelper(getContext(), PaymentHelper.PAYMENT_TELENOR).checkBillingStatus((getPrefs().getMsisdn(PaywallGoonjFragment.SLUG))!!, object: PaymentHelper.BillingStatusCheckListener{
                    override fun onStatus(code: Int, status: String) {
                        if(getPrefs().getSubscriptionStatus(PaywallGoonjFragment.SLUG) == PaymentHelper.Companion.PaymentStatus.STATUS_BILLED){
                            mPlayerMainLayout.visibility = View.VISIBLE
                            mProgressBar.visibility = View.GONE
                            mDummy.visibility = View.GONE;

                            // Default play first channel
                            if(mList.size > 0){
                                play((mList.get(0)));
                            }else{
                                Logger.println("List size is zero")
                            }

                        }else{
                            mPlayerMainLayout.visibility = View.VISIBLE
                            mProgressBar.visibility = View.GONE
                            mDummy.visibility = View.VISIBLE;
                        }
                    }

                })
            }
            else{
                mPlayerMainLayout.visibility = View.VISIBLE;
                mProgressBar.visibility = View.GONE;
                mDummy.visibility = View.VISIBLE;
            }
        }
    }

    private fun play(channel: Channel) {
        PlayerActivity.ARGS_CHANNEL = channel;
        PlayerActivity.ARGS_CHANNELS = mList;

        if(getPrefs().getSubscriptionStatus(PaywallGoonjFragment.SLUG) == PaymentHelper.Companion.PaymentStatus.STATUS_BILLED){
            // play
            mChannelTitle.text = channel.getName();
            mCurrentChannel = channel;

            // play media

            /*val model = MediaModel();
            model.isLive = true
            model.setId(channel.getId());
            model.setUrl(Utility.generateLiveUrl(mPrefs.getGlobalBitrate()!!, channel.getHlsLink()));*/
            mPlayerManager.playMedia(MediaModel.getLiveMediaModel(channel, mPrefs.getGlobalBitrate()!!));

            mDummy.visibility = View.GONE
        }else{
            // display paywall
            for(listener in getListeners()){
                listener.goToPaywall();
            }
        }
    }


    override fun onClick(v: View?) {
        when(v){
            mShare -> {
                if(mCurrentChannel != null){
                    Utility.fireShareIntent(getContext(), mCurrentChannel?.getName()!!.toLowerCase().replace(" ", "-"), mCurrentChannel?.getId()!!, true)
                }
            }
        }
    }

    override fun pauseStreaming(){
        mPlayerManager.pause();
    }

    private fun setRecyclerView(recyclerView: RecyclerView) {
        val manager = GridLayoutManager(getContext(), 4)
        recyclerView.layoutManager = manager
    }
}