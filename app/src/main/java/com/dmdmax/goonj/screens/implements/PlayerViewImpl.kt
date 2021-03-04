package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.ChannelsCarouselListAdapter
import com.dmdmax.goonj.adapters.HeadlinesCarouselListAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.MediaModel
import com.dmdmax.goonj.models.Video
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.player.ExoPlayerManager
import com.dmdmax.goonj.screens.views.PlayerView
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.JSONParser
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Utility
import com.google.android.exoplayer2.Player

class PlayerViewImpl: BaseObservableView<PlayerView.Listener>, PlayerView, View.OnClickListener {

    private lateinit var mTitle: TextView;
    private lateinit var mChannelTitle: TextView;
    private lateinit var mBack: ImageView;
    private lateinit var mPlayerManager: ExoPlayerManager;
    private lateinit var mPlayer: com.google.android.exoplayer2.ui.PlayerView;
    private lateinit var mPrefs: GoonjPrefs;

    private lateinit var mShare: LinearLayout;
    private lateinit var mRecommendedLiveChannels: RecyclerView;
    private lateinit var mRecommendedHeadlines: RecyclerView;
    private lateinit var mRecommendedEntertainmentChannels: RecyclerView;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_player, parent, false));
    }

    override fun initialize(id: String, name: String, thumbnail: String, hls: String, list: ArrayList<Channel>?) {
        mTitle = findViewById(R.id.title);
        mChannelTitle = findViewById(R.id.channel_title);
        mShare = findViewById(R.id.share);
        mBack = findViewById(R.id.back_arrow);
        mRecommendedLiveChannels = findViewById(R.id.recommended_live_channels);
        mRecommendedHeadlines = findViewById(R.id.recommended_headlines);
        mRecommendedEntertainmentChannels = findViewById(R.id.recommended_entertainment_channels);

        setRecyclerView(mRecommendedHeadlines);
        setRecyclerView(mRecommendedLiveChannels)
        setRecyclerView(mRecommendedEntertainmentChannels)

        mBack.setOnClickListener(this);
        mShare.setOnClickListener(this);

        mPlayer = findViewById(R.id.video_view);

        mTitle.text = name;
        mChannelTitle.text = name;
        mPlayerManager = ExoPlayerManager();
        mPrefs = GoonjPrefs(getContext());

        mPlayerManager.init(getContext());
        mPlayer.player = mPlayerManager.getPlayer();
        mPlayer.player.playWhenReady = true;
        mPlayer.useController = false;
        mPlayer.player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)

                // Loading Time
                if (playbackState == Player.STATE_READY) {
                    mPlayer.useController = true;
                    Logger.println("STATE_READY");
                    if(mPlayer.player.playWhenReady){
                        // Currently playing
                        mPlayer.findViewById<ImageButton>(R.id.exo_pause).visibility = View.VISIBLE;
                        mPlayer.findViewById<ImageButton>(R.id.exo_play).visibility = View.GONE;
                    }else{
                        // Stopped
                        mPlayer.findViewById<ImageButton>(R.id.exo_pause).visibility = View.GONE;
                        mPlayer.findViewById<ImageButton>(R.id.exo_play).visibility = View.VISIBLE;
                    }
                    mPlayer.findViewById<ProgressBar>(R.id.exo_buffering).visibility = View.GONE;
                }else if(playbackState == Player.STATE_BUFFERING) {
                    Logger.println("STATE_BUFFERING");
                    mPlayer.findViewById<ImageButton>(R.id.exo_pause).visibility = View.GONE;
                    mPlayer.findViewById<ImageButton>(R.id.exo_play).visibility = View.GONE;
                    mPlayer.findViewById<ProgressBar>(R.id.exo_buffering).visibility = View.VISIBLE;
                }else{
                    Logger.println("ELSE");
                    mPlayer.findViewById<ImageButton>(R.id.exo_pause).visibility = View.GONE;
                    mPlayer.findViewById<ImageButton>(R.id.exo_play).visibility = View.VISIBLE;
                    mPlayer.findViewById<ProgressBar>(R.id.exo_buffering).visibility = View.GONE;
                }
            }
        })


        val model = MediaModel();
        model.setLive(true)
        model.setId(id);
        model.setUrl(Utility.generateLiveUrl(mPrefs.getGlobalBitrate()!!, hls));
        mPlayerManager.playMedia(model);

        // Set bottom views
        if(list != null) mRecommendedLiveChannels.adapter = ChannelsCarouselListAdapter(list, getContext());
        displayHeadlines();
        displayEntertainmentChannels(list);
    }

    private fun displayHeadlines() {
        RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.VIDEO_BY_CATEGORY + "news", RestClient.Companion.Method.GET, null, object: NetworkOperationListener {
            override fun onSuccess(response: String?) {
                mRecommendedHeadlines.adapter = HeadlinesCarouselListAdapter(JSONParser.getFeed(response), getContext());
            }

            override fun onFailed(code: Int, reason: String?) {
                getLogger().println("onFailed:- "+reason);
            }
        }).exec();
    }
    private fun displayEntertainmentChannels(list: ArrayList<Channel>?) {
        val list = getPrefs().getChannels() as ArrayList<Channel>?;
        var entertainmentChannels: ArrayList<Channel> = list!!.filter { s -> s.getCategory() == "entertainment" } as ArrayList<Channel>
        mRecommendedEntertainmentChannels.adapter = ChannelsCarouselListAdapter(entertainmentChannels, getContext());
    }

    override fun onClick(v: View?) {
        when(v){
            mBack -> {
                mPlayer.player.playWhenReady = false;
                for (listener in getListeners()) {
                    listener.goBack();
                }
            }

            mShare -> {
                getToaster().printToast(getContext(), "Share");
            }
        }
    }

    override fun pauseStreaming(){
        mPlayerManager.pause();
    }

    private fun setRecyclerView(recyclerView: RecyclerView) {
        val horizontalLayoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = horizontalLayoutManager
    }
}