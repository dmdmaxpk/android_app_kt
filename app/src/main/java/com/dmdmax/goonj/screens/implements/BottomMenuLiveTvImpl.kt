package com.dmdmax.goonj.screens.implements

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
import com.dmdmax.goonj.player.ExoPlayerManager
import com.dmdmax.goonj.screens.views.BottomMenuLiveTvView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import java.util.*


class BottomMenuLiveTvImpl: BaseObservableView<BottomMenuLiveTvView.Listener>, BottomMenuLiveTvView, View.OnClickListener {

    private lateinit var mChannelTitle: TextView;
    private lateinit var mHeader: FrameLayout;
    private lateinit var mPlayerManager: ExoPlayerManager;
    private lateinit var mPlayer: com.google.android.exoplayer2.ui.PlayerView;
    private lateinit var mPrefs: GoonjPrefs;

    private lateinit var mShare: LinearLayout;
    private lateinit var mRecommendedLiveChannels: RecyclerView;
    private lateinit var mList: ArrayList<Channel>;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.fragment_bottom_live, parent, false));
    }

    override fun initialize() {
        mChannelTitle = findViewById(R.id.channel_title);
        mShare = findViewById(R.id.share);
        mHeader = findViewById(R.id.header);
        mHeader.visibility = View.GONE;
        mRecommendedLiveChannels = findViewById(R.id.recommended_live_channels);

        setRecyclerView(mRecommendedLiveChannels)
        mShare.setOnClickListener(this);
        mPlayer = findViewById(R.id.video_view);
        mPrefs = GoonjPrefs(getContext());

        // init player
        mPlayerManager = ExoPlayerManager();
        mPlayerManager.init(getContext(), mPlayer);
        mList = getPrefs().getChannels();

        // Set bottom views
        mRecommendedLiveChannels.adapter = ChannelsCarouselListAdapter(mList, getContext(), object : ChannelsCarouselListAdapter.OnItemClickListener {
            override fun onClick(channel: Channel, position: Int) {
                play(channel);
            }
        })

        // Default play first channel
        if(mList.size > 0){
            play((mList.get(0)));
        }else{
            Logger.println("List size is zero")
        }
    }

    private fun play(channel: Channel) {
        mChannelTitle.text = channel.getName();

        // play media
        val model = MediaModel();
        model.setLive(true)
        model.setId(channel.getId());
        model.setUrl(Utility.generateLiveUrl(mPrefs.getGlobalBitrate()!!, channel.getHlsLink()));
        mPlayerManager.playMedia(model);
    }


    override fun onClick(v: View?) {
        when(v){
            mShare -> {
                getToaster().printToast(getContext(), "Share");
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